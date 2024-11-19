# Sipariş Yönetimi

Bu proje, Spring Boot, Kafka, Debezium ve PostgreSQL kullanarak sipariş yönetimi için bir mikroservis mimarisi uygular. Proje, sipariş oluşturma, ürün yönetimi ve fatura oluşturma gibi işlemleri yönetirken, servisler arası iletişimde Kafka ve Feign Client'ları kullanır. Aşağıda, proje tasarımına ve mimarisine dair detaylar yer almaktadır.

## Kullanılan Teknolojiler
- Spring Boot 3.3.5
- Java 21
- Kafka (Debezium ile Change Data Capture)
- PostgreSQL (Veritabanı işlemleri için)
- Feign Client'ları (Servisler arası iletişim için)
- Docker (Containerize edilmiş dağıtımlar için)
- JUnit 5 ve TestContainer (Birim ve entegrasyon testleri için)

## Proje Tasarımı

## 1. Sistem Tasarımı ve Kullanılacak Sınıflar
### OrderService
- Siparişlerin oluşturulmasından ve yönetilmesinden sorumlu servis.
- createOrder(CreateOrderRequest request): Yeni bir sipariş oluşturur.
- Siparişler oluşurken feign client ile product servisten stok kontrolü yapar ve sonrasında stoğu azaltır.
### InvoiceService
- Yeni bir sipariş oluştuğunda outbox tablosunu dinler ve fatura oluşturur.
###  ProductService
- Ürün ve stok yönetiminden sorumlu servis.

## 2. Veritabanı İşlemleri ve Veri Modeli
### Veritabanı Tasarımı
- Veritabanı işlemleri için PostgreSQL kullanılır ve her servis için ayrı veritabanı şemaları tercih edilir.
### OrderService Veritabanı:
- orders: Sipariş bilgilerini tutar.
- order items: Sipariş bilgilerinin detayını tutar.
- outbox: Sipariş olaylarını Kafka'ya göndermek için kullanılır (Debezium ile senkronize edilir).
### InvoiceService Veritabanı:
- invoices: Fatura bilgilerini tutar.
- invoice items: Fatura bilgilerinin detayını tutar.
### ProductService Veritabanı:
- products: Ürün bilgilerini ve stok durumlarını tutar.

## 3. Fatura İşlemi ve Veritabanı Hatalarının Yönetimi

Fatura işlemi sırasında bir hata meydana gelirse, işlemi tekrar denemek için retry mekanizması kullanılabilir. Bunun için @Retryable gibi Spring’in retry özelliklerinden faydalanılabilir. Ayrıca, hata durumlarında tüm işlemi geri alacak bir transactional rollback mekanizması kullanılabilir.
### Fatura işlemi sırasında bir hata oluşursa:
- Kafka mesajı işlenmezse, mesaj yeniden işlenmek üzere kuyruğa alınır.
- Veritabanı hatası meydana gelirse, Spring’in @Transactional desteğiyle işlem geri alınabilir.

## 4. Eşzamanlılık (Concurrency) Yönetimi
Birden fazla siparişin aynı anda işlenmesi durumunda, concurrency yönetimi önemlidir. Bu durumu yönetmek için aşağıdaki Java özellikleri ve araçları kullanılabilir:

- Optimistic Locking: @Version ile, bir kaynağa aynı anda erişimi sınırlayabiliriz.
- Pessimistic Locking: @Lock anotasyonu ile belirli veritabanı kayıtlarını kilitleyebiliriz.
- Senkronizasyon: Java'da synchronized anahtar kelimesi veya ReentrantLock gibi kilitleme mekanizmalarıyla eşzamanlı işlemleri yönetebiliriz.
- Kafka ve Debezium: Kafka ve Debezium kullanarak olayları yönetmek, eşzamanlılık için güçlü bir çözüm sunar, çünkü her olay bir kuyruğa alınır ve sırayla işlenir.

## 5. Test Stratejileri
Uygulamanın test edilebilirliği için aşağıdaki test stratejileri kullanılabilir:

- Unit Test: Servislerin işlevselliğini test etmek için.

Örneğin, OrderServiceTest sınıfı, sipariş oluşturma işlevinin doğru çalışıp çalışmadığını test edebilir.
@MockBean ve @WebMvcTest kullanarak bağımsız testler yazılabilir.

