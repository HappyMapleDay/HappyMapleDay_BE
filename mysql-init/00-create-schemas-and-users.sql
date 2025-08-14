-- 서비스별 스키마 생성 (계정/권한은 01-create-users-and-grants.sh에서 처리)
CREATE DATABASE IF NOT EXISTS happy_maple_day_boss CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS happy_maple_day_character CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS happy_maple_day_settlement CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS happy_maple_day_history CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS happy_maple_day_admin CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
