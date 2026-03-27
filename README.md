# Book management and distribution system (Backend)
Hệ thống quản lý và phát hành sách

## Công nghệ sử dụng
* **Ngôn ngữ:** Java (Last version)
* **IDE:** IntelliJ IDEA 2025.2.1
* **Framework:** Spring Boot 4.0.3
* **ORM:** Spring Data JPA (Quản lý thực thể và quan hệ DB)
* **Database:** MySQL 
* **Thư viện hỗ trợ:** - `Lombok`: Tự động tạo Getter, Setter, Constructor.
    - `Jackson`: Xử lý chuyển đổi dữ liệu sang định dạng JSON.
    - `I18n`: Đa ngôn ngữ hóa các thông báo phản hồi (MessageSource).

## Cấu trúc package: controller, service, repository, entity, dto, config.

### Tổng quan Luồng Dữ liệu *(Data Flow)*

Mỗi HTTP Request từ Client *(Frontend)* gửi tới Server sẽ đi qua các lớp theo thứ tự chuẩn sau: **Client** ➔ `Controller` ➔ `DTO` ➔ `Service` ➔ `Repository` ➔ `Entity` ➔ **Database *(MySQL)***.

#### Chi tiết từng Package

1. `com.example.demo.entity` ***(Tầng Thực thể / Domain Model)***

- **Nhiệm vụ:** Chứa các class Java ánh xạ trực tiếp 1-1 với các bảng *(tables)* trong cơ sở dữ liệu MySQL thông qua JPA *(Java Persistence API)* / Hibernate.

- **Đặc điểm nổi bật trong dự án:**

    - Các file chính: `User.java`, `Book.java`, `Category.java`, `History.java`.

    - Sử dụng các annotation của JPA: `@Entity`, `@Table`, `@Id`, `@GeneratedValue` để định nghĩa khóa chính tự tăng.

    - Định nghĩa các mối quan hệ CSDL như `@ManyToOne` và `@OneToMany` (ví dụ: `Category` chứa danh sách `Book`).

    - Sử dụng `@JsonIgnoreProperties` để ngăn chặn lỗi vòng lặp vô tận *(Infinite Recursion)* khi parse JSON *(Sách gọi Danh mục, Danh mục lại gọi Sách)*.

    - Tích hợp sâu **Lombok** *(`@Data`, `@Getter`, `@Setter`, `@NoArgsConstructor`, `@Builder`)* để loại bỏ code thừa *(getter/setter boilerplate)*.

2. `com.example.demo.repository` *(Tầng Truy cập Dữ liệu / Data Access Layer)*

- **Nhiệm vụ:** Chịu trách nhiệm giao tiếp trực tiếp với Database *(thực hiện các thao tác CRUD: Create, Read, Update, Delete)*.

- **Đặc điểm nổi bật trong dự án:**

    - Các file chính: `UserRepository.java`, `BookRepository.java`, `CategoryRepository.java`, `HistoryRepository.java`.

    - Đều là các Interface kế thừa từ `JpaRepository<Entity, ID>`.

    - **Tận dụng Spring Data JPA Query Derivation:** Không cần viết các câu lệnh SQL thuần túy phức tạp. Thay vào đó, dự án định nghĩa tên hàm để Spring tự dịch ra SQL. Ví dụ:

        - `existsByUsernameAndIdNot(String username, Long id)`: Dùng để check trùng lặp khi Update dữ liệu.

        - `findAllByOrderByTimestampDesc()`: Lấy lịch sử sắp xếp giảm dần theo thời gian.
    
3. `com.example.demo.dto` *(Tầng Truyền Tải Dữ liệu / Data Transfer Object)*

- **Nhiệm vụ:** Chứa các đối tượng dùng để "đóng gói" và vận chuyển dữ liệu giữa Client và Server *(hoặc giữa Controller và Service)*. Nó đóng vai trò làm "vùng đệm" bảo vệ Entity.

- **Đặc điểm nổi bật trong dự án:**

    - Các file chính: `UserUpdateDTO.java`, `LoginRequestDTO.java`, `ApiResponse.java`.

    - **Tách biệt với Entity:** Ví dụ `UserUpdateDTO` chứa `oldPassword`, `newPassword`, `confirmPassword` để phục vụ riêng cho form đổi mật khẩu ở Frontend, trong khi class `User` *(Entity)* không hề có các cột này dưới Database.

    - **Chuẩn hóa API:** Dùng `ApiResponse.java` để bọc mọi kết quả trả về theo một format chuẩn (có chứa `status`, `message`, `data`).

