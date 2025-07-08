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

-- 스데미: 데미안 노말부터 아래로 12개
('스데미', '[
  {"boss_id": 15}, {"boss_id": 12}, {"boss_id": 4}, {"boss_id": 8}, 
  {"boss_id": 2}, {"boss_id": 5}, {"boss_id": 6}, {"boss_id": 7}, 
  {"boss_id": 1}, {"boss_id": 11}, {"boss_id": 9}, {"boss_id": 3}
]'),

-- 이루윌: 윌 이지부터 아래로 12개
('이루윌', '[
  {"boss_id": 22}, {"boss_id": 19}, {"boss_id": 17}, {"boss_id": 15}, 
  {"boss_id": 12}, {"boss_id": 4}, {"boss_id": 8}, {"boss_id": 2}, 
  {"boss_id": 5}, {"boss_id": 6}, {"boss_id": 7}, {"boss_id": 1}
]'),

-- 노듄더: 듄켈 노말부터 아래로 12개
('노듄더', '[
  {"boss_id": 29}, {"boss_id": 25}, {"boss_id": 23}, {"boss_id": 20}, 
  {"boss_id": 17}, {"boss_id": 15}, {"boss_id": 12}, {"boss_id": 4}, 
  {"boss_id": 8}, {"boss_id": 2}, {"boss_id": 5}, {"boss_id": 6}
]'),

-- 하스데: 스우 하드부터 아래로 12개
('하스데', '[
  {"boss_id": 13}, {"boss_id": 16}, {"boss_id": 29}, {"boss_id": 25}, 
  {"boss_id": 23}, {"boss_id": 20}, {"boss_id": 17}, {"boss_id": 4}, 
  {"boss_id": 8}, {"boss_id": 2}, {"boss_id": 5}, {"boss_id": 6}
]'),

-- 검밑솔: 진힐라 하드부터 아래로 12개
('검밑솔', '[
  {"boss_id": 28}, {"boss_id": 30}, {"boss_id": 24}, {"boss_id": 21}, 
  {"boss_id": 26}, {"boss_id": 18}, {"boss_id": 13}, {"boss_id": 16}, 
  {"boss_id": 4}, {"boss_id": 8}, {"boss_id": 2}, {"boss_id": 5}
]'),

-- 하세이칼: 세렌 하드부터 아래로 12개
('하세이칼', '[
  {"boss_id": 34}, {"boss_id": 36}, {"boss_id": 28}, {"boss_id": 30}, 
  {"boss_id": 24}, {"boss_id": 21}, {"boss_id": 26}, {"boss_id": 18}, 
  {"boss_id": 13}, {"boss_id": 16}, {"boss_id": 4}, {"boss_id": 8}
]'); 

-- 아이템 마스터 데이터 삽입
INSERT INTO items (item_name, is_random_box) VALUES
('녹옥의 보스 반지 상자', true),
('홍옥의 보스 반지 상자', true),
('흑옥의 보스 반지 상자', true),
('백옥의 보스 반지 상자', true),
('생명의 보스 반지 상자', true),
('루즈 컨트롤 머신 마크', false),
('손상된 블랙 하트', false),
('컴플리트 언더 컨트롤', false),
('마력이 깃든 안대', false),
('트와일라이트 마크', false),
('몽환의 벨트', false),
('저주받은 마도서 선택 상자', false),
('에스텔라 이어링', false),
('커맨더 포스 이어링', false),
('거대한 공포', false),
('데이브레이크 펜던트', false),
('고통의 근원', false),
('창세의 뱃지', false),
('익셉셔널 해머 - 벨트', false),
('미트라의 분노 선택 상자', false),
('익셉셔널 해머 - 얼굴장식', false),
('생명의 연마석', false),
('의지의 에테르넬 방어구 상자', false),
('익셉셔널 해머 - 눈장식', false),
('흉수의 에테르넬 방어구 상자', false),
('익셉셔널 해머 - 귀고리', false),
('신념의 연마석', false),
('욕망의 에테르넬 방어구 상자', false),
('근원의 속삭임', false),
('발드릭스로이드', false),
('맹세의 에테르넬 방어구 상자', false);

