# Bookstore Management System 
Xây dựng hệ thống quản lý sách 

## Công nghệ sử dụng
* **Ngôn ngữ:** Java (Last version)
* **IDE:** IntelliJ IDEA 2025.2.1
* **Framework:** Spring Boot 4.0.3
* **ORM:** Spring Data JPA (Quản lý thực thể và quan hệ DB)
* **Database:** MySQL 
* **Thư viện hỗ trợ:** - `Lombok`: Tự động tạo Getter, Setter, Constructor.
    - `Jackson`: Xử lý chuyển đổi dữ liệu sang định dạng JSON.
    - `I18n`: Đa ngôn ngữ hóa các thông báo phản hồi (MessageSource).

---

## Cấu trúc và Quan hệ Dữ liệu (1-N)

Đã giải quyết bài toán quan hệ Một - Nhiều giữa `Category` và `Book`:
- **Một Thể loại** chứa nhiều cuốn sách (`@OneToMany`).
- **Một cuốn sách** thuộc về một thể loại cụ thể (`@ManyToOne`).

### Kỹ thuật xử lý JSON nâng cao:
Để tránh lỗi vòng lặp vô tận (Infinite Recursion) khi trả về dữ liệu, dự án sử dụng:
- **`@JsonIgnoreProperties`**: Giúp kiểm soát chính xác những trường nào được hiển thị, đảm bảo JSON trả về gọn nhẹ và không bị lặp lại dữ liệu thừa.

---

## Các tính năng đã hoàn thành

### Quản lý Thể loại (Category)
- **CRUD cơ bản:** Thêm, Sửa, Xóa, Lấy danh sách thể loại.
- **Cascading Save:** Cho phép tạo một Thể loại mới kèm theo danh sách các cuốn sách bên trong chỉ với một lần gọi API.
- **Logic Validation:** Kiểm tra trùng tên thể loại trong Database trước khi lưu.

### Quản lý Sách (Book)
- **Truy xuất thông tin:** Mỗi cuốn sách khi lấy ra đều đi kèm thông tin chi tiết về Thể loại của nó.
- **Update linh hoạt:** Dễ dàng thay đổi thể loại cho sách thông qua API.

### Xử lý đa ngôn ngữ (I18n)
Tích hợp MessageSource để trả về các thông báo lỗi hoặc thành công bằng nhiều ngôn ngữ (Ví dụ: "Tên thể loại này đã tồn tại rồi ơi!", "Tên sách này đã có trong hệ thống rồi!").

---

## Hướng dẫn Test API

### 1. Tạo Category kèm danh sách Sách (POST)
**Endpoint:** `POST /api/categories`  
**Body (JSON):**
```json
{
  "name": "Lập trình Java",
  "description": "Sách về Java Core, Spring Boot, Hibernate",
  "status": true,
  "books": [
    {
      "title": "Spring Boot từ cơ bản đến nâng cao",
      "author": "Hoàng Văn Vũ",
      "price": 250000,
      "stock": 100
    },
    {
      "title": "Java Design Patterns",
      "author": "Vũ Developer",
      "price": 300000,
      "stock": 50
    }
  ]
}
```

### 2. Lấy danh sách Sách để kiểm tra Category

**Endpoint:** `GET /api/books`

**Kết quả mong đợi:** Mỗi cuốn sách sẽ hiển thị kèm theo Object category bên trong..

---

## Nhật ký Fix Bug & Tối ưu hóa
- Fix lỗi Vòng lặp: Chuyển từ `@JsonIgnore` sang `@JsonIgnoreProperties` để xem được dữ liệu ở cả 2 đầu API `/api/books` và `/api/categories`.
- Fix lỗi Khóa ngoại: Thêm vòng lặp `book.setCategory(categorycategory)` trong Service để đảm bảo từng cuốn sách đều nhận được ID của Category cha.
- Xử lý Response: Sử dụng `ResponseEntity` kết hợp với `MessageSource` để trả về thông báo chuyên nghiệp.