4. com.example.demo.service (Tầng Nghiệp vụ / Business Logic Layer)

- **Nhiệm vụ:** Nơi chứa "chất xám" cốt lõi của hệ thống. Nhận dữ liệu từ Controller, xử lý các thuật toán, kiểm tra ràng buộc logic, gọi Repository để lưu trữ, và ném ra lỗi nếu có vi phạm.

- **Đặc điểm nổi bật trong dự án:**

    - Các file chính: `UserService.java`, `BookService.java`, `EmailService.java`, `HistoryService.java`.

    - **Gắn** `@Service`: Được Spring quản lý dưới dạng Bean.

    - **Xử lý ràng buộc chặt chẽ:** Ví dụ hàm `updateUser` sẽ kiểm tra mật khẩu cũ, kiểm tra xem Email/Phone mới có bị trùng với người khác không trước khi lưu.

    - **Giao tiếp chéo *(Orchestration)*:** Các Service gọi lẫn nhau một cách mạch lạc. Ví dụ: `BookService` và `UserService` sau khi thao tác xong đều gọi `HistoryService.logAction(...)` để tự động lưu vết hệ thống.

    - **Bảo mật & Tiện ích:** `EmailService` tích hợp gửi mail mã OTP quên mật khẩu.

5. com.example.demo.controller (Tầng Giao tiếp API / Presentation Layer)

- **Nhiệm vụ:** Đóng vai trò là "Lễ tân", tiếp nhận các HTTP Request *(GET, POST, PUT, DELETE)* từ Frontend, chuyển đến Service xử lý, và trả về HTTP Response *(JSON)*.

- **Đặc điểm nổi bật trong dự án:**

    - Các file chính: `UserController.java`, `BookController.java`, `DashboardController.java`, `SystemController.java`.

    - Sử dụng `@RestController` và cấu hình CORS *(@`CrossOrigin`)* để cho phép ReactJS gọi API.

    - Cấu trúc URL RESTful rõ ràng *(ví dụ: `/api/books`, `/api/users/login`)*.

    - **Tích hợp Đa ngôn ngữ *(i18n)*:** Sử dụng `MessageSource` và `LocaleContextHolder` để trả về các câu thông báo lỗi/thành công (như `"login.success"`) tương ứng với ngôn ngữ cấu hình, thay vì hard-code tiếng Việt trong code.

    - Dùng `@RequestBody` để hứng JSON từ Frontend map vào DTO, và `@PathVariable` để lấy ID trên URL.

6. com.example.demo.configuration (Tầng Cấu hình hệ thống / Config)

- **Nhiệm vụ:** Chứa các class cấu hình hệ thống lúc Spring Boot khởi động, khởi tạo các Bean cần thiết.

- **Đặc điểm nổi bật trong dự án:**

    - `DataInitializer.java`: Một class cực kỳ xuất sắc chạy ngầm lúc start server. Nếu Database trống, nó sẽ tự động bơm *(seed)* dữ liệu mẫu: 10 Users *(gồm Admin)*, 10 Danh mục, và 10 Cuốn sách vào DB để dev hoặc tester có thể chạy app ngay lập tức mà không cần nhập tay.

    - `LocaleResolverConfiguration.java`: Khởi tạo cấu hình đa ngôn ngữ *(Internationalization - i18n)*, thiết lập ngôn ngữ mặc định là `vi_VN` và nạp các file properties tin nhắn (`messages.properties`).

    - `BookstoreAppPropertiesConfiguration.java`: Dùng `@ConfigurationProperties` để map các cấu hình từ file `application.properties` *(ví dụ thông tin app, version, chế độ bảo trì)* thành object Java để sử dụng trong `SystemController`.

### Thực hành gọi thử các API. 

#### Mục đích kiểm thử

- Giai đoạn kiểm thử API được tiến hành nhằm mục đích:

    - Xác minh tính chính xác của các luồng nghiệp vụ *(Business Logic)* ở phía Backend *(Spring Boot)* độc lập với Frontend.

    - Đảm bảo hệ thống bắt lỗi *(Validation)* và trả về các mã trạng thái HTTP *(HTTP Status Codes)* chuẩn xác *(200, 400, 401, 404, 500)*.

    - Kiểm tra hiệu năng và tính ổn định của cơ sở dữ liệu khi thực hiện các luồng thao tác liên tục *(CRUD)*.

    - Xác minh luồng ghi log tự động *(HistoryService)* và luồng gửi Email OTP hoạt động đúng thiết kế.