-- 보스 드랍 아이템 연결 데이터 삽입
INSERT INTO boss_drop_items (boss_id, item_id) VALUES

-- 스우
((SELECT id FROM bosses WHERE boss_name = '스우' AND difficulty = '노말'), (SELECT id FROM items WHERE item_name = '녹옥의 보스 반지 상자')),
((SELECT id FROM bosses WHERE boss_name = '스우' AND difficulty = '하드'), (SELECT id FROM items WHERE item_name = '홍옥의 보스 반지 상자')),
((SELECT id FROM bosses WHERE boss_name = '스우' AND difficulty = '하드'), (SELECT id FROM items WHERE item_name = '루즈 컨트롤 머신 마크')),
((SELECT id FROM bosses WHERE boss_name = '스우' AND difficulty = '하드'), (SELECT id FROM items WHERE item_name = '손상된 블랙 하트')),
((SELECT id FROM bosses WHERE boss_name = '스우' AND difficulty = '익스트림'), (SELECT id FROM items WHERE item_name = '백옥의 보스 반지 상자')),
((SELECT id FROM bosses WHERE boss_name = '스우' AND difficulty = '익스트림'), (SELECT id FROM items WHERE item_name = '루즈 컨트롤 머신 마크')),
((SELECT id FROM bosses WHERE boss_name = '스우' AND difficulty = '익스트림'), (SELECT id FROM items WHERE item_name = '손상된 블랙 하트')),
((SELECT id FROM bosses WHERE boss_name = '스우' AND difficulty = '익스트림'), (SELECT id FROM items WHERE item_name = '컴플리트 언더 컨트롤')),

-- 데미안
((SELECT id FROM bosses WHERE boss_name = '데미안' AND difficulty = '노말'), (SELECT id FROM items WHERE item_name = '녹옥의 보스 반지 상자')),
((SELECT id FROM bosses WHERE boss_name = '데미안' AND difficulty = '하드'), (SELECT id FROM items WHERE item_name = '홍옥의 보스 반지 상자')),
((SELECT id FROM bosses WHERE boss_name = '데미안' AND difficulty = '하드'), (SELECT id FROM items WHERE item_name = '마력이 깃든 안대')),

-- 가디언 엔젤 슬라임
((SELECT id FROM bosses WHERE boss_name = '가디언 엔젤 슬라임' AND difficulty = '노말'), (SELECT id FROM items WHERE item_name = '녹옥의 보스 반지 상자')),
((SELECT id FROM bosses WHERE boss_name = '가디언 엔젤 슬라임' AND difficulty = '카오스'), (SELECT id FROM items WHERE item_name = '흑옥의 보스 반지 상자')),

-- 루시드
((SELECT id FROM bosses WHERE boss_name = '루시드' AND difficulty = '이지'), (SELECT id FROM items WHERE item_name = '녹옥의 보스 반지 상자')),
((SELECT id FROM bosses WHERE boss_name = '루시드' AND difficulty = '노말'), (SELECT id FROM items WHERE item_name = '녹옥의 보스 반지 상자')),
((SELECT id FROM bosses WHERE boss_name = '루시드' AND difficulty = '노말'), (SELECT id FROM items WHERE item_name = '트와일라이트 마크')),
((SELECT id FROM bosses WHERE boss_name = '루시드' AND difficulty = '하드'), (SELECT id FROM items WHERE item_name = '홍옥의 보스 반지 상자')),
((SELECT id FROM bosses WHERE boss_name = '루시드' AND difficulty = '하드'), (SELECT id FROM items WHERE item_name = '트와일라이트 마크')),
((SELECT id FROM bosses WHERE boss_name = '루시드' AND difficulty = '하드'), (SELECT id FROM items WHERE item_name = '몽환의 벨트')),

