import classifier.DecisionTreeClassifier;
import classifier.IClassifier;
import model.UserRecord;

import java.util.*;

public class Main {
    public static void main(String[] args) {

        // 1. Veri yükle (arkadaşının DataLoader'ı)
        // List<UserRecord> allData = DataLoader.load("MarketSalesKocaeli.xlsx");

        // 2. Ön işleme (arkadaşının PreProcessor'ı)
        // PreProcessor.process(allData);

        // 3. Decision Tree
        DecisionTreeClassifier dt = new DecisionTreeClassifier();
        dt.setMaxDepth(10);

        // 4. Train/test split
        List<UserRecord>[] splits = dt.splitData(allData);
        List<UserRecord> trainSet = splits[0];
        List<UserRecord> testSet  = splits[1];

        // 5. Eğit
        long startTime = System.currentTimeMillis();
        dt.train(trainSet);
        long trainTime = System.currentTimeMillis() - startTime;

        // 6. Değerlendir
        long evalStart = System.currentTimeMillis();
        double accuracy = dt.calculateAccuracy(testSet);
        long evalTime = System.currentTimeMillis() - evalStart;

        System.out.println("=== Decision Tree Sonuçları ===");
        System.out.printf("Doğruluk : %.2f%%%n", accuracy);
        System.out.println("Eğitim süresi : " + trainTime + " ms");
        System.out.println("Değerlendirme  : " + evalTime  + " ms");
    }
}