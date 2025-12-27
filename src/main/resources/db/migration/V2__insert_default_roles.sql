INSERT INTO roles (name, created_at)
    VALUES
        ('ADMIN', NOW(6)),
        ('INTERPRETER', NOW(6)),
        ('CLIENT', NOW(6))
    AS new
ON DUPLICATE KEY UPDATE name = VALUES(name);
