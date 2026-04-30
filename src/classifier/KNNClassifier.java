package classifier;

import model.ProcessedRecord;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KNNClassifier extends BaseAlgorithm {

    private int k;
    private List<ProcessedRecord> egitimVerisi;

    public KNNClassifier(int k) {
        this.k = k;
        this.egitimVerisi = new ArrayList<>();
    }

    @Override
    public void train(List<ProcessedRecord> trainingData) {
        this.egitimVerisi = trainingData;
        System.out.println("KNN eğitildi. Eğitim verisi: " + egitimVerisi.size() + " kayıt, K=" + k);
    }

    @Override
    public String predict(ProcessedRecord yeniKayit) {

        List<KomsuBilgi> komsuListesi = new ArrayList<>();
        for (ProcessedRecord egitimKaydi : egitimVerisi) {
            double mesafe = oklidMesafesi(yeniKayit, egitimKaydi);
            komsuListesi.add(new KomsuBilgi(mesafe, egitimKaydi.getCategory()));
        }

        Collections.sort(komsuListesi);

        Map<String, Integer> kategoriler = new HashMap<>();
        for (int i = 0; i < k && i < komsuListesi.size(); i++) {
            String kategori = komsuListesi.get(i).kategori;
            kategoriler.put(kategori, kategoriler.getOrDefault(kategori, 0) + 1);
        }

        String enCokTekrarlanan = "";
        int maxSayi = 0;
        for (Map.Entry<String, Integer> entry : kategoriler.entrySet()) {
            if (entry.getValue() > maxSayi) {
                maxSayi = entry.getValue();
                enCokTekrarlanan = entry.getKey();
            }
        }

        return enCokTekrarlanan;
    }

    private double oklidMesafesi(ProcessedRecord a, ProcessedRecord b) {
        double fark1 = a.getGenderEncoded()  - b.getGenderEncoded();
        double fark2 = a.getLineNetNorm()    - b.getLineNetNorm();
        double fark3 = a.getBrandCodeNorm()  - b.getBrandCodeNorm();
        return Math.sqrt(fark1 * fark1 + fark2 * fark2 + fark3 * fark3);
    }

    private static class KomsuBilgi implements Comparable<KomsuBilgi> {
        double mesafe;
        String kategori;

        KomsuBilgi(double mesafe, String kategori) {
            this.mesafe   = mesafe;
            this.kategori = kategori;
        }

        @Override
        public int compareTo(KomsuBilgi diger) {
            return Double.compare(this.mesafe, diger.mesafe);
        }
    }
}