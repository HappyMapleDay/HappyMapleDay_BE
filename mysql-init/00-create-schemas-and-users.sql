-- 서비스별 스키마 생성
CREATE DATABASE IF NOT EXISTS happy_maple_day_boss CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS happy_maple_day_character CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS happy_maple_day_settlement CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS happy_maple_day_history CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS happy_maple_day_admin CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 서비스별 전용 계정 생성 (로컬 기본 비밀번호)
CREATE USER IF NOT EXISTS 'boss_user'@'%' IDENTIFIED BY 'boss_password';
CREATE USER IF NOT EXISTS 'character_user'@'%' IDENTIFIED BY 'character_password';
CREATE USER IF NOT EXISTS 'settlement_user'@'%' IDENTIFIED BY 'settlement_password';
CREATE USER IF NOT EXISTS 'history_user'@'%' IDENTIFIED BY 'history_password';
CREATE USER IF NOT EXISTS 'admin_user'@'%' IDENTIFIED BY 'admin_password';

-- 권한 부여 (각 스키마 전용)
GRANT ALL PRIVILEGES ON happy_maple_day_boss.* TO 'boss_user'@'%';
GRANT ALL PRIVILEGES ON happy_maple_day_character.* TO 'character_user'@'%';
GRANT ALL PRIVILEGES ON happy_maple_day_settlement.* TO 'settlement_user'@'%';
GRANT ALL PRIVILEGES ON happy_maple_day_history.* TO 'history_user'@'%';
GRANT ALL PRIVILEGES ON happy_maple_day_admin.* TO 'admin_user'@'%';

FLUSH PRIVILEGES;