#### Công cụ và Môi trường

- **Công cụ sử dụng:** Postman 12.3.0.

- **Môi trường Server:** Localhost.

- **Cấu hình chung *(Environment Variables)*:**

    - Biến `{{base_url}}` = `http://localhost:8080/api`

    - **Headers:** Thiết lập `Content-Type: application/json`. Đối với các API cần định danh người dùng để ghi log, bổ sung header `X-Username: <tên_tài_khoản>`.

#### Các kịch bản kiểm thử tiêu biểu *(Test Cases)*

Dự án đã tiến hành chạy tổng cộng hơn 20 kịch bản kiểm thử bao phủ toàn bộ các module. Dưới đây là bảng tổng hợp các kịch bản quan trọng nhất:

1. Module Xác thực & Người dùng *(Auth & Users)*

| STT |	      Kịch bản kiểm thử (Test Case)      |  Method  |         Endpoint         |              Dữ liệu đầu vào (Body/JSON)             |    Kết quả kỳ vọng(Expected) |	 Trạng thái thực tế   |
|-----|------------------------------------------|----------|--------------------------|------------------------------------------------------|------------------------------------|------|
| 1   | Đăng nhập thành công với tài khoản Admin | `POST`   | `/users/login`           | `{"username": "admin", "password": "123456"}`        | HTTP `200` OK, trả về thông tin user.                     | PASS |
| 2   | Đăng nhập thất bại do sai mật khẩu       | `POST`   | `/users/login`           | `{"username": "admin", "password": "sai"}`           | HTTP `401 Unauthorized`, báo lỗi "Sai tài khoản/mật khẩu".   | PASS |
| 3   | Đăng ký tài khoản mới hợp lệ             | `POST`   | `/users/register`        | `{"username": "test01", "email": "test@g.com", ...}` | HTTP `200` OK, dữ liệu được lưu vào CSDL.                  | PASS |
| 4   | Đăng ký lỗi do trùng Username/Email      | `POST`   | `/users/register`        | Gửi lại Body của Test Case 3                         | HTTP `400 Bad Request`, báo lỗi "Tên tài khoản đã tồn tại". | PASS |
| 5   | Quên mật khẩu *(Yêu cầu gửi OTP)*        | `POST`   | `/users/forgot-password` | `{"email": "admin@gmail.com"}`                       | HTTP `200` OK, Console báo đã gửi email OTP thành công.    | PASS |

2. Module Quản lý Danh mục *(Categories)*

| STT |	    Kịch bản kiểm thử (Test Case)    |  Method  |    Endpoint     |     Dữ liệu đầu vào (Body/JSON)     |              Kết quả kỳ vọng(Expected)           |	 Trạng thái thực tế    |
|-----|--------------------------------------|----------|-----------------|------------------------------------ |-------------------------------------------------------|------|
| 6   | Lấy danh sách toàn bộ danh mục       | `GET`    | `/categories`   | NULL                                | HTTP `200` OK, trả về mảng JSON chứa 10 danh mục.                   | PASS |
| 7   | Thêm danh mục mới                    | `POST`   | `/categories`   | `{"name": "Manga", "status": true}` | HTTP `200` OK, thông báo "Thêm thành công: Manga".                | PASS |
| 8   | Cập nhật tên danh mục                | `PUT`    | `/categories/1` | `{"name": "IT & Công nghệ"}`        | HTTP `200` OK, thông báo cập nhật thành công.CSDL.                  | PASS |
| 9   | Lỗi cập nhật trùng tên danh mục khác | `PUT`    | `/categories/2` | `{"name": "IT & Công nghệ"}`        | HTTP `400 Bad Request`, báo lỗi "Tên danh mục đã tồn tại".                  | PASS |
| 10  | Xóa danh mục theo ID                 | `DELETE` | `/categories/1` | NULL                                | HTTP `200` OK, danh mục bị xóa khỏi DB.                    | PASS |

3. Module Quản lý Kho Sách *(Books)*

