CREATE TABLE tb_device_tokens (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL,
    token VARCHAR(255) NOT NULL UNIQUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    CONSTRAINT fk_device_user FOREIGN KEY(user_id) REFERENCES tb_users(id) ON DELETE CASCADE
);