- Integration test: Servisler arası iletişimi test etmek için.

@SpringBootTest kullanarak tüm mikroservislerin birlikte çalıştığını test edebiliriz.
Feign Client'ları ve Kafka olaylarını test edebiliriz.

- End-to-End Test: Sistemin genel işleyişini test etmek için.

Tüm mikroservislerin birbirleriyle doğru iletişim kurup kurmadığını doğrulayan testler.

## 6. Performans ve Ölçeklenebilirlik

- Veritabanı Optimizasyonu: Veritabanı işlemlerini optimize etmek için doğru indeksleme kullanmak ve gereksiz veri yükünü engellemek için veritabanı sorguları optimize edilebilir.

- Cache Kullanımı: Sık kullanılan veriler için Redis gibi bir cache teknolojisi kullanmak, veritabanı üzerindeki yükü azaltabilir.

- Horizontal Scaling: Servislerin yatayda ölçeklenebilmesi için Docker ve Kubernetes gibi araçlar kullanılarak, sistem daha fazla yük taşıyacak şekilde yapılandırılabilir.

- Asenkron İşlem: Kafka ve Debezium kullanarak, asenkron bir yapı ile işlemler hızlandırılabilir ve sistemin genel performansı iyileştirilebilir.

- Logging ve İzleme: Zipkin, Prometheus ve Grafana gibi araçlar kullanarak, mikroservisler arasında yapılan çağrıları izler ve performans sorunlarını tespit edebilirsiniz. Bu araçlar, hangi mikroservisin hangi işlemde ne kadar zaman harcadığını gösterir ve gereksiz gecikmeleri tespit etmenize yardımcı olur.

## Proje Kurulumu
```bash
mvn clean install
```
### Docker Compose ile Çalıştırma

Projeyi Docker Compose kullanarak çalıştırabilirsiniz. Aşağıdaki komutla tüm servisleri başlatabilirsiniz:


```bash
docker-compose -f docker-compose.yml up -d
```

### Debezium Connector Oluşturma

Bu JSON yapısını localhost:8084/connectors adresine POST yöntemi ile gönderin. Bunun için aşağıdaki gibi bir cURL komutunu kullanabilirsiniz:

```bash
curl -X POST \
  http://localhost:8084/connectors \
  -H 'Content-Type: application/json' \
  -d '{
        "name": "debezium-postgres-source-connector",
        "config": {
          "connector.class": "io.debezium.connector.postgresql.PostgresConnector",
          "tasks.max": "1",
          "database.hostname": "order-db",
          "database.port": "5432",
          "database.user": "user",
          "database.password": "password",
          "database.dbname": "order_service_db",
          "database.server.name": "outbox_server",
          "table.include.list": "public.outbox",
          "plugin.name": "pgoutput",
          "snapshot.mode": "initial",
          "topic.prefix": "dbserver1",
          "transforms": "outbox",
          "transforms.outbox.type": "io.debezium.transforms.outbox.EventRouter",
          "transforms.outbox.route.topic.replacement": "outbox-events"
        }
      }'

```

## Ürün Oluşturma API

Yeni bir ürün oluşturmak için aşağıdaki **POST** isteğini kullanabilirsiniz. Bu istek, **Product Service** üzerinde çalışır.

**Endpoint**: `localhost:8083/api/v1/products`

**HTTP Method**: `POST`

**Body** (JSON):

```json
{
  "name": "Iphone-15",
  "price": 50000.00,
  "quantity": 100,
  "tax": 0.18,
  "discount": 0.10
}
```
## Sipariş Oluşturma API

Yeni bir sipariş oluşturmak için aşağıdaki **POST** isteğini kullanabilirsiniz. Bu istek, **Order Service** üzerinde çalışır ve belirtilen ürünleri sipariş eder.

**Endpoint**: `localhost:8081/api/v1/orders`

**HTTP Method**: `POST`

**Body** (JSON):

```json
[
    {
        "productId": "b05e6258-bedb-48ea-9cf5-e188c49dc12d",
        "quantity": 2
    },
    {
        "productId": "e5b7e7fd-cd19-457b-8eea-7932f141a790",
        "quantity": 10
    }
]
```