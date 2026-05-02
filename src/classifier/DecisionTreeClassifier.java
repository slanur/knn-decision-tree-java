package classifier;

import model.ProcessedRecord;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DecisionTreeClassifier extends BaseAlgorithm {

    private int maxDerinlik;
    private Node kok;

    public DecisionTreeClassifier(int maxDerinlik) {
        this.maxDerinlik = maxDerinlik;
    }

    private static class Node {
        boolean yaprakMi;
        String kategori;     
        int ozellikIndex;     
        double esik;          
        Node solDal;          
        Node sagDal;          

        
        Node(String kategori) {
            this.yaprakMi = true;
            this.kategori = kategori;
        }


        Node(int ozellikIndex, double esik) {
            this.yaprakMi     = false;
            this.ozellikIndex = ozellikIndex;
            this.esik         = esik;
        }
    }

    //train
    @Override
    public void train(List<ProcessedRecord> trainingData) {
        this.kok = agacOlustur(trainingData, 0);
        System.out.println("Decision Tree eğitildi. Eğitim verisi: " + trainingData.size() + " kayıt");
    }

    //predict
    @Override
    public String predict(ProcessedRecord kayit) {
        return dugumTahmin(kok, kayit);
    }

    private String dugumTahmin(Node dugum, ProcessedRecord kayit) {
        if (dugum.yaprakMi) return dugum.kategori;

        double deger = ozellikAl(kayit, dugum.ozellikIndex);
        if (deger <= dugum.esik) {
            return dugumTahmin(dugum.solDal, kayit);
        } else {
            return dugumTahmin(dugum.sagDal, kayit);
        }
    }

    //ağaç oluşturma(recursive)
    private Node agacOlustur(List<ProcessedRecord> veri, int derinlik) {

        if (veri.isEmpty() || derinlik >= maxDerinlik) {
            return new Node(enCokTekrarlanan(veri));
        }

        
        if (hepsiAyniKategori(veri)) {
            return new Node(veri.get(0).getCategory());
        }

        
        int enIyiOzellik = -1;
        double enIyiEsik  = 0;
        double enIyiGini  = Double.MAX_VALUE;

        for (int ozellik = 0; ozellik < 3; ozellik++) {
            for (ProcessedRecord kayit : veri) {
                double esik = ozellikAl(kayit, ozellik);
                double gini = giniHesapla(veri, ozellik, esik);
                if (gini < enIyiGini) {
                    enIyiGini    = gini;
                    enIyiOzellik = ozellik;
                    enIyiEsik    = esik;
                }
            }
        }

       
        List<ProcessedRecord> sol = new ArrayList<>();
        List<ProcessedRecord> sag = new ArrayList<>();
        for (ProcessedRecord kayit : veri) {
            if (ozellikAl(kayit, enIyiOzellik) <= enIyiEsik) {
                sol.add(kayit);
            } else {
                sag.add(kayit);
            }
        }

        
        if (sol.isEmpty() || sag.isEmpty()) {
            return new Node(enCokTekrarlanan(veri));
        }

        
        Node dugum = new Node(enIyiOzellik, enIyiEsik);
        dugum.solDal = agacOlustur(sol, derinlik + 1);
        dugum.sagDal = agacOlustur(sag, derinlik + 1);
        return dugum;
    }

    //yardımcı Metodlar

    private double ozellikAl(ProcessedRecord kayit, int index) {
        switch (index) {
            case 0: return kayit.getGenderEncoded();
            case 1: return kayit.getLineNetNorm();
            case 2: return kayit.getBrandCodeNorm();
            default: return 0;
        }
    }

    private double giniHesapla(List<ProcessedRecord> veri, int ozellik, double esik) {
        List<ProcessedRecord> sol = new ArrayList<>();
        List<ProcessedRecord> sag = new ArrayList<>();

        for (ProcessedRecord kayit : veri) {
            if (ozellikAl(kayit, ozellik) <= esik) sol.add(kayit);
            else sag.add(kayit);
        }

        int toplam = veri.size();
        double giniSol = giniImpurity(sol);
        double giniSag = giniImpurity(sag);

        return ((double) sol.size() / toplam) * giniSol +
               ((double) sag.size() / toplam) * giniSag;
    }

    private double giniImpurity(List<ProcessedRecord> veri) {
        if (veri.isEmpty()) return 0;

        Map<String, Integer> sayac = new HashMap<>();
        for (ProcessedRecord kayit : veri) {
            sayac.put(kayit.getCategory(), sayac.getOrDefault(kayit.getCategory(), 0) + 1);
        }

        double gini = 1.0;
        for (int sayi : sayac.values()) {
            double oran = (double) sayi / veri.size();
            gini -= oran * oran;
        }
        return gini;
    }

    private boolean hepsiAyniKategori(List<ProcessedRecord> veri) {
        String ilk = veri.get(0).getCategory();
        for (ProcessedRecord kayit : veri) {
            if (!kayit.getCategory().equals(ilk)) return false;
        }
        return true;
    }

    private String enCokTekrarlanan(List<ProcessedRecord> veri) {
        if (veri.isEmpty()) return "";
        Map<String, Integer> sayac = new HashMap<>();
        for (ProcessedRecord kayit : veri) {
            sayac.put(kayit.getCategory(), sayac.getOrDefault(kayit.getCategory(), 0) + 1);
        }
        return sayac.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .get().getKey();
    }
}