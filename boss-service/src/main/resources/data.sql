-- 보스 마스터 데이터
INSERT INTO bosses (boss_name, difficulty, crystal_price, max_party_size, is_monthly, is_active, min_entry_level, boss_level, required_force_type, required_force_amount) VALUES

-- 자쿰
('자쿰', '카오스', 8080000, 6, false, true, 90, 180, 'NONE', NULL),

-- 매그너스
('매그너스', '하드', 8560000, 6, false, true, 175, 190, 'NONE', NULL),

-- 힐라
('힐라', '하드', 5750000, 6, false, true, 170, 190, 'NONE', NULL),

-- 파풀라투스
('파풀라투스', '카오스', 17300000, 6, false, true, 190, 190, 'NONE', NULL),

-- 피에르
('피에르', '카오스', 8170000, 6, false, true, 180, 190, 'NONE', NULL),

-- 반반
('반반', '카오스', 8150000, 6, false, true, 180, 190, 'NONE', NULL),

-- 블러디퀸
('블러디퀸', '카오스', 8140000, 6, false, true, 180, 190, 'NONE', NULL),

-- 벨룸
('벨룸', '카오스', 9280000, 6, false, true, 180, 190, 'NONE', NULL),

-- 핑크빈
('핑크빈', '카오스', 6580000, 6, false, true, 170, 190, 'NONE', NULL),

-- 시그너스
('시그너스', '이지', 4550000, 6, false, true, 165, 140, 'NONE', NULL),
('시그너스', '노말', 7500000, 6, false, true, 165, 190, 'NONE', NULL),

-- 스우
('스우', '노말', 22000000, 6, false, true, 190, 190, 'NONE', NULL),
('스우', '하드', 77400000, 6, false, true, 190, 210, 'NONE', NULL),
('스우', '익스트림', 549000000, 2, false, true, 190, 285, 'NONE', NULL),

-- 데미안
('데미안', '노말', 23000000, 6, false, true, 190, 210, 'NONE', NULL),
('데미안', '하드', 73500000, 6, false, true, 190, 210, 'NONE', NULL),

-- 가디언 엔젤 슬라임
('가디언 엔젤 슬라임', '노말', 33500000, 6, false, true, 210, 220, 'NONE', NULL),
('가디언 엔젤 슬라임', '카오스', 113000000, 6, false, true, 210, 250, 'NONE', NULL),

-- 루시드
('루시드', '이지', 39200000, 6, false, true, 220, 230, 'ARCANE', 360),
('루시드', '노말', 46900000, 6, false, true, 220, 230, 'ARCANE', 360),
('루시드', '하드', 94500000, 6, false, true, 220, 230, 'ARCANE', 360),

-- 윌
('윌', '이지', 42500000, 6, false, true, 235, 235, 'ARCANE', 560),
('윌', '노말', 54100000, 6, false, true, 235, 235, 'ARCANE', 760),
('윌', '하드', 116000000, 6, false, true, 235, 250, 'ARCANE', 760),

-- 더스크
('더스크', '노말', 57900000, 6, false, true, 245, 255, 'ARCANE', 730),
('더스크', '카오스', 105000000, 6, false, true, 245, 255, 'ARCANE', 730),

-- 진 힐라
('진 힐라', '노말', 107000000, 6, false, true, 250, 250, 'ARCANE', 820),
('진 힐라', '하드', 160000000, 6, false, true, 250, 250, 'ARCANE', 900),

-- 듄켈
('듄켈', '노말', 62500000, 6, false, true, 255, 265, 'ARCANE', 850),
('듄켈', '하드', 142000000, 6, false, true, 255, 265, 'ARCANE', 850),

-- 검은 마법사 (월간 보스)
('검은 마법사', '하드', 1000000000, 6, true, true, 255, 275, 'ARCANE', 1320),
('검은 마법사', '익스트림', 9200000000, 6, true, true, 255, 280, 'ARCANE', 1320),

-- 선택받은 세렌
('선택받은 세렌', '노말', 259000000, 6, false, true, 260, 270, 'AUTHENTIC', 200),
('선택받은 세렌', '하드', 440000000, 6, false, true, 260, 275, 'AUTHENTIC', 200),
('선택받은 세렌', '익스트림', 2420000000, 6, false, true, 260, 280, 'AUTHENTIC', 200),

