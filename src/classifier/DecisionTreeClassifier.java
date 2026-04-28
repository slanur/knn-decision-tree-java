package classifier;

import model.UserRecord;
import java.util.*;

public class DecisionTreeClassifier extends BaseAlgorithm {

    private Node root;
    private int maxDepth = 10;

    // ── İç sınıf: Ağaç düğümü ─────────────────────────────────────────
    private static class Node {
        String feature;       // Hangi özelliğe göre bölünüyor
        double threshold;     // Sayısal özellikler için eşik değeri
        String leafLabel;     // Yaprak düğümse tahmin edilen kategori
        Node left;
        Node right;

        boolean isLeaf() { return leafLabel != null; }
    }

    // ── train: Ağacı eğitim verisiyle kur ────────────────────────────
    @Override
    public void train(List<UserRecord> trainingData) {
        root = buildTree(trainingData, 0);
    }

    // ── predict: Tek kayıt için tahmin ───────────────────────────────
    @Override
    public String predict(UserRecord user) {
        return traverse(root, user);
    }

    // ── Ağacı recursive olarak inşa et ───────────────────────────────
    private Node buildTree(List<UserRecord> data, int depth) {
        Node node = new Node();

        // Durdurma koşulları
        if (data.isEmpty()) {
            node.leafLabel = "Unknown";
            return node;
        }
        if (depth >= maxDepth || allSameCategory(data)) {
            node.leafLabel = majorityClass(data);
            return node;
        }

        // En iyi bölmeyi bul
        BestSplit best = findBestSplit(data);
        if (best == null) {
            node.leafLabel = majorityClass(data);
            return node;
        }

        node.feature   = best.feature;
        node.threshold = best.threshold;

        List<UserRecord> leftData  = new ArrayList<>();
        List<UserRecord> rightData = new ArrayList<>();

        for (UserRecord r : data) {
            if (getFeatureValue(r, best.feature) <= best.threshold) {
                leftData.add(r);
            } else {
                rightData.add(r);
            }
        }

        // Bölme anlamsızsa yaprak yap
        if (leftData.isEmpty() || rightData.isEmpty()) {
            node.leafLabel = majorityClass(data);
            return node;
        }

        node.left  = buildTree(leftData,  depth + 1);
        node.right = buildTree(rightData, depth + 1);
        return node;
    }

    // ── Ağaçta tahmin için gezinme ────────────────────────────────────
    private String traverse(Node node, UserRecord user) {
        if (node.isLeaf()) return node.leafLabel;

        double val = getFeatureValue(user, node.feature);
        if (val <= node.threshold) {
            return traverse(node.left, user);
        } else {
            return traverse(node.right, user);
        }
    }

    // ── En iyi bölmeyi bul (Information Gain) ─────────────────────────
    private BestSplit findBestSplit(List<UserRecord> data) {
        String[] features = {"lineNetTotal", "brandCode", "gender", "clientCode"};
        double bestGain = -1;
        BestSplit best  = null;

        for (String feature : features) {
            List<Double> values = new ArrayList<>();
            for (UserRecord r : data) values.add(getFeatureValue(r, feature));
            Collections.sort(values);

            // Eşik adayları: ardışık değerlerin ortası
            Set<Double> thresholds = new LinkedHashSet<>();
            for (int i = 0; i < values.size() - 1; i++) {
                thresholds.add((values.get(i) + values.get(i + 1)) / 2.0);
            }

            for (double threshold : thresholds) {
                List<UserRecord> left  = new ArrayList<>();
                List<UserRecord> right = new ArrayList<>();
                for (UserRecord r : data) {
                    if (getFeatureValue(r, feature) <= threshold) left.add(r);
                    else right.add(r);
                }
                if (left.isEmpty() || right.isEmpty()) continue;

                double gain = informationGain(data, left, right);
                if (gain > bestGain) {
                    bestGain       = gain;
                    best           = new BestSplit();
                    best.feature   = feature;
                    best.threshold = threshold;
                }
            }
        }
        return best;
    }

    // ── Information Gain hesaplama ─────────────────────────────────────
    private double informationGain(List<UserRecord> parent,
                                   List<UserRecord> left,
                                   List<UserRecord> right) {
        double parentEntropy = entropy(parent);
        double n = parent.size();
        double weightedEntropy = (left.size()  / n) * entropy(left)
                               + (right.size() / n) * entropy(right);
        return parentEntropy - weightedEntropy;
    }

    // ── Entropy hesaplama ──────────────────────────────────────────────
    private double entropy(List<UserRecord> data) {
        Map<String, Integer> counts = new HashMap<>();
        for (UserRecord r : data) {
            counts.merge(r.getCategory(), 1, Integer::sum);
        }
        double entropy = 0;
        int n = data.size();
        for (int count : counts.values()) {
            double p = (double) count / n;
            if (p > 0) entropy -= p * (Math.log(p) / Math.log(2));
        }
        return entropy;
    }

    // ── Yardımcı: özellik değeri döndür ───────────────────────────────
    private double getFeatureValue(UserRecord r, String feature) {
        switch (feature) {
            case "lineNetTotal": return r.getLineNetTotal();
            case "brandCode":    return r.getBrandCode();
            case "clientCode":   return r.getClientCode();
            case "gender":       return r.getGender().equalsIgnoreCase("E") ? 1.0 : 0.0;
            default:             return 0.0;
        }
    }

    // ── Yardımcı: çoğunluk sınıfı ─────────────────────────────────────
    private String majorityClass(List<UserRecord> data) {
        Map<String, Integer> counts = new HashMap<>();
        for (UserRecord r : data) counts.merge(r.getCategory(), 1, Integer::sum);
        return Collections.max(counts.entrySet(), Map.Entry.comparingByValue()).getKey();
    }

    // ── Yardımcı: hepsi aynı kategori mi? ─────────────────────────────
    private boolean allSameCategory(List<UserRecord> data) {
        String first = data.get(0).getCategory();
        for (UserRecord r : data) {
            if (!r.getCategory().equals(first)) return false;
        }
        return true;
    }

    // ── İç sınıf: en iyi bölme bilgisi ───────────────────────────────
    private static class BestSplit {
        String feature;
        double threshold;
    }

    public void setMaxDepth(int maxDepth) { this.maxDepth = maxDepth; }
}