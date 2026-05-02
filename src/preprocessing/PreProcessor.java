package preprocessing;

import model.ProcessedRecord;
import model.UserRecord;

import java.util.ArrayList;
import java.util.List;

public class PreProcessor {

    
    private double minLineNet, maxLineNet;
    private double minBrandCode, maxBrandCode;

    //UserRecord listesini alır, ProcessedRecord listesi döndürür
    public List<ProcessedRecord> process(List<UserRecord> kayitlar) {

        //1-Min-Max değerlerini bul
        minMaxHesapla(kayitlar);

        //2-Her kaydı dönüştür
        List<ProcessedRecord> islenmisList = new ArrayList<>();

        for (UserRecord kayit : kayitlar) {

            //Encoding: E-1.0 K-0.0
            double genderEncoded = kayit.getGender().equalsIgnoreCase("E") ? 1.0 : 0.0;

            //Normalizasyon
            double lineNetNorm   = normalize(kayit.getLineNetTotal(), minLineNet, maxLineNet);
            double brandCodeNorm = normalize(kayit.getBrandCode(), minBrandCode, maxBrandCode);

            //yeni ProcessedRecord oluştur
            ProcessedRecord islenmis = new ProcessedRecord(
                genderEncoded,
                lineNetNorm,
                brandCodeNorm,
                kayit.getCategory()
            );

            islenmisList.add(islenmis);
        }

        System.out.println("Ön işleme tamamlandı. İşlenen kayıt: " + islenmisList.size());
        return islenmisList;
    }


    //yardımcı metodlar
    // Tüm kayıtlar üzerinde min ve max değerlerini hesapla
    private void minMaxHesapla(List<UserRecord> kayitlar) {
        minLineNet   = Double.MAX_VALUE;
        maxLineNet   = Double.MIN_VALUE;
        minBrandCode = Double.MAX_VALUE;
        maxBrandCode = Double.MIN_VALUE;

        for (UserRecord kayit : kayitlar) {
            if (kayit.getLineNetTotal() < minLineNet) minLineNet = kayit.getLineNetTotal();
            if (kayit.getLineNetTotal() > maxLineNet) maxLineNet = kayit.getLineNetTotal();

            if (kayit.getBrandCode() < minBrandCode) minBrandCode = kayit.getBrandCode();
            if (kayit.getBrandCode() > maxBrandCode) maxBrandCode = kayit.getBrandCode();
        }

        System.out.println("LineNetTotal → min: " + minLineNet + ", max: " + maxLineNet);
        System.out.println("BrandCode    → min: " + minBrandCode + ", max: " + maxBrandCode);
    }

    // Min-Max normalizasyon formülü
    private double normalize(double deger, double min, double max) {
        if (max == min) return 0.0;
        return (deger - min) / (max - min);
    }
}