-- 윌
((SELECT id FROM bosses WHERE boss_name = '윌' AND difficulty = '이지'), (SELECT id FROM items WHERE item_name = '녹옥의 보스 반지 상자')),
((SELECT id FROM bosses WHERE boss_name = '윌' AND difficulty = '노말'), (SELECT id FROM items WHERE item_name = '녹옥의 보스 반지 상자')),
((SELECT id FROM bosses WHERE boss_name = '윌' AND difficulty = '노말'), (SELECT id FROM items WHERE item_name = '트와일라이트 마크')),
((SELECT id FROM bosses WHERE boss_name = '윌' AND difficulty = '하드'), (SELECT id FROM items WHERE item_name = '홍옥의 보스 반지 상자')),
((SELECT id FROM bosses WHERE boss_name = '윌' AND difficulty = '하드'), (SELECT id FROM items WHERE item_name = '트와일라이트 마크')),
((SELECT id FROM bosses WHERE boss_name = '윌' AND difficulty = '하드'), (SELECT id FROM items WHERE item_name = '저주받은 마도서 선택 상자')),

-- 듄켈
((SELECT id FROM bosses WHERE boss_name = '듄켈' AND difficulty = '노말'), (SELECT id FROM items WHERE item_name = '녹옥의 보스 반지 상자')),
((SELECT id FROM bosses WHERE boss_name = '듄켈' AND difficulty = '노말'), (SELECT id FROM items WHERE item_name = '에스텔라 이어링')),
((SELECT id FROM bosses WHERE boss_name = '듄켈' AND difficulty = '하드'), (SELECT id FROM items WHERE item_name = '흑옥의 보스 반지 상자')),
((SELECT id FROM bosses WHERE boss_name = '듄켈' AND difficulty = '하드'), (SELECT id FROM items WHERE item_name = '에스텔라 이어링')),
((SELECT id FROM bosses WHERE boss_name = '듄켈' AND difficulty = '하드'), (SELECT id FROM items WHERE item_name = '커맨더 포스 이어링')),

-- 더스크
((SELECT id FROM bosses WHERE boss_name = '더스크' AND difficulty = '노말'), (SELECT id FROM items WHERE item_name = '녹옥의 보스 반지 상자')),
((SELECT id FROM bosses WHERE boss_name = '더스크' AND difficulty = '노말'), (SELECT id FROM items WHERE item_name = '에스텔라 이어링')),
((SELECT id FROM bosses WHERE boss_name = '더스크' AND difficulty = '카오스'), (SELECT id FROM items WHERE item_name = '흑옥의 보스 반지 상자')),
((SELECT id FROM bosses WHERE boss_name = '더스크' AND difficulty = '카오스'), (SELECT id FROM items WHERE item_name = '에스텔라 이어링')),
((SELECT id FROM bosses WHERE boss_name = '더스크' AND difficulty = '카오스'), (SELECT id FROM items WHERE item_name = '거대한 공포')),

-- 진 힐라
((SELECT id FROM bosses WHERE boss_name = '진 힐라' AND difficulty = '노말'), (SELECT id FROM items WHERE item_name = '홍옥의 보스 반지 상자')),
((SELECT id FROM bosses WHERE boss_name = '진 힐라' AND difficulty = '노말'), (SELECT id FROM items WHERE item_name = '데이브레이크 펜던트')),
((SELECT id FROM bosses WHERE boss_name = '진 힐라' AND difficulty = '하드'), (SELECT id FROM items WHERE item_name = '흑옥의 보스 반지 상자')),
((SELECT id FROM bosses WHERE boss_name = '진 힐라' AND difficulty = '하드'), (SELECT id FROM items WHERE item_name = '데이브레이크 펜던트')),
((SELECT id FROM bosses WHERE boss_name = '진 힐라' AND difficulty = '하드'), (SELECT id FROM items WHERE item_name = '고통의 근원')),

