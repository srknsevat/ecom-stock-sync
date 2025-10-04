# Railway Deployment Guide - Ecom Stock Sync

## Proje Özeti
Bu proje, e-ticaret platformları (eBay, Shopify, Amazon vb.) arasında stok senkronizasyonu sağlayan bir Spring Boot uygulamasıdır.

## Özellikler
- ✅ Platform yönetimi (eBay, Shopify, Amazon, WooCommerce, Magento)
- ✅ Ürün yönetimi ve stok takibi
- ✅ Platform-ürün eşleştirmesi
- ✅ API credential şifreleme (AES-256)
- ✅ Stok senkronizasyonu (stub implementation)
- ✅ RESTful API endpoints
- ✅ H2 (local) / PostgreSQL (production) desteği

## Railway'de Deploy Etme

### 1. Railway Hesabı Oluştur
- [Railway.app](https://railway.app) adresine git
- GitHub hesabınla giriş yap

### 2. Yeni Proje Oluştur
- "New Project" butonuna tıkla
- "Deploy from GitHub repo" seç
- Bu repository'yi seç

### 3. Environment Variables Ayarla
Railway dashboard'da Settings > Variables bölümünde şu değişkenleri ekle:

```bash
# Database (Railway otomatik oluşturur)
DATABASE_URL=postgresql://username:password@host:port/database

# Encryption Key (güvenli bir anahtar oluştur)
APP_ENCRYPTION_KEY=your-32-character-encryption-key-here

# Spring Profile
SPRING_PROFILES_ACTIVE=railway

# Server Port (Railway otomatik ayarlar)
PORT=8080
```

### 4. Deploy
- Railway otomatik olarak deploy edecek
- Build logs'u takip et
- Deploy tamamlandığında URL'yi al

## API Endpoints

### Ürün Yönetimi
```bash
# Tüm ürünleri listele
GET /api/products

# Ürün oluştur
POST /api/products
{
  "name": "Ürün Adı",
  "sku": "SKU-001",
  "stock": 100,
  "price": 99.90
}

# Ürün güncelle
PUT /api/products/{id}

# Ürün sil
DELETE /api/products/{id}
```

### Platform Yönetimi
```bash
# Tüm platformları listele
GET /api/platforms

# Platform oluştur
POST /api/platforms
{
  "name": "eBay",
  "code": "EBAY",
  "type": "EBAY",
  "description": "eBay entegrasyonu",
  "baseUrl": "https://api.ebay.com"
}

# Platform ürünlerini listele
GET /api/platforms/{id}/products

# Platform istatistikleri
GET /api/platforms/{id}/stats
```

### Senkronizasyon
```bash
# Tüm platformları senkronize et
POST /api/sync/all

# Belirli platformu senkronize et
POST /api/sync/platform/{platformId}

# Belirli ürünü senkronize et
POST /api/sync/product/{productId}

# Senkronizasyon durumu
GET /api/sync/status

# Platform bağlantısını test et
GET /api/sync/test/{platformId}
```

### Credential Yönetimi
```bash
# Platform credential'larını listele
GET /api/platforms/{id}/credentials

# Credential kaydet
POST /api/platforms/{id}/credentials
{
  "credentialType": "API_KEY",
  "credentialValue": "your-api-key"
}

# Credential sil
DELETE /api/platforms/{id}/credentials/{credentialType}
```

## Geliştirme

### Lokal Çalıştırma
```bash
# H2 veritabanı ile
./mvnw spring-boot:run

# PostgreSQL ile
./mvnw spring-boot:run -Dspring.profiles.active=railway
```

### Test
```bash
# Ürün oluştur
curl -X POST http://localhost:8081/api/products \
  -H 'Content-Type: application/json' \
  -d '{"name":"Test Ürün","sku":"TEST-001","stock":50,"price":29.90}'

# Platform oluştur
curl -X POST http://localhost:8081/api/platforms \
  -H 'Content-Type: application/json' \
  -d '{"name":"eBay","code":"EBAY","type":"EBAY","baseUrl":"https://api.ebay.com"}'

# Senkronizasyon durumu
curl http://localhost:8081/api/sync/status
```

## Güvenlik
- API credential'ları AES-256 ile şifrelenir
- Railway environment variables güvenli şekilde saklanır
- CORS tüm origin'lere açık (production'da kısıtlanmalı)

## Monitoring
- Health check: `/actuator/health`
- Uygulama metrikleri: `/actuator/metrics`

## Sonraki Adımlar
1. Gerçek platform API entegrasyonları ekle
2. Webhook desteği ekle
3. Scheduled sync jobs ekle
4. Error handling ve retry mekanizmaları geliştir
5. Logging ve monitoring iyileştir
