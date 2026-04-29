package classifier;

import model.ProcessedRecord;
import java.util.List;

public abstract class BaseAlgorithm implements IClassifier {

    public double calculateAccuracy(List<ProcessedRecord> testData) {
        int correct = 0;
        for (ProcessedRecord record : testData) {
            String predicted = predict(record);
            if (predicted.equals(record.getCategory())) {
                correct++;
            }
        }
        return (double) correct / testData.size() * 100.0;
    }

    public List<ProcessedRecord>[] splitData(List<ProcessedRecord> data) {
        int trainSize = (int) (data.size() * 0.8);
        List<ProcessedRecord> trainSet = new java.util.ArrayList<>(data.subList(0, trainSize));
        List<ProcessedRecord> testSet  = new java.util.ArrayList<>(data.subList(trainSize, data.size()));

        @SuppressWarnings("unchecked")
        List<ProcessedRecord>[] result = new List[]{trainSet, testSet};
        return result;
    }
}