-- 검은 마법사
((SELECT id FROM bosses WHERE boss_name = '검은 마법사' AND difficulty = '하드'), (SELECT id FROM items WHERE item_name = '백옥의 보스 반지 상자')),
((SELECT id FROM bosses WHERE boss_name = '검은 마법사' AND difficulty = '하드'), (SELECT id FROM items WHERE item_name = '창세의 뱃지')),
((SELECT id FROM bosses WHERE boss_name = '검은 마법사' AND difficulty = '익스트림'), (SELECT id FROM items WHERE item_name = '백옥의 보스 반지 상자')),
((SELECT id FROM bosses WHERE boss_name = '검은 마법사' AND difficulty = '익스트림'), (SELECT id FROM items WHERE item_name = '창세의 뱃지')),
((SELECT id FROM bosses WHERE boss_name = '검은 마법사' AND difficulty = '익스트림'), (SELECT id FROM items WHERE item_name = '익셉셔널 해머 - 벨트')),

-- 선택받은 세렌
((SELECT id FROM bosses WHERE boss_name = '선택받은 세렌' AND difficulty = '노말'), (SELECT id FROM items WHERE item_name = '흑옥의 보스 반지 상자')),
((SELECT id FROM bosses WHERE boss_name = '선택받은 세렌' AND difficulty = '노말'), (SELECT id FROM items WHERE item_name = '데이브레이크 펜던트')),
((SELECT id FROM bosses WHERE boss_name = '선택받은 세렌' AND difficulty = '하드'), (SELECT id FROM items WHERE item_name = '백옥의 보스 반지 상자')),
((SELECT id FROM bosses WHERE boss_name = '선택받은 세렌' AND difficulty = '하드'), (SELECT id FROM items WHERE item_name = '데이브레이크 펜던트')),
((SELECT id FROM bosses WHERE boss_name = '선택받은 세렌' AND difficulty = '하드'), (SELECT id FROM items WHERE item_name = '미트라의 분노 선택 상자')),
((SELECT id FROM bosses WHERE boss_name = '선택받은 세렌' AND difficulty = '익스트림'), (SELECT id FROM items WHERE item_name = '백옥의 보스 반지 상자')),
((SELECT id FROM bosses WHERE boss_name = '선택받은 세렌' AND difficulty = '익스트림'), (SELECT id FROM items WHERE item_name = '데이브레이크 펜던트')),
((SELECT id FROM bosses WHERE boss_name = '선택받은 세렌' AND difficulty = '익스트림'), (SELECT id FROM items WHERE item_name = '미트라의 분노 선택 상자')),
((SELECT id FROM bosses WHERE boss_name = '선택받은 세렌' AND difficulty = '익스트림'), (SELECT id FROM items WHERE item_name = '익셉셔널 해머 - 얼굴장식')),

-- 감시자 칼로스
((SELECT id FROM bosses WHERE boss_name = '감시자 칼로스' AND difficulty = '이지'), (SELECT id FROM items WHERE item_name = '백옥의 보스 반지 상자')),
((SELECT id FROM bosses WHERE boss_name = '감시자 칼로스' AND difficulty = '노말'), (SELECT id FROM items WHERE item_name = '백옥의 보스 반지 상자')),
((SELECT id FROM bosses WHERE boss_name = '감시자 칼로스' AND difficulty = '노말'), (SELECT id FROM items WHERE item_name = '생명의 연마석')),
((SELECT id FROM bosses WHERE boss_name = '감시자 칼로스' AND difficulty = '카오스'), (SELECT id FROM items WHERE item_name = '생명의 보스 반지 상자')),
((SELECT id FROM bosses WHERE boss_name = '감시자 칼로스' AND difficulty = '카오스'), (SELECT id FROM items WHERE item_name = '생명의 연마석')),
((SELECT id FROM bosses WHERE boss_name = '감시자 칼로스' AND difficulty = '카오스'), (SELECT id FROM items WHERE item_name = '의지의 에테르넬 방어구 상자')),
((SELECT id FROM bosses WHERE boss_name = '감시자 칼로스' AND difficulty = '익스트림'), (SELECT id FROM items WHERE item_name = '생명의 보스 반지 상자')),
((SELECT id FROM bosses WHERE boss_name = '감시자 칼로스' AND difficulty = '익스트림'), (SELECT id FROM items WHERE item_name = '생명의 연마석')),
((SELECT id FROM bosses WHERE boss_name = '감시자 칼로스' AND difficulty = '익스트림'), (SELECT id FROM items WHERE item_name = '의지의 에테르넬 방어구 상자')),
((SELECT id FROM bosses WHERE boss_name = '감시자 칼로스' AND difficulty = '익스트림'), (SELECT id FROM items WHERE item_name = '익셉셔널 해머 - 눈장식')),

