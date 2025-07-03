# 🧾 Receipt Generator

A Spring Boot 3.0 based full-stack application for managing rent or utility receipts, with PDF generation and email functionality.

---

## 📦 Features

- RESTful APIs for full CRUD operations on receipts
- HTML-based admin dashboard (`receipts-master`) built with Vanilla JS & Thymeleaf
- Generate receipt PDFs (`/pdf`) and resend email with receipt
- Search receipts by owner name, email, and date range
- Editable UI table with inline updates and delete support

---

## 🔧 Tech Stack

| Layer      | Technology                     |
|------------|--------------------------------|
| Backend    | Spring Boot 3, Spring Web, JPA |
| Database   | H2 (in-memory) / MySQL / etc.  |
| PDF        | iText or OpenPDF (pluggable)   |
| Email      | Spring Mail + SMTP config      |
| Frontend   | Thymeleaf, HTML, JavaScript    |

---

## 🚀 Endpoints Overview

| HTTP Method | Endpoint                          | Description                            |
|-------------|-----------------------------------|----------------------------------------|
| `POST`      | `/api/receipts`                   | Create a new receipt                   |
| `GET`       | `/api/receipts`                   | Get all receipts                       |
| `GET`       | `/api/receipts/{id}`              | Get receipt by ID                      |
| `GET`       | `/api/receipts/owner/{name}`      | Get receipts by owner name             |
| `GET`       | `/api/receipts/email/{email}`     | Get receipts by email                  |
| `GET`       | `/api/receipts/date-range`        | Get receipts within date range         |
| `PUT`       | `/api/receipts/{id}`              | Update a receipt                       |
| `DELETE`    | `/api/receipts/{id}`              | Delete a receipt                       |
| `GET`       | `/api/receipts/{id}/pdf`          | Download receipt as PDF                |
| `POST`      | `/api/receipts/{id}/resend-email` | Send receipt to email                  |

---

## 📄 Usage

### Run the App

```bash
./mvnw spring-boot:run
```

Navigate to:

```
http://localhost:8080/receipts-master
```

> Adjust port if configured differently.

---

## 📂 File Structure

```
src/
├── main/
│   ├── java/com/example/receiptgenerator/
│   │   ├── controller/ReceiptController.java
│   │   ├── service/ReceiptService.java
│   │   ├── entity/Receipt.java
│   │   ├── dto/ReceiptRequest.java, ReceiptResponse.java
│   │   ├── util/NumberToWordsConverter.java
│   └── resources/
│       ├── templates/receipts-master.html
│       ├── application.properties
```

---

## 📬 Email Configuration (SMTP)

Add in `application.properties`:

```properties
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your_email@gmail.com
spring.mail.password=your_password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

---

## 🧪 Sample cURL

```bash
curl -X POST http://localhost:8080/api/receipts  -H 'Content-Type: application/json'  -d '{
  "ownerName": "Sahil Patil",
  "billNo": "B123",
  "receiptNo": "R123",
  "receiptDate": "2025-07-03",
  "amount": 1500.00,
  "email": "sahil@example.com"
}'
```

---

## 📘 License

MIT © 2025 Sahil Patil
