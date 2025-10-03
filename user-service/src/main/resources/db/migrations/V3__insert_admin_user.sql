INSERT INTO tb_users (id, first_name, last_name, email, password, role_type, account_status)
VALUES (
    '6a9d867c-3551-4579-99ea-ae5e50bb9d7b',
    'admin',
    'admin',
    'admin@admin.com',
    '********',
    'ADMIN',
    'ACTIVE'
) ON CONFLICT (id) DO NOTHING;