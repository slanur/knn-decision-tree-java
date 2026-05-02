package evaluation;

import classifier.BaseAlgorithm;
import model.ProcessedRecord;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Evaluator {

    
    public void evaluate(BaseAlgorithm algoritma, List<ProcessedRecord> testData, String algoritmAdi) {

        long baslangic = System.currentTimeMillis();

        int dogru = 0;
        for (ProcessedRecord kayit : testData) {
            String tahmin = algoritma.predict(kayit);
            if (tahmin.equals(kayit.getCategory())) {
                dogru++;
            }
        }

        long sure = System.currentTimeMillis() - baslangic;
        double dogruluk = (double) dogru / testData.size() * 100.0;

        System.out.println("\n========== " + algoritmAdi + " ==========");
        System.out.println("Doğru tahmin : " + dogru + " / " + testData.size());
        System.out.println("Doğruluk     : %" + String.format("%.2f", dogruluk));
        System.out.println("Süre         : " + sure + " ms");
    }

    
    public void confusionMatrix(BaseAlgorithm algoritma, List<ProcessedRecord> testData, String algoritmAdi) {

        Map<String, Map<String, Integer>> matris = new HashMap<>();

        for (ProcessedRecord kayit : testData) {
            String gercek = kayit.getCategory();
            String tahmin = algoritma.predict(kayit);

            matris.putIfAbsent(gercek, new HashMap<>());
            matris.get(gercek).put(tahmin, matris.get(gercek).getOrDefault(tahmin, 0) + 1);
        }

        System.out.println("\n--- " + algoritmAdi + " Confusion Matrix ---");
        for (Map.Entry<String, Map<String, Integer>> satirEntry : matris.entrySet()) {
            String gercek = satirEntry.getKey();
            for (Map.Entry<String, Integer> sutunEntry : satirEntry.getValue().entrySet()) {
                String tahmin = sutunEntry.getKey();
                int sayi      = sutunEntry.getValue();
                if (gercek.equals(tahmin)) {
                    System.out.println("✓ " + gercek + " → " + tahmin + " : " + sayi);
                } else {
                    System.out.println("✗ " + gercek + " → " + tahmin + " : " + sayi);
                }
            }
        }
    }

    public void karsilastir(BaseAlgorithm alg1, BaseAlgorithm alg2,
                             List<ProcessedRecord> testData,
                             String ad1, String ad2) {

        long bas1 = System.currentTimeMillis();
        double dogruluk1 = alg1.calculateAccuracy(testData);
        long sure1 = System.currentTimeMillis() - bas1;

        long bas2 = System.currentTimeMillis();
        double dogruluk2 = alg2.calculateAccuracy(testData);
        long sure2 = System.currentTimeMillis() - bas2;

        System.out.println("\nKARŞILAŞTIRMA");
        System.out.printf("%-25s %-15s %-15s%n", "Algoritma", "Doğruluk", "Süre");
        System.out.println("-------------------------------------------");
        System.out.printf("%-25s %%%s%-10s %s ms%n", ad1, String.format("%.2f", dogruluk1), "", sure1);
        System.out.printf("%-25s %%%s%-10s %s ms%n", ad2, String.format("%.2f", dogruluk2), "", sure2);

        String kazanan = dogruluk1 >= dogruluk2 ? ad1 : ad2;
        System.out.println("\nDaha doğru algoritma: " + kazanan);
    }
}