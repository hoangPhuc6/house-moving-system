-- Khởi tạo các vai trò (roles) cơ bản trong hệ thống (không có tiền tố ROLE_)
INSERT INTO roles (code, name, description) VALUES
    ('CUSTOMER', 'Khách hàng', 'Người dùng đặt dịch vụ chuyển nhà'),
    ('ADMIN', 'Quản trị viên', 'Quản trị toàn bộ hệ thống'),
    ('DISPATCHER', 'Điều phối viên', 'Nhân viên trực tổng đài và điều phối đơn hàng'),
    ('MOVER', 'Nhân viên vận chuyển', 'Tài xế và nhân viên bốc xếp vận chuyển')
ON CONFLICT (code) DO NOTHING;
