package io;

import model.UserRecord;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DataLoader {

    // Hücreden güvenli string oku
    private String getString(Cell cell) {
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case STRING:  return cell.getStringCellValue().trim();
            case NUMERIC: return String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN: return String.valueOf(cell.getBooleanCellValue());
            default:      return "";
        }
    }

    // Hücreden güvenli double oku
    private double getDouble(Cell cell) {
        if (cell == null) return -1;
        switch (cell.getCellType()) {
            case NUMERIC: return cell.getNumericCellValue();
            case STRING:
                try { return Double.parseDouble(cell.getStringCellValue().trim()); }
                catch (Exception e) { return -1; }
            default: return -1;
        }
    }

    public List<UserRecord> loadData(String dosyaYolu) {

        List<UserRecord> kayitlar = new ArrayList<>();

        try {
            System.out.println("Dosya açılıyor: " + dosyaYolu);
            FileInputStream fis = new FileInputStream(new File(dosyaYolu));
            Workbook workbook = new XSSFWorkbook(fis);
            Sheet sayfa = workbook.getSheetAt(0);

            System.out.println("Toplam satır sayısı: " + sayfa.getLastRowNum());

            boolean ilkSatir = true;
            for (Row satir : sayfa) {
                // Başlık satırını atla
                if (ilkSatir) { ilkSatir = false; continue; }

                // Sütunları oku
                double clientCode   = getDouble(satir.getCell(9));
                String gender       = getString(satir.getCell(17));
                double lineNetTotal = getDouble(satir.getCell(7));
                String brandCodeStr = getString(satir.getCell(10));
                String brand        = getString(satir.getCell(11));
                String category     = getString(satir.getCell(12));

                // Boş satırları atla
                if (clientCode == -1 || gender.isEmpty() ||
                    lineNetTotal == -1 || brandCodeStr.isEmpty() ||
                    brand.isEmpty() || category.isEmpty()) {
                    continue;
                }

                try {
                    int brandCode;
try {
    brandCode = Integer.parseInt(brandCodeStr);
} catch (NumberFormatException e) {
    brandCode = Math.abs(brandCodeStr.hashCode()) % 1000;
}
                    UserRecord kayit = new UserRecord(
                        clientCode, gender, lineNetTotal,
                        brandCode, brand, category
                    );
                    kayitlar.add(kayit);
                } catch (Exception e) {
                    continue;
                }
            }

            workbook.close();
            fis.close();

        } catch (FileNotFoundException e) {
            System.out.println("HATA: Dosya bulunamadı → " + dosyaYolu);
        } catch (IOException e) {
            System.out.println("HATA: " + e.getMessage());
        }

        System.out.println("Yüklenen kayıt sayısı: " + kayitlar.size());
        return kayitlar;
    }
}