package model;

public class ProcessedRecord {

    
    private double genderEncoded;   // E→1.0, K→0.0
    private double lineNetNorm;     // 0-1 arası normalize edilmiş harcama
    private double brandCodeNorm;   // 0-1 arası normalize edilmiş marka kodu
    private String category;        // Tahmin edilecek alan

    
    public ProcessedRecord(double genderEncoded, double lineNetNorm,
                           double brandCodeNorm, String category) {
        this.genderEncoded = genderEncoded;
        this.lineNetNorm   = lineNetNorm;
        this.brandCodeNorm = brandCodeNorm;
        this.category      = category;
    }

    
    public double getGenderEncoded()  { return genderEncoded; }
    public double getLineNetNorm()    { return lineNetNorm; }
    public double getBrandCodeNorm()  { return brandCodeNorm; }
    public String getCategory()       { return category; }

    
    @Override
    public String toString() {
        return "ProcessedRecord{" +
               "gender="    + genderEncoded +
               ", lineNet=" + String.format("%.4f", lineNetNorm) +
               ", brand="   + String.format("%.4f", brandCodeNorm) +
               ", category='" + category + "'" +
               "}";
    }
}