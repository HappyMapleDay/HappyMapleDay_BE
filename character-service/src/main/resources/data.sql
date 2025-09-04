-- 테이블 생성
CREATE TABLE IF NOT EXISTS characters (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    character_name VARCHAR(255) NOT NULL,
    ocid VARCHAR(255) NOT NULL,
    server_name VARCHAR(100) NOT NULL,
    is_main_character BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 테스트용 캐릭터 데이터
INSERT INTO characters (user_id, character_name, ocid, server_name, is_main_character, created_at, updated_at) VALUES
(1, '테스트본캐', '61f6d8b4ee8b4c1b9c7f8d9e0f1a2b3c', '스카니아', true, '2024-01-01 00:00:00', '2024-01-01 00:00:00'),
(1, '테스트보돌1', '71f6d8b4ee8b4c1b9c7f8d9e0f1a2b3d', '스카니아', false, '2024-01-05 00:00:00', '2024-01-05 00:00:00'),
(1, '테스트보돌2', '81f6d8b4ee8b4c1b9c7f8d9e0f1a2b3e', '베라', false, '2024-01-10 00:00:00', '2024-01-10 00:00:00'),
(2, '다른유저캐릭터', '91f6d8b4ee8b4c1b9c7f8d9e0f1a2b3f', '스카니아', true, '2024-01-15 00:00:00', '2024-01-15 00:00:00'); 