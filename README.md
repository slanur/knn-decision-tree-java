# 🛒 KNN & Decision Tree — Müşteri Kategori Tahmin Sistemi

[![Java](https://img.shields.io/badge/Java-11%2B-ED8B00?logo=openjdk&logoColor=white)](https://www.java.com)
[![Maven](https://img.shields.io/badge/Build-Maven-C71A36?logo=apachemaven&logoColor=white)](https://maven.apache.org/)
[![Spark](https://img.shields.io/badge/Web-Spark%20Framework-black)](https://sparkjava.com/)
[![License](https://img.shields.io/badge/License-Educational-lightgrey)]()

Java ile **hiçbir hazır makine öğrenmesi kütüphanesi kullanılmadan sıfırdan** geliştirilmiş **K‑En Yakın Komşu (KNN)** ve **Karar Ağacı (Decision Tree – CART)** algoritmalarının canlı, web arayüzlü karşılaştırmalı analiz projesi.

<p align="center">
  <img src="https://img.shields.io/badge/Doğruluk-KNN%20%2576.86%25-blue" />
  <img src="https://img.shields.io/badge/Doğruluk-DecisionTree%20%2582.59%25-brightgreen" />
</p>

---

## 📑 İçindekiler

- [Proje Hakkında](#-proje-hakkında)
- [Proje Mimarisi](#️-proje-mimarisi)
- [OOP Prensipleri](#️-oop-prensipleri)
- [Veri Ön İşleme](#-veri-ön-işleme)
- [Algoritmalar](#-algoritmalar)
- [Deneysel Sonuçlar](#-deneysel-sonuçlar)
- [Arayüz Özellikleri](#️-arayüz-özellikleri)
- [API Uç Noktaları](#-api-uç-noktaları)
- [Kurulum ve Çalıştırma](#-kurulum-ve-çalıştırma)
- [Kullanılan Kütüphaneler](#-kullanılan-kütüphaneler)
- [Geliştiriciler](#-geliştiriciler)

---

## 📌 Proje Hakkında

Bu proje, Kocaeli'deki bir perakende zincirinin **2017 Mart ayı** satış verileri kullanılarak müşterilerin ürün kategori tercihlerinin tahmin edilmesini amaçlamaktadır. İki farklı sınıflandırma algoritması sıfırdan Java ile kodlanmış, aynı veri seti üzerinde doğruluk, hız ve bellek kullanımı açısından karşılaştırılmıştır.

| | |
|---|---|
| 📂 **Veri Seti** | `MarketSalesKocaeli.xlsx` (5093 satır → 4625 temiz kayıt) |
| 🎯 **Hedef Değişken** | `CATEGORY_NAME1` (GIDA, İÇECEK, KOZMETİK, SİGARA vb. 10 kategori) |
| 💻 **Programlama Dili** | Java (hazır ML kütüphanesi kullanılmadı) |
| 🖥️ **Arayüz** | Web tabanlı (HTML/CSS/JavaScript + Spark Framework) |

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
| **Encapsulation** | `UserRecord`, `ProcessedRecord` — private alanlar, public getter |
| **Abstraction** | `IClassifier` (interface), `BaseAlgorithm` (abstract sınıf) |
| **Inheritance** | `KNNClassifier` ve `DecisionTreeClassifier` → `BaseAlgorithm`'den extends |
| **Polymorphism** | `Evaluator` — `BaseAlgorithm` türünden parametre alır, runtime'da karar verilir |

---

## 🔄 Veri Ön İşleme

1. **Veri Temizleme** — eksik değer içeren satırlar çıkarıldı
2. **Label Encoding** — `E → 1`, `K → 0`
3. **Alfanümerik Kodlar** — `hashCode()` ile sayıya dönüştürüldü (`A25`, `A15` vb.)
4. **Min-Max Normalizasyon** — tüm sayısal değerler `[0, 1]` aralığına çekildi
5. **Train/Test Ayrımı** — `%80` eğitim (3700 kayıt) / `%20` test (925 kayıt)

---

## 🤖 Algoritmalar

### K-En Yakın Komşu (KNN)
- `K = 5` (arayüzden değiştirilebilir)
- Mesafe ölçütü: **Öklid mesafesi**
- Her tahmin için tüm eğitim verisiyle mesafe hesaplar (lazy learning)

### Karar Ağacı (Decision Tree — CART)
- `maxDepth = 10` (arayüzden değiştirilebilir)
- Dallanma kriteri: **Gini Safsızlığı**
- İkili bölme, özyinelemeli yapı

---

## 📊 Deneysel Sonuçlar

| Algoritma | Doğruluk | Süre | Bellek |
|---|---|---|---|
| KNN (K=5) | **%76.86** | 364 ms | 24395 KB |
| Decision Tree (maxDepth=10) | **%82.59** | 1 ms | 1024 KB |

> 💡 Decision Tree, test aşamasında KNN'den hem daha doğru hem de çok daha hızlı ve bellek-verimli sonuç vermiştir; KNN ise her tahmin için eğitim setinin tamamını taradığından maliyeti daha yüksektir.

---

## 🖥️ Arayüz Özellikleri

- 📂 Veri seti yükleme (`.xlsx`)
- 🤖 Model seçme (KNN / Decision Tree / İkisi Birden)
- ⚙️ Parametre girişi (K değeri, max derinlik)
- 📊 Doğruluk ve hız karşılaştırma grafikleri
- 📂 Kategori bazında doğruluk grafikleri
- 🔢 Confusion Matrix (KNN ve DT sekmeli)
- ⚡ Çalışma süresi ve bellek kullanımı

---

## 🔌 API Uç Noktaları

Sunucu `http://localhost:4567` üzerinde ayağa kalkar ve aşağıdaki JSON uç noktalarını sunar:

| Metot | Endpoint | Açıklama |
|---|---|---|
| `GET` | `/api/info` | Yüklenen veri hakkında özet bilgi (toplam/eğitim/test kayıt sayısı) |
| `POST` | `/api/yukle` | `.xlsx` dosyasını yükler, temizler ve `%80/%20` train/test olarak böler |
| `POST` | `/api/knn` | `{"k": 5}` parametresiyle KNN çalıştırır; doğruluk, süre, bellek ve confusion matrix döner |
| `POST` | `/api/dt` | `{"maxDerinlik": 10}` parametresiyle Decision Tree çalıştırır; doğruluk, süre, bellek ve confusion matrix döner |

---

## 🚀 Kurulum ve Çalıştırma

### Gereksinimler
- Java 11+
- Maven 3.6+

### Adımlar

```bash
# 1. Projeyi klonla
git clone https://github.com/slanur/knn-decision-tree-java.git
cd knn-decision-tree-java

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
| [Apache POI](https://poi.apache.org/) | `.xlsx` dosyası okuma |
| [Spark Framework](https://sparkjava.com/) | Web sunucusu |
| [Gson](https://github.com/google/gson) | JSON dönüşümü |

> ⚠️ Makine öğrenmesi algoritmaları (KNN, Decision Tree) hazır kütüphane kullanılmadan sıfırdan geliştirilmiştir. Yukarıdaki kütüphaneler sadece dosya okuma, web sunucusu ve JSON dönüşümü gibi yardımcı işler için kullanılmıştır.

---

## 👥 Geliştiriciler

- **Sılanur Çandır**
- **Hiranur Settaşı**

Kocaeli Üniversitesi — Bilgisayar Mühendisliği Bölümü
