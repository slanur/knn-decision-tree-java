import io.DataLoader;
import model.ProcessedRecord;
import model.UserRecord;
import preprocessing.PreProcessor;

import java.util.List;

public class Main {
    public static void main(String[] args) {

        // 1. Veriyi yükle
        DataLoader loader = new DataLoader();
        List<UserRecord> kayitlar = loader.loadData(
            "/Users/silanurcandir/Desktop/musteriTahmin/MarketSalesKocaeli.xlsx"
        );

        // 2. Ön işleme yap
        PreProcessor processor = new PreProcessor();
        List<ProcessedRecord> islenmisList = processor.process(kayitlar);

        // 3. İlk 3 işlenmiş kaydı yazdır
        System.out.println("\n--- İşlenmiş Kayıtlar ---");
        for (int i = 0; i < 3; i++) {
            System.out.println(islenmisList.get(i));
        }
    }
}