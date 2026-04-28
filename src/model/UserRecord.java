package model;

public class UserRecord {

    // ── Alanlar (private → dışarıdan direkt erişilemez) ──────────────────
    private double clientCode;
    private String gender;        // "E" veya "K"
    private double lineNetTotal;
    private int    brandCode;
    private String brand;
    private String category;      // Tahmin edilecek alan

    // ── Constructor ───────────────────────────────────────────────────────
    // Nesne oluşturulurken tüm değerler bir kerede atanır
    public UserRecord(double clientCode, String gender, double lineNetTotal,
                      int brandCode, String brand, String category) {
        this.clientCode    = clientCode;
        this.gender        = gender;
        this.lineNetTotal  = lineNetTotal;
        this.brandCode     = brandCode;
        this.brand         = brand;
        this.category      = category;
    }

    // ── Getter Metodları ──────────────────────────────────────────────────
    // Her alan için sadece okuma izni veriyoruz
    public double getClientCode()   { return clientCode; }
    public String getGender()       { return gender; }
    public double getLineNetTotal() { return lineNetTotal; }
    public int    getBrandCode()    { return brandCode; }
    public String getBrand()        { return brand; }
    public String getCategory()     { return category; }

    // ── toString ──────────────────────────────────────────────────────────
    // Nesneyi ekrana yazdırırken okunabilir olsun diye
    @Override
    public String toString() {
        return "UserRecord{" +
               "clientCode="   + clientCode   +
               ", gender='"    + gender       + "'" +
               ", lineNet="    + lineNetTotal +
               ", brandCode="  + brandCode    +
               ", brand='"     + brand        + "'" +
               ", category='"  + category     + "'" +
               "}";
    }
}