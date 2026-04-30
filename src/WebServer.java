import classifier.DecisionTreeClassifier;
import classifier.KNNClassifier;
import com.google.gson.Gson;
import io.DataLoader;
import model.ProcessedRecord;
import model.UserRecord;
import preprocessing.PreProcessor;

import javax.servlet.MultipartConfigElement;
import javax.servlet.http.Part;
import java.io.*;
import java.nio.file.*;
import java.util.*;

import static spark.Spark.*;

public class WebServer {

    static List<ProcessedRecord> egitim    = new ArrayList<>();
    static List<ProcessedRecord> test      = new ArrayList<>();
    static List<ProcessedRecord> tumVeri   = new ArrayList<>();
    static boolean veriYuklendi            = false;
    static Gson gson                       = new Gson();

    public static void start() {

        staticFiles.location("/public");
        port(4567);

        before((req, res) -> res.header("Access-Control-Allow-Origin", "*"));

        // ── Veri bilgisi ──────────────────────────────────────────────────
        get("/api/info", (req, res) -> {
            res.type("application/json");
            Map<String, Object> bilgi = new HashMap<>();
            bilgi.put("toplamKayit", tumVeri.size());
            bilgi.put("egitimKayit", egitim.size());
            bilgi.put("testKayit",   test.size());
            bilgi.put("veriYuklendi", veriYuklendi);
            return gson.toJson(bilgi);
        });

        // ── Dosya yükleme ─────────────────────────────────────────────────
        post("/api/yukle", (req, res) -> {
            res.type("application/json");
            try {
                req.raw().setAttribute("org.eclipse.jetty.multipartConfig",
                    new MultipartConfigElement(System.getProperty("java.io.tmpdir")));

                Part dosyaPart = req.raw().getPart("dosya");
                if (dosyaPart == null) {
                    return gson.toJson(Map.of("hata", "Dosya bulunamadı"));
                }

                // Geçici dosyaya kaydet
                String geciciYol = System.getProperty("java.io.tmpdir") + "/yuklenenveri.xlsx";
                try (InputStream is = dosyaPart.getInputStream()) {
                    Files.copy(is, Paths.get(geciciYol), StandardCopyOption.REPLACE_EXISTING);
                }

                // Veriyi yükle ve işle
                DataLoader loader = new DataLoader();
                List<UserRecord> kayitlar = loader.loadData(geciciYol);

                if (kayitlar.isEmpty()) {
                    return gson.toJson(Map.of("hata", "Veri okunamadı. Dosya formatını kontrol edin."));
                }

                PreProcessor processor = new PreProcessor();
                tumVeri = processor.process(kayitlar);
                Collections.shuffle(tumVeri);

                int trainSize = (int)(tumVeri.size() * 0.8);
                egitim = new ArrayList<>(tumVeri.subList(0, trainSize));
                test   = new ArrayList<>(tumVeri.subList(trainSize, tumVeri.size()));
                veriYuklendi = true;

                Map<String, Object> sonuc = new HashMap<>();
                sonuc.put("basari", true);
                sonuc.put("toplamKayit", tumVeri.size());
                sonuc.put("egitimKayit", egitim.size());
                sonuc.put("testKayit",   test.size());
                return gson.toJson(sonuc);

            } catch (Exception e) {
                return gson.toJson(Map.of("hata", e.getMessage()));
            }
        });

        // ── KNN çalıştır ──────────────────────────────────────────────────
        post("/api/knn", (req, res) -> {
            res.type("application/json");
            if (!veriYuklendi) return gson.toJson(Map.of("hata", "Önce veri yükleyin"));

            Map<String, Object> body = gson.fromJson(req.body(), Map.class);
            int k = ((Double) body.getOrDefault("k", 5.0)).intValue();

            KNNClassifier knn = new KNNClassifier(k);
            knn.train(egitim);

            long baslangic = System.currentTimeMillis();
            int dogru = 0;
            Map<String, Map<String, Integer>> matris = new HashMap<>();

            for (ProcessedRecord kayit : test) {
                String tahmin = knn.predict(kayit);
                String gercek = kayit.getCategory();
                if (tahmin.equals(gercek)) dogru++;
                matris.putIfAbsent(gercek, new HashMap<>());
                matris.get(gercek).put(tahmin, matris.get(gercek).getOrDefault(tahmin, 0) + 1);
            }

            long sure = System.currentTimeMillis() - baslangic;
            double dogruluk = (double) dogru / test.size() * 100.0;

            Map<String, Double> kategoriDogruluk = kategoriDogrulukHesapla(matris);

            Map<String, Object> sonuc = new HashMap<>();
            sonuc.put("algoritma",        "KNN (K=" + k + ")");
            sonuc.put("dogruluk",         Math.round(dogruluk * 100.0) / 100.0);
            sonuc.put("sure",             sure);
            sonuc.put("dogru",            dogru);
            sonuc.put("toplam",           test.size());
            sonuc.put("konfuzyonMatrisi", matris);
            sonuc.put("kategoriDogruluk", kategoriDogruluk);
            return gson.toJson(sonuc);
        });

        // ── Decision Tree çalıştır ────────────────────────────────────────
        post("/api/dt", (req, res) -> {
            res.type("application/json");
            if (!veriYuklendi) return gson.toJson(Map.of("hata", "Önce veri yükleyin"));

            Map<String, Object> body = gson.fromJson(req.body(), Map.class);
            int maxDerinlik = ((Double) body.getOrDefault("maxDerinlik", 10.0)).intValue();

            DecisionTreeClassifier dt = new DecisionTreeClassifier(maxDerinlik);
            dt.train(egitim);

            long baslangic = System.currentTimeMillis();
            int dogru = 0;
            Map<String, Map<String, Integer>> matris = new HashMap<>();

            for (ProcessedRecord kayit : test) {
                String tahmin = dt.predict(kayit);
                String gercek = kayit.getCategory();
                if (tahmin.equals(gercek)) dogru++;
                matris.putIfAbsent(gercek, new HashMap<>());
                matris.get(gercek).put(tahmin, matris.get(gercek).getOrDefault(tahmin, 0) + 1);
            }

            long sure = System.currentTimeMillis() - baslangic;
            double dogruluk = (double) dogru / test.size() * 100.0;

            Map<String, Double> kategoriDogruluk = kategoriDogrulukHesapla(matris);

            Map<String, Object> sonuc = new HashMap<>();
            sonuc.put("algoritma",        "Decision Tree (maxDerinlik=" + maxDerinlik + ")");
            sonuc.put("dogruluk",         Math.round(dogruluk * 100.0) / 100.0);
            sonuc.put("sure",             sure);
            sonuc.put("dogru",            dogru);
            sonuc.put("toplam",           test.size());
            sonuc.put("konfuzyonMatrisi", matris);
            sonuc.put("kategoriDogruluk", kategoriDogruluk);
            return gson.toJson(sonuc);
        });

        System.out.println("\n✅ Sunucu başlatıldı → http://localhost:4567");
    }

    private static Map<String, Double> kategoriDogrulukHesapla(Map<String, Map<String, Integer>> matris) {
        Map<String, Double> kategoriDogruluk = new HashMap<>();
        for (Map.Entry<String, Map<String, Integer>> entry : matris.entrySet()) {
            String kat      = entry.getKey();
            int toplamKat   = entry.getValue().values().stream().mapToInt(Integer::intValue).sum();
            int dogruKat    = entry.getValue().getOrDefault(kat, 0);
            kategoriDogruluk.put(kat, toplamKat > 0 ? (double) dogruKat / toplamKat * 100 : 0);
        }
        return kategoriDogruluk;
    }
}