-- 카링
((SELECT id FROM bosses WHERE boss_name = '카링' AND difficulty = '이지'), (SELECT id FROM items WHERE item_name = '백옥의 보스 반지 상자')),
((SELECT id FROM bosses WHERE boss_name = '카링' AND difficulty = '노말'), (SELECT id FROM items WHERE item_name = '백옥의 보스 반지 상자')),
((SELECT id FROM bosses WHERE boss_name = '카링' AND difficulty = '노말'), (SELECT id FROM items WHERE item_name = '생명의 연마석')),
((SELECT id FROM bosses WHERE boss_name = '카링' AND difficulty = '하드'), (SELECT id FROM items WHERE item_name = '생명의 보스 반지 상자')),
((SELECT id FROM bosses WHERE boss_name = '카링' AND difficulty = '하드'), (SELECT id FROM items WHERE item_name = '생명의 연마석')),
((SELECT id FROM bosses WHERE boss_name = '카링' AND difficulty = '하드'), (SELECT id FROM items WHERE item_name = '흉수의 에테르넬 방어구 상자')),
((SELECT id FROM bosses WHERE boss_name = '카링' AND difficulty = '익스트림'), (SELECT id FROM items WHERE item_name = '생명의 보스 반지 상자')),
((SELECT id FROM bosses WHERE boss_name = '카링' AND difficulty = '익스트림'), (SELECT id FROM items WHERE item_name = '생명의 연마석')),
((SELECT id FROM bosses WHERE boss_name = '카링' AND difficulty = '익스트림'), (SELECT id FROM items WHERE item_name = '흉수의 에테르넬 방어구 상자')),
((SELECT id FROM bosses WHERE boss_name = '카링' AND difficulty = '익스트림'), (SELECT id FROM items WHERE item_name = '익셉셔널 해머 - 귀고리')),

-- 림보
((SELECT id FROM bosses WHERE boss_name = '림보' AND difficulty = '노말'), (SELECT id FROM items WHERE item_name = '생명의 보스 반지 상자')),
((SELECT id FROM bosses WHERE boss_name = '림보' AND difficulty = '노말'), (SELECT id FROM items WHERE item_name = '신념의 연마석')),
((SELECT id FROM bosses WHERE boss_name = '림보' AND difficulty = '하드'), (SELECT id FROM items WHERE item_name = '생명의 보스 반지 상자')),
((SELECT id FROM bosses WHERE boss_name = '림보' AND difficulty = '하드'), (SELECT id FROM items WHERE item_name = '신념의 연마석')),
((SELECT id FROM bosses WHERE boss_name = '림보' AND difficulty = '하드'), (SELECT id FROM items WHERE item_name = '욕망의 에테르넬 방어구 상자')),
((SELECT id FROM bosses WHERE boss_name = '림보' AND difficulty = '하드'), (SELECT id FROM items WHERE item_name = '근원의 속삭임')),

-- 발드릭스
((SELECT id FROM bosses WHERE boss_name = '발드릭스' AND difficulty = '노말'), (SELECT id FROM items WHERE item_name = '생명의 보스 반지 상자')),
((SELECT id FROM bosses WHERE boss_name = '발드릭스' AND difficulty = '노말'), (SELECT id FROM items WHERE item_name = '신념의 연마석')),
((SELECT id FROM bosses WHERE boss_name = '발드릭스' AND difficulty = '노말'), (SELECT id FROM items WHERE item_name = '발드릭스로이드')),
((SELECT id FROM bosses WHERE boss_name = '발드릭스' AND difficulty = '하드'), (SELECT id FROM items WHERE item_name = '생명의 보스 반지 상자')),
((SELECT id FROM bosses WHERE boss_name = '발드릭스' AND difficulty = '하드'), (SELECT id FROM items WHERE item_name = '신념의 연마석')),
((SELECT id FROM bosses WHERE boss_name = '발드릭스' AND difficulty = '하드'), (SELECT id FROM items WHERE item_name = '발드릭스로이드')),
((SELECT id FROM bosses WHERE boss_name = '발드릭스' AND difficulty = '하드'), (SELECT id FROM items WHERE item_name = '맹세의 에테르넬 방어구 상자'));