| STT |	   Kịch bản kiểm thử (Test Case)   |  Method  |  Endpoint  |          Dữ liệu đầu vào (Body/JSON)         |              Kết quả kỳ vọng(Expected)           |	 Trạng thái thực tế    |
|-----|------------------------------------|----------|------------|----------------------------------------------|-------------------------------------------------------|------|
| 11  | Lấy danh sách toàn bộ sách         | `GET`    | `/books`   | NULL                                         | HTTP `200` OK, trả về mảng JSON danh sách sách. | PASS   |
| 12  | Thêm sách mới *(Mapping Category)* | `POST`   | `/books`   | `{"title": "Sách A", "category": {"id": 1}}` | HTTP `200` OK, sách được lưu và liên kết đúng danh mục 1. | PASS |
| 13  | Lỗi thêm sách không có Title       | `POST`   | `/books`   | `{"author": "Tác giả A"}`                    | HTTP `400 Bad Request`, vi phạm ràng buộc dữ liệu.  | PASS |
| 14  | Lấy thông tin chi tiết 1 cuốn sách | `GET`    | `/books/1` | NULL                                         | HTTP `200` OK, trả về Object Book ID=1.   | PASS |


4. Module Thống kê & Lịch sử *(Dashboard & History)*

| STT |	   Kịch bản kiểm thử (Test Case)    | Method |      Endpoint      |                               Kết quả kỳ vọng(Expected)                              |	 Trạng thái thực tế    |
|-----|-------------------------------------|--------|--------------------|--------------------------------------------------------------------------------------|------|
| 15  | Lấy số liệu thống kê tổng quan      | `GET`  | `/dashboard/stats` | HTTP `200` OK, trả về tổng Users, Books, Categories và danh sách lịch sử gần nhất.   | PASS |
| 16  | Kiểm tra tính năng Ghi Log hệ thống | `GET`  | `/history`         | HTTP `200` OK, trả về mảng Log chứa các hành động Thêm/Sửa/Xóa vừa thực hiện ở trên. | PASS |
| 17  | Kiểm tra cấu hình hệ thống          | `GET`  | `/system/info`     | HTTP `200` OK, trả về thông tin Version, Tên App từ file `application.properties`.   | PASS |

#### Kết luận và Đánh giá

Sau khi tiến hành kiểm thử toàn diện các API thông qua Postman:

1. **Độ ổn định *(Reliability)*:** 100% các kịch bản kiểm thử đường dẫn chuẩn *(Happy Path)* đều trả về kết quả chính xác, dữ liệu được cập nhật đúng vào CSDL MySQL.

2. **Kiểm soát lỗi *(Error Handling)*:** Hệ thống xử lý xuất sắc các trường hợp luồng ngoại lệ *(Bad Path)* như nhập sai mật khẩu, tạo dữ liệu trùng lặp, tìm kiếm ID không tồn tại. Tầng `GlobalExceptionHandler` đã bắt lỗi thành công và trả về mã HTTP chuẩn *(400, 401, 404)* kèm theo thông báo tiếng Việt rõ ràng, thân thiện với Frontend.

3. **Tính liên kết logic:** Chức năng tự động ghi log *(`HistoryService`)* hoạt động ngầm hoàn hảo, ghi nhận chính xác mọi sự thay đổi dữ liệu mà không làm ảnh hưởng đến thời gian phản hồi *(latency)* của API chính.

### Xử lý EXCEPTION, VALIDATION VÀ LOGGING

#### Xử lý EXCEPTION

Dự án áp dụng mô hình **Xử lý ngoại lệ tập trung (*Global Exception Handling*)** ở phía Backend, giúp code ở các Controller cực kỳ sạch sẽ, không bị vướng víu bởi các khối `try-catch` lặp đi lặp lại.

1. Kiến trúc xử lý tập trung

- Tầng cấu hình sử dụng class`GlobalExceptionHandler` kết hợp với annotation `@RestControllerAdvice`.

- **Chức năng:** Hoạt động như một "tấm lưới" bao trùm toàn bộ hệ thống. Bất kỳ lỗi nào *(Exception)* văng ra từ tầng Service hay Controller đều sẽ bị "tấm lưới" này tóm gọn trước khi trả về cho Frontend.

2. Phân loại và Chuẩn hóa mã lỗi

Hệ thống tự định nghĩa và phân loại các ngoại lệ để trả về mã HTTP Status Code tương ứng:

- `ResourceNotFoundException` **(*Lỗi 404 - Not Found*)**: Kích hoạt khi tìm kiếm một ID không tồn tại *(ví dụ: `findById(99)`)*.

- `RuntimeException` **(*Lỗi 400 - Bad Request*)**: Kích hoạt khi vi phạm logic nghiệp vụ *(ví dụ: Thêm người dùng nhưng trùng Username, sai mật khẩu)*.

3. Chuẩn hóa Output và Đa ngôn ngữ *(i18n)*

Thay vì trả về lỗi "thô" của Java *(Stacktrace)* rất khó đọc và dễ lộ bảo mật, `GlobalExceptionHandler` bọc lỗi lại thành đối tượng `ApiResponse` chuẩn mực *(gồm `status`, `message`, `data`)*.

