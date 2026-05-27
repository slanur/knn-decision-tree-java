# 🛒 Müşteri Tahmin Sistemi

Java tabanlı, kütüphane kullanılmadan sıfırdan geliştirilmiş **K-En Yakın Komşu (KNN)** ve **Karar Ağacı (Decision Tree - CART)** algoritmalarının karşılaştırmalı analizi.

---

## 📌 Proje Hakkında

Bu proje, Kocaeli'deki bir perakende zincirinin 2017 yılı Mart ayına ait satış verileri kullanılarak müşterilerin ürün kategori tercihlerinin tahmin edilmesini amaçlamaktadır.

- **Veri Seti:** MarketSalesKocaeli.xlsx (5093 satır, 4625 temiz kayıt)
- **Hedef:** CATEGORY_NAME1 sütunu (GIDA, İÇECEK, KOZMETİK, SİGARA vb. 10 kategori)
- **Programlama Dili:** Java (Hazır ML kütüphanesi kullanılmadı)
- **Arayüz:** Web tabanlı (HTML/CSS/JavaScript + Spark Framework)

---

## 🏗️ Proje Mimarisi

```
src/
├── Main.java                          → Uygulamayı başlatır
├── WebServer.java                     → HTTP sunucusu (Spark Framework)
├── model/
│   ├── UserRecord.java                → Ham veri modeli (Encapsulation)
│   └── ProcessedRecord.java           → İşlenmiş veri modeli
├── io/
│   └── DataLoader.java                → .xlsx dosyası okuma (Apache POI)
├── preprocessing/
│   └── PreProcessor.java              → Encoding + Normalizasyon
├── classifier/
│   ├── IClassifier.java               → Interface (train + predict)
│   ├── BaseAlgorithm.java             → Abstract sınıf (ortak metodlar)
│   ├── KNNClassifier.java             → KNN algoritması
│   └── DecisionTreeClassifier.java    → CART Decision Tree algoritması
└── evaluation/
    └── Evaluator.java                 → Doğruluk, süre, confusion matrix
```

---

## ⚙️ OOP Prensipleri

| Prensip | Nerede Kullanıldı |
|---|---|
| **Encapsulation** | UserRecord, ProcessedRecord — private alanlar, public getter |
| **Abstraction** | IClassifier (interface), BaseAlgorithm (abstract sınıf) |
| **Inheritance** | KNNClassifier ve DecisionTreeClassifier → BaseAlgorithm'den extends |
| **Polymorphism** | Evaluator — BaseAlgorithm türünden parametre alır, runtime'da karar verilir |

---

## 🔄 Veri Ön İşleme

- **Veri Temizleme:** Eksik değer içeren satırlar çıkarıldı
- **Label Encoding:** E → 1, K → 0
- **Alfanümerik Kodlar:** hashCode() ile sayıya dönüştürüldü (A25, A15 vb.)
- **Min-Max Normalizasyon:** Tüm sayısal değerler [0, 1] aralığına çekildi
- **Train/Test Ayrımı:** %80 eğitim (3700 kayıt), %20 test (925 kayıt)

---

## 🤖 Algoritmalar

### K-En Yakın Komşu (KNN)
- K = 5
- Mesafe ölçütü: Öklid mesafesi
- Her tahmin için tüm eğitim verisiyle mesafe hesaplar

### Karar Ağacı (Decision Tree — CART)
- maxDepth = 10
- Dallanma kriteri: Gini Safsızlığı
- İkili bölme, özyinelemeli yapı

---

## 📊 Deneysel Sonuçlar

| Algoritma | Doğruluk | Süre | Bellek |
|---|---|---|---|
| KNN (K=5) | %76.86 | 364 ms | 24395 KB |
| Decision Tree (maxDepth=10) | %82.59 | 1 ms | 1024 KB |

---

## 🖥️ Arayüz Özellikleri

- 📂 Veri seti yükleme (.xlsx)
- 🤖 Model seçme (KNN / Decision Tree / İkisi Birden)
- ⚙️ Parametre girişi (K değeri, max derinlik)
- 📊 Doğruluk ve hız karşılaştırma grafikleri
- 📂 Kategori bazında doğruluk grafikleri
- 🔢 Confusion Matrix (KNN ve DT sekmeli)
- ⚡ Çalışma süresi ve bellek kullanımı

---

## 🚀 Kurulum ve Çalıştırma

### Gereksinimler
- Java 11+
- Maven 3.6+

### Adımlar

```bash
# 1. Projeyi klonla
git clone https://github.com/kullaniciadi/musteriTahmin.git
cd musteriTahmin

# 2. Bağımlılıkları yükle
mvn dependency:resolve

# 3. Çalıştır
mvn compile exec:java -Dexec.mainClass="Main"

# 4. Tarayıcıda aç
# http://localhost:4567
```

### Kullanım

1. Tarayıcıda `http://localhost:4567` adresine git
2. **Dosya Seç** butonuyla `MarketSalesKocaeli.xlsx` dosyasını seç
3. **Yükle** butonuna bas
4. Model seç (KNN / Decision Tree / İkisi Birden)
5. Parametreleri gir (K değeri veya max derinlik)
6. **Seçili Modeli Çalıştır** butonuna bas
7. Sonuçları incele

---

## 📦 Kullanılan Kütüphaneler

| Kütüphane | Amaç |
|---|---|
| Apache POI | .xlsx dosyası okuma |
| Spark Framework | Web sunucusu |
| Gson | JSON dönüşümü |

> ⚠️ Makine öğrenmesi algoritmaları hazır kütüphane kullanılmadan sıfırdan geliştirilmiştir.

---

## 👥 Geliştirici

-Sılanur Çandır  
-Hiranur Settaşı 

Kocaeli Üniversitesi — Bilgisayar Mühendisliği Bölümü  
