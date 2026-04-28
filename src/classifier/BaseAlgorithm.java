package classifier;

import model.UserRecord;
import java.util.List;

public abstract class BaseAlgorithm implements IClassifier {

    // Doğruluk hesaplama — her iki algoritma da kullanır
    public double calculateAccuracy(List<UserRecord> testData) {
        int correct = 0;
        for (UserRecord record : testData) {
            String predicted = predict(record);
            if (predicted.equals(record.getCategory())) {
                correct++;
            }
        }
        return (double) correct / testData.size() * 100.0;
    }

    // Train/Test split — 80/20
    public List<UserRecord>[] splitData(List<UserRecord> data) {
        int trainSize = (int) (data.size() * 0.8);
        List<UserRecord> trainSet = new java.util.ArrayList<>(data.subList(0, trainSize));
        List<UserRecord> testSet  = new java.util.ArrayList<>(data.subList(trainSize, data.size()));

        @SuppressWarnings("unchecked")
        List<UserRecord>[] result = new List[]{trainSet, testSet};
        return result;
    }
}