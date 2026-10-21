-- 1. BẢNG ROLES (Vai trò trong hệ thống)
CREATE TABLE roles (
    id SERIAL PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 2. BẢNG USERS (Tài khoản xác thực & đăng nhập)
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    phone_number VARCHAR(20),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE INDEX idx_users_email ON users(email);

-- 3. BẢNG USER_ROLES (Liên kết N-N giữa User và Role)
CREATE TABLE user_roles (
    user_id INT NOT NULL,
    role_id INT NOT NULL,
    assigned_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_user_roles_role FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

-- 4. BẢNG CUSTOMERS (Hồ sơ Khách hàng)
CREATE TABLE customers (
    id SERIAL PRIMARY KEY,
    user_id INT UNIQUE,
    customer_code VARCHAR(30) UNIQUE,
    full_name VARCHAR(255) NOT NULL,
    birthday DATE,
    phone_number VARCHAR(20),
    email VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_customers_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
);

CREATE INDEX idx_customers_user_id ON customers(user_id);

-- 5. BẢNG STAFF (Hồ sơ Nhân viên / Tài xế / Điều phối)
CREATE TABLE staff (
    id SERIAL PRIMARY KEY,
    user_id INT UNIQUE,
    staff_code VARCHAR(30) UNIQUE,
    full_name VARCHAR(255) NOT NULL,
    hire_date DATE,
    employment_status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
    note TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_staff_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
);

CREATE INDEX idx_staff_user_id ON staff(user_id);