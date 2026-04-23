[ASK-MODE] Đã phân tích hệ thống:

Tuyệt, đây là kế hoạch 1 tuần chi tiết theo ngày, bám theo trạng thái hiện tại (đã chạy được `Create Product`) và hướng tới nền MVP đúng `PRD`/`epics`.

## Mục tiêu tuần này
- Chuyển auth từ Basic tạm thời sang luồng đăng nhập dùng token (MVP).
- Hoàn thiện Product lifecycle cơ bản (update/deactivate/list/detail).
- Dựng khung Sales flow tối thiểu và chốt được 1 luồng complete order an toàn.
- Có test tự động cho các luồng nghiệp vụ rủi ro cao.

## Day 1 - Chốt auth + RBAC nền tảng
- Rà soát contract `auth/login`, error codes, quyền `ADMIN/SALES`.
- Thiết kế payload:
  - Request: `username`, `password`
  - Response: `accessToken`, `tokenType`, `expiresIn`, `role`, `username`
- Cấu hình security để hỗ trợ:
  - endpoint public: `auth/login`, swagger, health
  - endpoint còn lại: require token
- Viết test:
  - login đúng/sai
  - endpoint protected trả `401` khi thiếu token

**Kết quả cuối ngày:** gọi được `POST /api/v1/auth/login`, nhận token hợp lệ.

## Day 2 - Áp RBAC vào Product + hoàn thiện error contract
- Áp quyền:
  - `ADMIN`: create/update/deactivate
  - `SALES`: chỉ đọc (nếu đã có list/detail)
- Chuẩn hóa error envelope:
  - `PRODUCT_SKU_DUPLICATE`
  - `VALIDATION_ERROR`
  - `PERMISSION_DENIED`
- Bổ sung test role-based:
  - SALES gọi create/update/deactivate bị chặn
  - ADMIN thao tác thành công

**Kết quả cuối ngày:** Product API có hành vi phân quyền rõ ràng, lỗi trả về nhất quán.

## Day 3 - Hoàn tất Story 1.3 + 1.4 (Product update/deactivate/low-stock eligibility)
- Thêm/hoàn thiện:
  - `PATCH /products/{id}`
  - `PATCH /products/{id}/status`
  - `GET /products`, `GET /products/{id}` (nếu chưa đủ)
- Đảm bảo business rule:
  - Deactivate không xóa lịch sử
  - Low-stock logic dựa trên `stock_qty <= low_stock_threshold`
- Viết unit/integration tests cho update/deactivate + filter sellable.

**Kết quả cuối ngày:** Epic 1 gần hoàn tất, Product module đủ dùng cho luồng bán.

## Day 4 - Khởi động Epic 2: Draft Sales Order
- Dựng model/order aggregate mức tối thiểu:
  - tạo draft order
  - thêm/sửa/xóa line items
  - tính subtotal deterministic
- API mục tiêu:
  - `POST /sales-orders`
  - `PATCH /sales-orders/{id}/items`
- Validate:
  - qty > 0
  - product active mới được thêm vào order

**Kết quả cuối ngày:** tạo và chỉnh sửa draft order chạy được end-to-end.

## Day 5 - Complete Order với transaction safety (trọng tâm tuần)
- Implement luồng `POST /sales-orders/{id}/complete`:
  - lock hàng tồn liên quan
  - kiểm tra đủ stock tất cả lines
  - trừ stock atomically
  - cập nhật order status COMPLETED
- Nếu thiếu stock:
  - fail toàn bộ, rollback toàn bộ
- Ghi inventory transaction cho SALE_OUT.

**Kết quả cuối ngày:** complete order không gây lệch tồn kho trong tình huống chuẩn.

## Day 6 - Test cứng + quan sát hệ thống
- Viết integration test bắt buộc:
  - đủ hàng -> complete thành công
  - thiếu hàng 1 line -> rollback tất cả
  - 2 request đồng thời cùng SKU -> không oversell
- Thêm logging/metrics cơ bản cho order completion latency + stock conflict.
- Soát response time các API chính với dữ liệu mẫu.

**Kết quả cuối ngày:** có bằng chứng kỹ thuật cho tính đúng đắn transaction/concurrency.

## Day 7 - Ổn định, tài liệu hóa, chuẩn bị sprint kế
- So khớp OpenAPI với implementation thực tế.
- Cập nhật `Task_List`/tiến độ stories đã done.
- Viết `README` phần:
  - cách chạy local
  - cách login lấy token
  - bộ API đã sẵn sàng test
- Lập backlog sprint kế:
  - cancel order + stock restore
  - stock-in/adjustment
  - reporting endpoints

**Kết quả cuối ngày:** branch “merge-ready” cho pha nền MVP.

---

## Ưu tiên nếu thiếu thời gian
- **Must-have:** Day 1, 2, 5, 6
- **Should-have:** Day 3, 4
- **Could-have:** phần polish tài liệu Day 7

## Tiêu chí hoàn thành tuần (DoD)
- Login/token hoạt động, endpoint bảo vệ đúng quyền.
- Product lifecycle cơ bản hoàn chỉnh.
- Có complete order atomic + test rollback + test concurrent.
- Tài liệu chạy local và test API rõ ràng cho team.

