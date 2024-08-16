CREATE TABLE IF NOT EXISTS topic
(
    id   BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS keyword
(
    id       BIGINT AUTO_INCREMENT PRIMARY KEY,
    name     VARCHAR(255) NOT NULL,
    topic_id BIGINT,
    FOREIGN KEY (topic_id) REFERENCES topic (id)
);

INSERT INTO topic (name)
VALUES ('동물');
INSERT INTO topic (name)
VALUES ('음식');
INSERT INTO topic (name)
VALUES ('스포츠');
INSERT INTO topic (name)
VALUES ('직업');
INSERT INTO topic (name)
VALUES ('물건');
INSERT INTO topic (name)
VALUES ('장소');
INSERT INTO topic (name)
VALUES ('상황');
INSERT INTO topic (name)
VALUES ('전자제품');

-- 동물 (topic ID: 1)
INSERT INTO keyword (name, topic_id)
VALUES ('사자', 1);
INSERT INTO keyword (name, topic_id)
VALUES ('호랑이', 1);
INSERT INTO keyword (name, topic_id)
VALUES ('코끼리', 1);
INSERT INTO keyword (name, topic_id)
VALUES ('기린', 1);
INSERT INTO keyword (name, topic_id)
VALUES ('고양이', 1);
INSERT INTO keyword (name, topic_id)
VALUES ('개', 1);
INSERT INTO keyword (name, topic_id)
VALUES ('돌고래', 1);
INSERT INTO keyword (name, topic_id)
VALUES ('상어', 1);
INSERT INTO keyword (name, topic_id)
VALUES ('펭귄', 1);
INSERT INTO keyword (name, topic_id)
VALUES ('원숭이', 1);
INSERT INTO keyword (name, topic_id)
VALUES ('코알라', 1);
INSERT INTO keyword (name, topic_id)
VALUES ('캥거루', 1);
INSERT INTO keyword (name, topic_id)
VALUES ('독수리', 1);
INSERT INTO keyword (name, topic_id)
VALUES ('올빼미', 1);
INSERT INTO keyword (name, topic_id)
VALUES ('하마', 1);
INSERT INTO keyword (name, topic_id)
VALUES ('얼룩말', 1);
INSERT INTO keyword (name, topic_id)
VALUES ('북극곰', 1);
INSERT INTO keyword (name, topic_id)
VALUES ('늑대', 1);
INSERT INTO keyword (name, topic_id)
VALUES ('사슴', 1);
INSERT INTO keyword (name, topic_id)
VALUES ('다람쥐', 1);
INSERT INTO keyword (name, topic_id)
VALUES ('비버', 1);
INSERT INTO keyword (name, topic_id)
VALUES ('양', 1);
INSERT INTO keyword (name, topic_id)
VALUES ('염소', 1);
INSERT INTO keyword (name, topic_id)
VALUES ('유니콘', 1);
INSERT INTO keyword (name, topic_id)
VALUES ('용', 1);
INSERT INTO keyword (name, topic_id)
VALUES ('페가수스', 1);
INSERT INTO keyword (name, topic_id)
VALUES ('페럿', 1);
INSERT INTO keyword (name, topic_id)
VALUES ('낙타', 1);
INSERT INTO keyword (name, topic_id)
VALUES ('오리너구리', 1);
INSERT INTO keyword (name, topic_id)
VALUES ('미어캣', 1);

-- 음식 (topic ID: 2)
INSERT INTO keyword (name, topic_id)
VALUES ('떡볶이', 2);
INSERT INTO keyword (name, topic_id)
VALUES ('김치찌개', 2);
INSERT INTO keyword (name, topic_id)
VALUES ('된장찌개', 2);
INSERT INTO keyword (name, topic_id)
VALUES ('비빔밥', 2);
INSERT INTO keyword (name, topic_id)
VALUES ('불고기', 2);
INSERT INTO keyword (name, topic_id)
VALUES ('김밥', 2);
INSERT INTO keyword (name, topic_id)
VALUES ('삼겹살', 2);
INSERT INTO keyword (name, topic_id)
VALUES ('갈비', 2);
INSERT INTO keyword (name, topic_id)
VALUES ('잡채', 2);
INSERT INTO keyword (name, topic_id)
VALUES ('전', 2);
INSERT INTO keyword (name, topic_id)
VALUES ('순대', 2);
INSERT INTO keyword (name, topic_id)
VALUES ('찜닭', 2);
INSERT INTO keyword (name, topic_id)
VALUES ('쭈꾸미 볶음', 2);
INSERT INTO keyword (name, topic_id)
VALUES ('치킨', 2);
INSERT INTO keyword (name, topic_id)
VALUES ('피자', 2);
INSERT INTO keyword (name, topic_id)
VALUES ('햄버거', 2);
INSERT INTO keyword (name, topic_id)
VALUES ('파스타', 2);
INSERT INTO keyword (name, topic_id)
VALUES ('스테이크', 2);
INSERT INTO keyword (name, topic_id)
VALUES ('초밥', 2);
INSERT INTO keyword (name, topic_id)
VALUES ('라면', 2);
INSERT INTO keyword (name, topic_id)
VALUES ('떡국', 2);
INSERT INTO keyword (name, topic_id)
VALUES ('오므라이스', 2);
INSERT INTO keyword (name, topic_id)
VALUES ('볶음밥', 2);
INSERT INTO keyword (name, topic_id)
VALUES ('만두', 2);
INSERT INTO keyword (name, topic_id)
VALUES ('핫도그', 2);
INSERT INTO keyword (name, topic_id)
VALUES ('샌드위치', 2);
INSERT INTO keyword (name, topic_id)
VALUES ('스프', 2);
INSERT INTO keyword (name, topic_id)
VALUES ('타코', 2);
INSERT INTO keyword (name, topic_id)
VALUES ('샐러드', 2);
INSERT INTO keyword (name, topic_id)
VALUES ('곤충 튀김', 2);

-- 스포츠 (topic ID: 3)
INSERT INTO keyword (name, topic_id)
VALUES ('축구', 3);
INSERT INTO keyword (name, topic_id)
VALUES ('농구', 3);
INSERT INTO keyword (name, topic_id)
VALUES ('야구', 3);
INSERT INTO keyword (name, topic_id)
VALUES ('배구', 3);
INSERT INTO keyword (name, topic_id)
VALUES ('테니스', 3);
INSERT INTO keyword (name, topic_id)
VALUES ('배드민턴', 3);
INSERT INTO keyword (name, topic_id)
VALUES ('탁구', 3);
INSERT INTO keyword (name, topic_id)
VALUES ('골프', 3);
INSERT INTO keyword (name, topic_id)
VALUES ('수영', 3);
INSERT INTO keyword (name, topic_id)
VALUES ('육상', 3);
INSERT INTO keyword (name, topic_id)
VALUES ('체조', 3);
INSERT INTO keyword (name, topic_id)
VALUES ('스키', 3);
INSERT INTO keyword (name, topic_id)
VALUES ('스노보드', 3);
INSERT INTO keyword (name, topic_id)
VALUES ('아이스하키', 3);
INSERT INTO keyword (name, topic_id)
VALUES ('럭비', 3);
INSERT INTO keyword (name, topic_id)
VALUES ('크리켓', 3);
INSERT INTO keyword (name, topic_id)
VALUES ('핸드볼', 3);
INSERT INTO keyword (name, topic_id)
VALUES ('양궁', 3);
INSERT INTO keyword (name, topic_id)
VALUES ('펜싱', 3);
INSERT INTO keyword (name, topic_id)
VALUES ('사이클링', 3);
INSERT INTO keyword (name, topic_id)
VALUES ('조정', 3);
INSERT INTO keyword (name, topic_id)
VALUES ('카누', 3);
INSERT INTO keyword (name, topic_id)
VALUES ('승마', 3);
INSERT INTO keyword (name, topic_id)
VALUES ('레슬링', 3);
INSERT INTO keyword (name, topic_id)
VALUES ('복싱', 3);
INSERT INTO keyword (name, topic_id)
VALUES ('태권도', 3);
INSERT INTO keyword (name, topic_id)
VALUES ('유도', 3);
INSERT INTO keyword (name, topic_id)
VALUES ('카라테', 3);
INSERT INTO keyword (name, topic_id)
VALUES ('서핑', 3);
INSERT INTO keyword (name, topic_id)
VALUES ('치킨 던지기', 3);

-- 직업 (topic ID: 4)
INSERT INTO keyword (name, topic_id)
VALUES ('의사', 4);
INSERT INTO keyword (name, topic_id)
VALUES ('간호사', 4);
INSERT INTO keyword (name, topic_id)
VALUES ('변호사', 4);
INSERT INTO keyword (name, topic_id)
VALUES ('판사', 4);
INSERT INTO keyword (name, topic_id)
VALUES ('경찰관', 4);
INSERT INTO keyword (name, topic_id)
VALUES ('소방관', 4);
INSERT INTO keyword (name, topic_id)
VALUES ('교사', 4);
INSERT INTO keyword (name, topic_id)
VALUES ('교수', 4);
INSERT INTO keyword (name, topic_id)
VALUES ('요리사', 4);
INSERT INTO keyword (name, topic_id)
VALUES ('바리스타', 4);
INSERT INTO keyword (name, topic_id)
VALUES ('엔지니어', 4);
INSERT INTO keyword (name, topic_id)
VALUES ('프로그래머', 4);
INSERT INTO keyword (name, topic_id)
VALUES ('디자이너', 4);
INSERT INTO keyword (name, topic_id)
VALUES ('건축가', 4);
INSERT INTO keyword (name, topic_id)
VALUES ('회계사', 4);
INSERT INTO keyword (name, topic_id)
VALUES ('기자', 4);
INSERT INTO keyword (name, topic_id)
VALUES ('작가', 4);
INSERT INTO keyword (name, topic_id)
VALUES ('번역가', 4);
INSERT INTO keyword (name, topic_id)
VALUES ('통역사', 4);
INSERT INTO keyword (name, topic_id)
VALUES ('예술가', 4);
INSERT INTO keyword (name, topic_id)
VALUES ('사진가', 4);
INSERT INTO keyword (name, topic_id)
VALUES ('영화 감독', 4);
INSERT INTO keyword (name, topic_id)
VALUES ('음악가', 4);
INSERT INTO keyword (name, topic_id)
VALUES ('가수', 4);
INSERT INTO keyword (name, topic_id)
VALUES ('배우', 4);
INSERT INTO keyword (name, topic_id)
VALUES ('운동 선수', 4);
INSERT INTO keyword (name, topic_id)
VALUES ('매니저', 4);
INSERT INTO keyword (name, topic_id)
VALUES ('세일즈맨', 4);
INSERT INTO keyword (name, topic_id)
VALUES ('마케터', 4);
INSERT INTO keyword (name, topic_id)
VALUES ('연구원', 4);
INSERT INTO keyword (name, topic_id)
VALUES ('UFO 연구가', 4);
INSERT INTO keyword (name, topic_id)
VALUES ('나무꾼', 4);

-- 물건 (topic ID: 5)
INSERT INTO keyword (name, topic_id)
VALUES ('책상', 5);
INSERT INTO keyword (name, topic_id)
VALUES ('의자', 5);
INSERT INTO keyword (name, topic_id)
VALUES ('소파', 5);
INSERT INTO keyword (name, topic_id)
VALUES ('침대', 5);
INSERT INTO keyword (name, topic_id)
VALUES ('책', 5);
INSERT INTO keyword (name, topic_id)
VALUES ('노트북', 5);
INSERT INTO keyword (name, topic_id)
VALUES ('스마트폰', 5);
INSERT INTO keyword (name, topic_id)
VALUES ('냉장고', 5);
INSERT INTO keyword (name, topic_id)
VALUES ('세탁기', 5);
INSERT INTO keyword (name, topic_id)
VALUES ('텔레비전', 5);
INSERT INTO keyword (name, topic_id)
VALUES ('시계', 5);
INSERT INTO keyword (name, topic_id)
VALUES ('안경', 5);
INSERT INTO keyword (name, topic_id)
VALUES ('가방', 5);
INSERT INTO keyword (name, topic_id)
VALUES ('신발', 5);
INSERT INTO keyword (name, topic_id)
VALUES ('옷', 5);
INSERT INTO keyword (name, topic_id)
VALUES ('모자', 5);
INSERT INTO keyword (name, topic_id)
VALUES ('우산', 5);
INSERT INTO keyword (name, topic_id)
VALUES ('컵', 5);
INSERT INTO keyword (name, topic_id)
VALUES ('접시', 5);
INSERT INTO keyword (name, topic_id)
VALUES ('숟가락', 5);
INSERT INTO keyword (name, topic_id)
VALUES ('포크', 5);
INSERT INTO keyword (name, topic_id)
VALUES ('칼', 5);
INSERT INTO keyword (name, topic_id)
VALUES ('가위', 5);
INSERT INTO keyword (name, topic_id)
VALUES ('필통', 5);
INSERT INTO keyword (name, topic_id)
VALUES ('연필', 5);
INSERT INTO keyword (name, topic_id)
VALUES ('볼펜', 5);
INSERT INTO keyword (name, topic_id)
VALUES ('공책', 5);
INSERT INTO keyword (name, topic_id)
VALUES ('컴퓨터', 5);
INSERT INTO keyword (name, topic_id)
VALUES ('라디오', 5);
INSERT INTO keyword (name, topic_id)
VALUES ('타임머신', 5);

-- 장소 (topic ID: 6)
INSERT INTO keyword (name, topic_id)
VALUES ('공원', 6);
INSERT INTO keyword (name, topic_id)
VALUES ('도서관', 6);
INSERT INTO keyword (name, topic_id)
VALUES ('박물관', 6);
INSERT INTO keyword (name, topic_id)
VALUES ('미술관', 6);
INSERT INTO keyword (name, topic_id)
VALUES ('영화관', 6);
INSERT INTO keyword (name, topic_id)
VALUES ('학교', 6);
INSERT INTO keyword (name, topic_id)
VALUES ('대학교', 6);
INSERT INTO keyword (name, topic_id)
VALUES ('병원', 6);
INSERT INTO keyword (name, topic_id)
VALUES ('경찰서', 6);
INSERT INTO keyword (name, topic_id)
VALUES ('소방서', 6);
INSERT INTO keyword (name, topic_id)
VALUES ('은행', 6);
INSERT INTO keyword (name, topic_id)
VALUES ('마트', 6);
INSERT INTO keyword (name, topic_id)
VALUES ('백화점', 6);
INSERT INTO keyword (name, topic_id)
VALUES ('시장', 6);
INSERT INTO keyword (name, topic_id)
VALUES ('카페', 6);
INSERT INTO keyword (name, topic_id)
VALUES ('레스토랑', 6);
INSERT INTO keyword (name, topic_id)
VALUES ('호텔', 6);
INSERT INTO keyword (name, topic_id)
VALUES ('공항', 6);
INSERT INTO keyword (name, topic_id)
VALUES ('기차역', 6);
INSERT INTO keyword (name, topic_id)
VALUES ('버스터미널', 6);
INSERT INTO keyword (name, topic_id)
VALUES ('지하철역', 6);
INSERT INTO keyword (name, topic_id)
VALUES ('해변', 6);
INSERT INTO keyword (name, topic_id)
VALUES ('산', 6);
INSERT INTO keyword (name, topic_id)
VALUES ('강', 6);
INSERT INTO keyword (name, topic_id)
VALUES ('호수', 6);
INSERT INTO keyword (name, topic_id)
VALUES ('섬', 6);
INSERT INTO keyword (name, topic_id)
VALUES ('교회', 6);
INSERT INTO keyword (name, topic_id)
VALUES ('사찰', 6);
INSERT INTO keyword (name, topic_id)
VALUES ('성당', 6);
INSERT INTO keyword (name, topic_id)
VALUES ('외계인 기지', 6);

-- 상황 (topic ID: 7)
INSERT INTO keyword (name, topic_id)
VALUES ('결혼식', 7);
INSERT INTO keyword (name, topic_id)
VALUES ('생일 파티', 7);
INSERT INTO keyword (name, topic_id)
VALUES ('졸업식', 7);
INSERT INTO keyword (name, topic_id)
VALUES ('입학식', 7);
INSERT INTO keyword (name, topic_id)
VALUES ('퇴사식', 7);
INSERT INTO keyword (name, topic_id)
VALUES ('회의', 7);
INSERT INTO keyword (name, topic_id)
VALUES ('발표회', 7);
INSERT INTO keyword (name, topic_id)
VALUES ('면접', 7);
INSERT INTO keyword (name, topic_id)
VALUES ('시험', 7);
INSERT INTO keyword (name, topic_id)
VALUES ('운동회', 7);
INSERT INTO keyword (name, topic_id)
VALUES ('콘서트', 7);
INSERT INTO keyword (name, topic_id)
VALUES ('연극 공연', 7);
INSERT INTO keyword (name, topic_id)
VALUES ('영화 상영', 7);
INSERT INTO keyword (name, topic_id)
VALUES ('가족 모임', 7);
INSERT INTO keyword (name, topic_id)
VALUES ('친구 모임', 7);
INSERT INTO keyword (name, topic_id)
VALUES ('데이트', 7);
INSERT INTO keyword (name, topic_id)
VALUES ('여행', 7);
INSERT INTO keyword (name, topic_id)
VALUES ('캠핑', 7);
INSERT INTO keyword (name, topic_id)
VALUES ('피크닉', 7);
INSERT INTO keyword (name, topic_id)
VALUES ('바비큐 파티', 7);
INSERT INTO keyword (name, topic_id)
VALUES ('쇼핑', 7);
INSERT INTO keyword (name, topic_id)
VALUES ('이사', 7);
INSERT INTO keyword (name, topic_id)
VALUES ('집들이', 7);
INSERT INTO keyword (name, topic_id)
VALUES ('병문안', 7);
INSERT INTO keyword (name, topic_id)
VALUES ('취업', 7);
INSERT INTO keyword (name, topic_id)
VALUES ('승진', 7);
INSERT INTO keyword (name, topic_id)
VALUES ('퇴직', 7);
INSERT INTO keyword (name, topic_id)
VALUES ('봉사활동', 7);
INSERT INTO keyword (name, topic_id)
VALUES ('기념일', 7);
INSERT INTO keyword (name, topic_id)
VALUES ('좀비 공격', 7);

-- 전자제품 (topic ID: 8)
INSERT INTO keyword (name, topic_id)
VALUES ('스마트폰', 8);
INSERT INTO keyword (name, topic_id)
VALUES ('태블릿', 8);
INSERT INTO keyword (name, topic_id)
VALUES ('노트북', 8);
INSERT INTO keyword (name, topic_id)
VALUES ('데스크탑 컴퓨터', 8);
INSERT INTO keyword (name, topic_id)
VALUES ('텔레비전', 8);
INSERT INTO keyword (name, topic_id)
VALUES ('라디오', 8);
INSERT INTO keyword (name, topic_id)
VALUES ('카메라', 8);
INSERT INTO keyword (name, topic_id)
VALUES ('비디오카메라', 8);
INSERT INTO keyword (name, topic_id)
VALUES ('헤드폰', 8);
INSERT INTO keyword (name, topic_id)
VALUES ('이어폰', 8);
INSERT INTO keyword (name, topic_id)
VALUES ('스피커', 8);
INSERT INTO keyword (name, topic_id)
VALUES ('마이크', 8);
INSERT INTO keyword (name, topic_id)
VALUES ('프린터', 8);
INSERT INTO keyword (name, topic_id)
VALUES ('스캐너', 8);
INSERT INTO keyword (name, topic_id)
VALUES ('모니터', 8);
INSERT INTO keyword (name, topic_id)
VALUES ('키보드', 8);
INSERT INTO keyword (name, topic_id)
VALUES ('마우스', 8);
INSERT INTO keyword (name, topic_id)
VALUES ('게임 콘솔', 8);
INSERT INTO keyword (name, topic_id)
VALUES ('드론', 8);
INSERT INTO keyword (name, topic_id)
VALUES ('전자책 리더', 8);
INSERT INTO keyword (name, topic_id)
VALUES ('스마트워치', 8);
INSERT INTO keyword (name, topic_id)
VALUES ('스마트밴드', 8);
INSERT INTO keyword (name, topic_id)
VALUES ('가습기', 8);
INSERT INTO keyword (name, topic_id)
VALUES ('공기청정기', 8);
INSERT INTO keyword (name, topic_id)
VALUES ('전자레인지', 8);
INSERT INTO keyword (name, topic_id)
VALUES ('전기밥솥', 8);
INSERT INTO keyword (name, topic_id)
VALUES ('전기포트', 8);
INSERT INTO keyword (name, topic_id)
VALUES ('헤어드라이어', 8);
INSERT INTO keyword (name, topic_id)
VALUES ('전기다리미', 8);
INSERT INTO keyword (name, topic_id)
VALUES ('로봇 청소기', 8);

-- 흔치 않은 제시어
INSERT INTO keyword (name, topic_id)
VALUES 
('아르마딜로', 1),
('테논네미아', 1),
('판골린', 1),
('다소리스', 1),
('아홀로틀', 1),
('캥거루쥐', 1),
('개미핥기', 1),
('슬로로리스', 1),
('타소', 1),
('퀸즈랜드 재규어', 1),
('이구아나', 1),
('마타마타 거북', 1),
('핑크 돌고래', 1),
('레서 판다', 1),
('마린 이구아나', 1),
('코코아 뱀', 1),
('크리스탈 새우', 1),
('살라만더', 1),
('바실리스크', 1),
('리머', 1),
('나무늘보', 1),
('늑대거미', 1),
('흡혈박쥐', 1),
('가마우지', 1),
('티라노사우루스', 1),
('마스토돈', 1),
('고래상어', 1),
('칼라브로', 1),
('올드잉글리시 쉽독', 1),
('캇소리');

INSERT INTO keyword (name, topic_id)
VALUES 
('흑임자 죽', 2),
('대나무밥', 2),
('군고구마 아이스크림', 2),
('초록콩 페이스트', 2),
('마라 샤브샤브', 2),
('송로버섯 리조또', 2),
('우메보시', 2),
('펑리수', 2),
('말린 오징어 포테이토칩', 2),
('곱창 전골', 2),
('메밀 막국수', 2),
('꽃게 무침', 2),
('바비큐 갈비', 2),
('살사 딥', 2),
('버섯 덮밥', 2),
('타피오카 펄', 2),
('대게찜', 2),
('회덮밥', 2),
('장어 구이', 2),
('매운 갈비찜', 2),
('돼지갈비찜', 2),
('치즈 떡볶이', 2),
('해물 파전', 2),
('파김치', 2),
('오이소박이', 2),
('사과식초 샐러드', 2),
('수제 햄버거', 2),
('대만식 치킨', 2),
('태국식 그린 카레', 2),
('하와이안 피자', 2);

INSERT INTO keyword (name, topic_id)
VALUES 
('세팍타크로', 3),
('고카트', 3),
('인라인 스케이팅', 3),
('클레이 사격', 3),
('양궁', 3),
('크로스핏', 3),
('페인트볼', 3),
('장대높이뛰기', 3),
('줄넘기', 3),
('산악 자전거', 3),
('하이킹', 3),
('암벽 등반', 3),
('패러글라이딩', 3),
('다이빙', 3),
('서핑', 3),
('스포츠클라이밍', 3),
('래프팅', 3),
('윈드서핑', 3),
('수구', 3),
('볼링', 3),
('당구', 3),
('비치발리볼', 3),
('펜싱', 3),
('카바디', 3),
('그리스로마 레슬링', 3),
('카포에라', 3),
('제트스키', 3),
('고래상어 다이빙', 3),
('스노클링', 3),
('조정', 3);

INSERT INTO keyword (name, topic_id)
VALUES 
('플로리스트', 4),
('전기차 수리사', 4),
('데이터 사이언티스트', 4),
('드론 조종사', 4),
('콘텐츠 크리에이터', 4),
('네일 아티스트', 4),
('웹툰 작가', 4),
('3D 프린터 엔지니어', 4),
('자율주행차 엔지니어', 4),
('인공지능 연구원', 4),
('바리스타', 4),
('화가', 4),
('게임 개발자', 4),
('방송 엔지니어', 4),
('실내 건축가', 4),
('큐레이터', 4),
('비행기 조종사', 4),
('치과 의사', 4),
('미용사', 4),
('사회복지사', 4),
('수의사', 4),
('비서', 4),
('재무 분석가', 4),
('컨설턴트', 4),
('파티 플래너', 4),
('패션 디자이너', 4),
('푸드 스타일리스트', 4),
('웨딩 플래너', 4),
('도서관 사서', 4),
('의료 코디네이터', 4);

INSERT INTO keyword (name, topic_id)
VALUES 
('클레이 바', 5),
('스파이더 실드', 5),
('파라솔', 5),
('라디에이터', 5),
('정원 가위', 5),
('지우개 모양 초콜릿', 5),
('미니 피아노', 5),
('와인오프너', 5),
('클래식 라디오', 5),
('레트로 카메라', 5),
('3D 프린터', 5),
('LED 촛대', 5),
('방독면', 5),
('파라코드', 5),
('라텍스 장갑', 5),
('미니어쳐 가구', 5),
('벽걸이 시계', 5),
('비닐 장갑', 5),
('펜 라이트', 5),
('초음파 세척기', 5),
('롤러 브러쉬', 5),
('페인팅 도구 세트', 5),
('캔들 홀더', 5),
('서랍 정리함', 5),
('타투 머신', 5),
('폴라로이드 카메라', 5),
('플라스틱 컵', 5),
('다리미', 5),
('LED 전구', 5),
('무드등', 5);

INSERT INTO keyword (name, topic_id)
VALUES 
('고고학 발굴지', 6),
('얼음 동굴', 6),
('수상 마을', 6),
('타임 스퀘어', 6),
('공장', 6),
('하수구', 6),
('메인프레임 룸', 6),
('지하 벙커', 6),
('우주 정거장', 6),
('산 정상', 6),
('비밀 정원', 6),
('무인도', 6),
('유령 마을', 6),
('마녀의 집', 6),
('태양의 도시', 6),
('빙하 동굴', 6),
('열대우림', 6),
('거미줄 숲', 6),
('산호초', 6),
('해저 도시', 6),
('디지털 갤러리', 6),
('스키 리조트', 6),
('국립 공원', 6),
('암석 지대', 6),
('바다 밑', 6),
('마법의 성', 6),
('동굴', 6),
('바위 절벽', 6),
('일본식 온천', 6),
('황금 사막', 6);

INSERT INTO keyword (name, topic_id)
VALUES 
('화성 탐사', 7),
('잠수함 체험', 7),
('사막 횡단', 7),
('북극 탐험', 7),
('우주여행', 7),
('용암 체험', 7),
('태풍 속에서 생존', 7),
('수중 세계 탐험', 7),
('밀림 서바이벌', 7),
('정글 캠핑', 7),
('거대 파충류 공룡 사냥', 7),
('무인도 생존', 7),
('마법사의 시험', 7),
('가상현실 게임 참가', 7),
('미래 세계로의 여행', 7),
('공포 체험', 7),
('언더워터 호텔 숙박', 7),
('시간여행', 7),
('인공지능과의 대화', 7),
('고대 문명 탐사', 7),
('극지방 서바이벌', 7),
('잠복근무', 7),
('비밀 요원 훈련', 7),
('신화 속 모험', 7),
('외계인 구조작전', 7),
('유령과의 대화', 7),
('수중 화산 탐험', 7),
('미래 도시 탐험', 7),
('타임루프 경험', 7),
('거대 로봇 조종', 7);

INSERT INTO keyword (name, topic_id)
VALUES 
('하이브리드 앰프', 8),
('디지털 포토 프레임', 8),
('스마트 미러', 8),
('전자 다트 보드', 8),
('무선 충전 스탠드', 8),
('네온 사인', 8),
('스마트 리모컨', 8),
('휴대용 선풍기', 8),
('공간 사운드 시스템', 8),
('4D 시뮬레이터', 8),
('스마트 커피 머신', 8),
('무선 진공 청소기', 8),
('고속 충전기', 8),
('스마트 냉장고', 8),
('전기 자전거', 8),
('디지털 알람 시계', 8),
('무드 조명', 8),
('증강 현실 헤드셋', 8),
('자율 주행차', 8),
('스마트 도어락', 8),
('스마트 가구', 8),
('음성 인식 비서', 8),
('터치 스크린 스토브', 8),
('3D 프린터', 8),
('4K 프로젝터', 8),
('자동 창문 블라인드', 8),
('스마트 체중계', 8),
('스마트 침대', 8),
('AI 로봇', 8),
('디지털 키보드', 8);