-- 박스 내용물 마스터 데이터 삽입
INSERT INTO box_content_item (item_name, item_level, full_item_name, is_special, notes) VALUES

-- 리스트레인트 링 시리즈
('리스트레인트 링', 1, '리스트레인트 링 1레벨', false, null),
('리스트레인트 링', 2, '리스트레인트 링 2레벨', false, null),
('리스트레인트 링', 3, '리스트레인트 링 3레벨', false, null),
('리스트레인트 링', 4, '리스트레인트 링 4레벨', false, null),
-- 컨티뉴어스 링 시리즈
('컨티뉴어스 링', 1, '컨티뉴어스 링 1레벨', false, null),
('컨티뉴어스 링', 2, '컨티뉴어스 링 2레벨', false, null),
('컨티뉴어스 링', 3, '컨티뉴어스 링 3레벨', false, null),
('컨티뉴어스 링', 4, '컨티뉴어스 링 4레벨', false, null),
-- 기타 아이템
('생명의 연마석', null, '생명의 연마석', true, '생명의 보스 반지 상자에서만 나옴');

-- 랜덤박스-내용물 연결 데이터 삽입

-- 녹옥의 보스 반지 상자 (1~3 레벨)
INSERT INTO random_box_items (item_id, box_content_item_id) 
SELECT items.id, box_content_item.id
FROM items
CROSS JOIN box_content_item
WHERE items.item_name = '녹옥의 보스 반지 상자'
  AND box_content_item.item_name IN ('리스트레인트 링', '컨티뉴어스 링')
  AND box_content_item.item_level BETWEEN 1 AND 3;

-- 홍옥의 보스 반지 상자 (1~4 레벨)
INSERT INTO random_box_items (item_id, box_content_item_id) 
SELECT items.id, box_content_item.id
FROM items
CROSS JOIN box_content_item
WHERE items.item_name = '홍옥의 보스 반지 상자'
  AND box_content_item.item_name IN ('리스트레인트 링', '컨티뉴어스 링')
  AND box_content_item.item_level BETWEEN 1 AND 4;

-- 흑옥의 보스 반지 상자 (1~4 레벨)
INSERT INTO random_box_items (item_id, box_content_item_id) 
SELECT items.id, box_content_item.id
FROM items
CROSS JOIN box_content_item
WHERE items.item_name = '흑옥의 보스 반지 상자'
  AND box_content_item.item_name IN ('리스트레인트 링', '컨티뉴어스 링')
  AND box_content_item.item_level BETWEEN 1 AND 4;

-- 백옥의 보스 반지 상자 (3~4 레벨)
INSERT INTO random_box_items (item_id, box_content_item_id) 
SELECT items.id, box_content_item.id
FROM items
CROSS JOIN box_content_item
WHERE items.item_name = '백옥의 보스 반지 상자'
  AND box_content_item.item_name IN ('리스트레인트 링', '컨티뉴어스 링')
  AND box_content_item.item_level BETWEEN 3 AND 4;

-- 생명의 보스 반지 상자 (3~4 레벨 + 생명의 연마석)
INSERT INTO random_box_items (item_id, box_content_item_id) 
SELECT items.id, box_content_item.id
FROM items
CROSS JOIN box_content_item
WHERE items.item_name = '생명의 보스 반지 상자'
  AND (
    (box_content_item.item_name IN ('리스트레인트 링', '컨티뉴어스 링') 
     AND box_content_item.item_level BETWEEN 3 AND 4)
    OR box_content_item.item_name = '생명의 연마석'
  );