- Đồng thời, hệ thống tích hợp `MessageSource` để tự động dịch các mã lỗi *(ví dụ: `user.username.exists`)* thành câu thông báo tiếng Việt *(`Tên tài khoản đã tồn tại!"`)* thông qua file `messages_vi.properties`.

#### Xử lý VALIDATION

Để đảm bảo tính toàn vẹn dữ liệu, SmartBook thiết lập một "bức tường phòng ngự 3 lớp" *(3-Layer Defense)* từ ngoài vào trong:

**Lớp 1: Ràng buộc giao diện (Frontend - ReactJS)**

*Công cụ:* Sử dụng bộ quy tắc *(Rules)* của form `Ant Design`.

*Cơ chế:* Ngăn chặn các lỗi sơ đẳng ngay từ trình duyệt người dùng mà chưa cần gọi API.

*Ví dụ:* Ép buộc nhập đủ trường `required`, kiểm tra định dạng email *(`type: 'email'`)*, hoặc so sánh khớp mật khẩu ở ô `confirmPassword`.

**Lớp 2: Ràng buộc nghiệp vụ (Backend - Service Layer)**

*Công cụ:* Xử lý bằng code Java logic tại các lớp `...Service`.

*Cơ chế:* Dù Frontend có bị bypass *(qua mặt bằng Postman)*, Tầng Service vẫn kiểm tra lại mọi thứ. Nó sử dụng các hàm Query của JPA *(như `existsByUsernameAndIdNot`)* để chốt chặn các trường hợp trùng lặp dữ liệu *(Unique Constraint)* trước khi gọi hàm `save()`.

*Ví dụ:* Kiểm tra xem mật khẩu cũ do user gửi lên có khớp với mật khẩu đang lưu trong CSDL hay không.

**Lớp 3: Ràng buộc cơ sở dữ liệu (Database Layer - Entity)**

*Công cụ:* Các Annotation của Hibernate/JPA.

*Cơ chế:* Chốt chặn cuối cùng ở mức CSDL vật lý. Các cột quan trọng được đánh dấu `@Column(nullable = false, unique = true)` *(không được bỏ trống, không được trùng lặp)*. Nếu code Java có sơ suất bỏ lọt, MySQL sẽ từ chối lưu và văng lỗi.

#### Ghi vết hệ thống (Logging / Audit Trail)

Khác với việc dùng `System.out.println()` hay `log.info()` chỉ in ra màn hình console cho Developer đọc, SmartBook xây dựng một cơ chế **Audit Log *(Ghi vết kiểm toán)*** thực thụ, lưu thẳng vào cơ sở dữ liệu để Admin có thể theo dõi.

1. **Kiến trúc của Module History**

- **Entity** `History`: Định nghĩa bảng `history_logs` trong CSDL với các cột quan trọng: `action` *(Thêm/Sửa/Xóa/Đăng nhập)*, `entityType` *(Tài khoản/Sách/Danh mục)*, `entityName`, `performedBy` **(Người thực hiện)**, `timestamp` **(Thời gian)** và `details`.

- `HistoryService`: Chứa hàm `logAction(...)` cung cấp công cụ ghi log dùng chung cho toàn dự án.

2. **Cơ chế Định danh người dùng tự động (X-Username Header)**

Làm sao để hệ thống biết "Ai" đang thực hiện hành động sửa/xóa sách?

1. Ở Frontend: File `axiosClient.ts` sử dụng Request Interceptor để tự động nhét tên đăng nhập của user hiện tại vào một Custom Header tên là `X-Username` trong mọi API call.

2. Ở Backend: Hàm `logAction` trong `HistoryService` dùng `RequestContextHolder` để bóc tách cái Header `X-Username` này ra. Nhờ vậy, hệ thống luôn ghi nhận chính xác tên tài khoản đang thao tác *(Performed By)* một cách hoàn toàn tự động mà không cần phải truyền tham số user rườm rà từ Controller xuống Service.

3. **Tích hợp sâu vào Business Logic**

Các hàm CRUD trong `BookService`, `CategoryService`, `UserService` được thiết kế theo pattern:

`[Kiểm tra lỗi] -> [Lưu DB] -> [Gọi HistoryService.logAction] -> [Trả về kết quả]`.

Điều này đảm bảo mọi sự thay đổi trạng thái của dữ liệu đều để lại "dấu chân" minh bạch trên hệ thống.
