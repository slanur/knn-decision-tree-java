package model;

public class UserRecord {

    private double clientCode;
    private String gender;
    private double lineNetTotal;
    private int    brandCode;
    private String brand;
    private String category;     

 
    public UserRecord(double clientCode, String gender, double lineNetTotal,
                      int brandCode, String brand, String category) {
        this.clientCode    = clientCode;
        this.gender        = gender;
        this.lineNetTotal  = lineNetTotal;
        this.brandCode     = brandCode;
        this.brand         = brand;
        this.category      = category;
    }

    public double getClientCode()   { return clientCode; }
    public String getGender()       { return gender; }
    public double getLineNetTotal() { return lineNetTotal; }
    public int    getBrandCode()    { return brandCode; }
    public String getBrand()        { return brand; }
    public String getCategory()     { return category; }

    
    //nesneyi ekrana yazdırırken okunabilir olsun diye
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