-- 감시자 칼로스
('감시자 칼로스', '이지', 345000000, 6, false, true, 265, 270, 'AUTHENTIC', 200),
('감시자 칼로스', '노말', 510000000, 6, false, true, 265, 275, 'AUTHENTIC', 300),
('감시자 칼로스', '카오스', 1120000000, 6, false, true, 265, 285, 'AUTHENTIC', 330),
('감시자 칼로스', '익스트림', 2700000000, 6, false, true, 265, 285, 'AUTHENTIC', 440),

-- 카링
('카링', '이지', 381000000, 6, false, true, 275, 275, 'AUTHENTIC', 230),
('카링', '노말', 595000000, 6, false, true, 275, 285, 'AUTHENTIC', 330),
('카링', '하드', 1310000000, 6, false, true, 275, 285, 'AUTHENTIC', 350),
('카링', '익스트림', 3150000000, 6, false, true, 275, 285, 'AUTHENTIC', 480),

-- 림보
('림보', '노말', 900000000, 3, false, true, 285, 285, 'AUTHENTIC', 500),
('림보', '하드', 1930000000, 3, false, true, 285, 285, 'AUTHENTIC', 500),

-- 발드릭스
('발드릭스', '노말', 1200000000, 3, false, true, 290, 290, 'AUTHENTIC', 700),
('발드릭스', '하드', 2160000000, 3, false, true, 290, 290, 'AUTHENTIC', 700);

-- 보스 프리셋 데이터 삽입  
-- 각 프리셋 내에서 같은 보스명은 하나의 난이도만 포함
INSERT INTO boss_presets (preset_name, boss_ids) VALUES

-- 스데미: 데미안 노말부터 아래로 12개 (같은 보스명 중복 제거)
('스데미', '[
  {"boss_id": 15}, {"boss_id": 12}, {"boss_id": 4}, {"boss_id": 8}, 
  {"boss_id": 2}, {"boss_id": 5}, {"boss_id": 6}, {"boss_id": 7}, 
  {"boss_id": 1}, {"boss_id": 11}, {"boss_id": 9}, {"boss_id": 3}
]'),

-- 이루윌: 윌 이지부터 아래로 12개 (루시드는 이지만 포함)
('이루윌', '[
  {"boss_id": 22}, {"boss_id": 19}, {"boss_id": 17}, {"boss_id": 15}, 
  {"boss_id": 12}, {"boss_id": 4}, {"boss_id": 8}, {"boss_id": 2}, 
  {"boss_id": 5}, {"boss_id": 6}, {"boss_id": 7}, {"boss_id": 1}
]'),

-- 노듄더: 듄켈 노말부터 아래로 12개 (매그너스 다음은 피에르, 반반)
('노듄더', '[
  {"boss_id": 29}, {"boss_id": 25}, {"boss_id": 23}, {"boss_id": 20}, 
  {"boss_id": 17}, {"boss_id": 15}, {"boss_id": 12}, {"boss_id": 4}, 
  {"boss_id": 8}, {"boss_id": 2}, {"boss_id": 5}, {"boss_id": 6}
]'),

-- 하스데: 스우 하드부터 아래로 12개 (같은 보스명 중복 제거)
('하스데', '[
  {"boss_id": 13}, {"boss_id": 16}, {"boss_id": 29}, {"boss_id": 25}, 
  {"boss_id": 23}, {"boss_id": 20}, {"boss_id": 17}, {"boss_id": 4}, 
  {"boss_id": 8}, {"boss_id": 2}, {"boss_id": 5}, {"boss_id": 6}
]'),

-- 검밑솔: 진힐라 하드부터 아래로 12개 (가디언은 카오스만 포함)
('검밑솔', '[
  {"boss_id": 28}, {"boss_id": 30}, {"boss_id": 24}, {"boss_id": 21}, 
  {"boss_id": 26}, {"boss_id": 18}, {"boss_id": 13}, {"boss_id": 16}, 
  {"boss_id": 4}, {"boss_id": 8}, {"boss_id": 2}, {"boss_id": 5}
]'),

-- 하세이칼: 세렌 하드부터 아래로 12개 (카링 제외, 가디언은 카오스만)
('하세이칼', '[
  {"boss_id": 34}, {"boss_id": 36}, {"boss_id": 28}, {"boss_id": 30}, 
  {"boss_id": 24}, {"boss_id": 21}, {"boss_id": 26}, {"boss_id": 18}, 
  {"boss_id": 13}, {"boss_id": 16}, {"boss_id": 4}, {"boss_id": 8}
]'); 