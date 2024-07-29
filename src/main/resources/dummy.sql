CREATE TABLE IF NOT EXISTS Topic
(
    id   BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS Keyword
(
    id       BIGINT AUTO_INCREMENT PRIMARY KEY,
    name     VARCHAR(255) NOT NULL,
    topic_id BIGINT,
    FOREIGN KEY (topic_id) REFERENCES Topic (id)
);

INSERT INTO Topic (name)
VALUES ('동물');
INSERT INTO Topic (name)
VALUES ('음식');
INSERT INTO Topic (name)
VALUES ('스포츠');
INSERT INTO Topic (name)
VALUES ('직업');
INSERT INTO Topic (name)
VALUES ('물건');
INSERT INTO Topic (name)
VALUES ('장소');
INSERT INTO Topic (name)
VALUES ('상황');
INSERT INTO Topic (name)
VALUES ('전자제품');

-- 동물 (Topic ID: 1)
INSERT INTO Keyword (name, topic_id)
VALUES ('사자', 1);
INSERT INTO Keyword (name, topic_id)
VALUES ('호랑이', 1);
INSERT INTO Keyword (name, topic_id)
VALUES ('코끼리', 1);
INSERT INTO Keyword (name, topic_id)
VALUES ('기린', 1);
INSERT INTO Keyword (name, topic_id)
VALUES ('고양이', 1);
INSERT INTO Keyword (name, topic_id)
VALUES ('개', 1);
INSERT INTO Keyword (name, topic_id)
VALUES ('돌고래', 1);
INSERT INTO Keyword (name, topic_id)
VALUES ('상어', 1);
INSERT INTO Keyword (name, topic_id)
VALUES ('펭귄', 1);
INSERT INTO Keyword (name, topic_id)
VALUES ('원숭이', 1);
INSERT INTO Keyword (name, topic_id)
VALUES ('코알라', 1);
INSERT INTO Keyword (name, topic_id)
VALUES ('캥거루', 1);
INSERT INTO Keyword (name, topic_id)
VALUES ('독수리', 1);
INSERT INTO Keyword (name, topic_id)
VALUES ('올빼미', 1);
INSERT INTO Keyword (name, topic_id)
VALUES ('하마', 1);
INSERT INTO Keyword (name, topic_id)
VALUES ('얼룩말', 1);
INSERT INTO Keyword (name, topic_id)
VALUES ('북극곰', 1);
INSERT INTO Keyword (name, topic_id)
VALUES ('늑대', 1);
INSERT INTO Keyword (name, topic_id)
VALUES ('사슴', 1);
INSERT INTO Keyword (name, topic_id)
VALUES ('다람쥐', 1);
INSERT INTO Keyword (name, topic_id)
VALUES ('비버', 1);
INSERT INTO Keyword (name, topic_id)
VALUES ('양', 1);
INSERT INTO Keyword (name, topic_id)
VALUES ('염소', 1);
INSERT INTO Keyword (name, topic_id)
VALUES ('유니콘', 1);
INSERT INTO Keyword (name, topic_id)
VALUES ('용', 1);
INSERT INTO Keyword (name, topic_id)
VALUES ('페가수스', 1);
INSERT INTO Keyword (name, topic_id)
VALUES ('페럿', 1);
INSERT INTO Keyword (name, topic_id)
VALUES ('낙타', 1);
INSERT INTO Keyword (name, topic_id)
VALUES ('오리너구리', 1);
INSERT INTO Keyword (name, topic_id)
VALUES ('미어캣', 1);

-- 음식 (Topic ID: 2)
INSERT INTO Keyword (name, topic_id)
VALUES ('떡볶이', 2);
INSERT INTO Keyword (name, topic_id)
VALUES ('김치찌개', 2);
INSERT INTO Keyword (name, topic_id)
VALUES ('된장찌개', 2);
INSERT INTO Keyword (name, topic_id)
VALUES ('비빔밥', 2);
INSERT INTO Keyword (name, topic_id)
VALUES ('불고기', 2);
INSERT INTO Keyword (name, topic_id)
VALUES ('김밥', 2);
INSERT INTO Keyword (name, topic_id)
VALUES ('삼겹살', 2);
INSERT INTO Keyword (name, topic_id)
VALUES ('갈비', 2);
INSERT INTO Keyword (name, topic_id)
VALUES ('잡채', 2);
INSERT INTO Keyword (name, topic_id)
VALUES ('전', 2);
INSERT INTO Keyword (name, topic_id)
VALUES ('순대', 2);
INSERT INTO Keyword (name, topic_id)
VALUES ('찜닭', 2);
INSERT INTO Keyword (name, topic_id)
VALUES ('쭈꾸미 볶음', 2);
INSERT INTO Keyword (name, topic_id)
VALUES ('치킨', 2);
INSERT INTO Keyword (name, topic_id)
VALUES ('피자', 2);
INSERT INTO Keyword (name, topic_id)
VALUES ('햄버거', 2);
INSERT INTO Keyword (name, topic_id)
VALUES ('파스타', 2);
INSERT INTO Keyword (name, topic_id)
VALUES ('스테이크', 2);
INSERT INTO Keyword (name, topic_id)
VALUES ('초밥', 2);
INSERT INTO Keyword (name, topic_id)
VALUES ('라면', 2);
INSERT INTO Keyword (name, topic_id)
VALUES ('떡국', 2);
INSERT INTO Keyword (name, topic_id)
VALUES ('오므라이스', 2);
INSERT INTO Keyword (name, topic_id)
VALUES ('볶음밥', 2);
INSERT INTO Keyword (name, topic_id)
VALUES ('만두', 2);
INSERT INTO Keyword (name, topic_id)
VALUES ('핫도그', 2);
INSERT INTO Keyword (name, topic_id)
VALUES ('샌드위치', 2);
INSERT INTO Keyword (name, topic_id)
VALUES ('스프', 2);
INSERT INTO Keyword (name, topic_id)
VALUES ('타코', 2);
INSERT INTO Keyword (name, topic_id)
VALUES ('샐러드', 2);
INSERT INTO Keyword (name, topic_id)
VALUES ('곤충 튀김', 2);

-- 스포츠 (Topic ID: 3)
INSERT INTO Keyword (name, topic_id)
VALUES ('축구', 3);
INSERT INTO Keyword (name, topic_id)
VALUES ('농구', 3);
INSERT INTO Keyword (name, topic_id)
VALUES ('야구', 3);
INSERT INTO Keyword (name, topic_id)
VALUES ('배구', 3);
INSERT INTO Keyword (name, topic_id)
VALUES ('테니스', 3);
INSERT INTO Keyword (name, topic_id)
VALUES ('배드민턴', 3);
INSERT INTO Keyword (name, topic_id)
VALUES ('탁구', 3);
INSERT INTO Keyword (name, topic_id)
VALUES ('골프', 3);
INSERT INTO Keyword (name, topic_id)
VALUES ('수영', 3);
INSERT INTO Keyword (name, topic_id)
VALUES ('육상', 3);
INSERT INTO Keyword (name, topic_id)
VALUES ('체조', 3);
INSERT INTO Keyword (name, topic_id)
VALUES ('스키', 3);
INSERT INTO Keyword (name, topic_id)
VALUES ('스노보드', 3);
INSERT INTO Keyword (name, topic_id)
VALUES ('아이스하키', 3);
INSERT INTO Keyword (name, topic_id)
VALUES ('럭비', 3);
INSERT INTO Keyword (name, topic_id)
VALUES ('크리켓', 3);
INSERT INTO Keyword (name, topic_id)
VALUES ('핸드볼', 3);
INSERT INTO Keyword (name, topic_id)
VALUES ('양궁', 3);
INSERT INTO Keyword (name, topic_id)
VALUES ('펜싱', 3);
INSERT INTO Keyword (name, topic_id)
VALUES ('사이클링', 3);
INSERT INTO Keyword (name, topic_id)
VALUES ('조정', 3);
INSERT INTO Keyword (name, topic_id)
VALUES ('카누', 3);
INSERT INTO Keyword (name, topic_id)
VALUES ('승마', 3);
INSERT INTO Keyword (name, topic_id)
VALUES ('레슬링', 3);
INSERT INTO Keyword (name, topic_id)
VALUES ('복싱', 3);
INSERT INTO Keyword (name, topic_id)
VALUES ('태권도', 3);
INSERT INTO Keyword (name, topic_id)
VALUES ('유도', 3);
INSERT INTO Keyword (name, topic_id)
VALUES ('카라테', 3);
INSERT INTO Keyword (name, topic_id)
VALUES ('서핑', 3);
INSERT INTO Keyword (name, topic_id)
VALUES ('치킨 던지기', 3);

-- 직업 (Topic ID: 4)
INSERT INTO Keyword (name, topic_id)
VALUES ('의사', 4);
INSERT INTO Keyword (name, topic_id)
VALUES ('간호사', 4);
INSERT INTO Keyword (name, topic_id)
VALUES ('변호사', 4);
INSERT INTO Keyword (name, topic_id)
VALUES ('판사', 4);
INSERT INTO Keyword (name, topic_id)
VALUES ('경찰관', 4);
INSERT INTO Keyword (name, topic_id)
VALUES ('소방관', 4);
INSERT INTO Keyword (name, topic_id)
VALUES ('교사', 4);
INSERT INTO Keyword (name, topic_id)
VALUES ('교수', 4);
INSERT INTO Keyword (name, topic_id)
VALUES ('요리사', 4);
INSERT INTO Keyword (name, topic_id)
VALUES ('바리스타', 4);
INSERT INTO Keyword (name, topic_id)
VALUES ('엔지니어', 4);
INSERT INTO Keyword (name, topic_id)
VALUES ('프로그래머', 4);
INSERT INTO Keyword (name, topic_id)
VALUES ('디자이너', 4);
INSERT INTO Keyword (name, topic_id)
VALUES ('건축가', 4);
INSERT INTO Keyword (name, topic_id)
VALUES ('회계사', 4);
INSERT INTO Keyword (name, topic_id)
VALUES ('기자', 4);
INSERT INTO Keyword (name, topic_id)
VALUES ('작가', 4);
INSERT INTO Keyword (name, topic_id)
VALUES ('번역가', 4);
INSERT INTO Keyword (name, topic_id)
VALUES ('통역사', 4);
INSERT INTO Keyword (name, topic_id)
VALUES ('예술가', 4);
INSERT INTO Keyword (name, topic_id)
VALUES ('사진가', 4);
INSERT INTO Keyword (name, topic_id)
VALUES ('영화 감독', 4);
INSERT INTO Keyword (name, topic_id)
VALUES ('음악가', 4);
INSERT INTO Keyword (name, topic_id)
VALUES ('가수', 4);
INSERT INTO Keyword (name, topic_id)
VALUES ('배우', 4);
INSERT INTO Keyword (name, topic_id)
VALUES ('운동 선수', 4);
INSERT INTO Keyword (name, topic_id)
VALUES ('매니저', 4);
INSERT INTO Keyword (name, topic_id)
VALUES ('세일즈맨', 4);
INSERT INTO Keyword (name, topic_id)
VALUES ('마케터', 4);
INSERT INTO Keyword (name, topic_id)
VALUES ('연구원', 4);
INSERT INTO Keyword (name, topic_id)
VALUES ('UFO 연구가', 4);

-- 물건 (Topic ID: 5)
INSERT INTO Keyword (name, topic_id)
VALUES ('책상', 5);
INSERT INTO Keyword (name, topic_id)
VALUES ('의자', 5);
INSERT INTO Keyword (name, topic_id)
VALUES ('소파', 5);
INSERT INTO Keyword (name, topic_id)
VALUES ('침대', 5);
INSERT INTO Keyword (name, topic_id)
VALUES ('책', 5);
INSERT INTO Keyword (name, topic_id)
VALUES ('노트북', 5);
INSERT INTO Keyword (name, topic_id)
VALUES ('스마트폰', 5);
INSERT INTO Keyword (name, topic_id)
VALUES ('냉장고', 5);
INSERT INTO Keyword (name, topic_id)
VALUES ('세탁기', 5);
INSERT INTO Keyword (name, topic_id)
VALUES ('텔레비전', 5);
INSERT INTO Keyword (name, topic_id)
VALUES ('시계', 5);
INSERT INTO Keyword (name, topic_id)
VALUES ('안경', 5);
INSERT INTO Keyword (name, topic_id)
VALUES ('가방', 5);
INSERT INTO Keyword (name, topic_id)
VALUES ('신발', 5);
INSERT INTO Keyword (name, topic_id)
VALUES ('옷', 5);
INSERT INTO Keyword (name, topic_id)
VALUES ('모자', 5);
INSERT INTO Keyword (name, topic_id)
VALUES ('우산', 5);
INSERT INTO Keyword (name, topic_id)
VALUES ('컵', 5);
INSERT INTO Keyword (name, topic_id)
VALUES ('접시', 5);
INSERT INTO Keyword (name, topic_id)
VALUES ('숟가락', 5);
INSERT INTO Keyword (name, topic_id)
VALUES ('포크', 5);
INSERT INTO Keyword (name, topic_id)
VALUES ('칼', 5);
INSERT INTO Keyword (name, topic_id)
VALUES ('가위', 5);
INSERT INTO Keyword (name, topic_id)
VALUES ('필통', 5);
INSERT INTO Keyword (name, topic_id)
VALUES ('연필', 5);
INSERT INTO Keyword (name, topic_id)
VALUES ('볼펜', 5);
INSERT INTO Keyword (name, topic_id)
VALUES ('공책', 5);
INSERT INTO Keyword (name, topic_id)
VALUES ('컴퓨터', 5);
INSERT INTO Keyword (name, topic_id)
VALUES ('라디오', 5);
INSERT INTO Keyword (name, topic_id)
VALUES ('타임머신', 5);

-- 장소 (Topic ID: 6)
INSERT INTO Keyword (name, topic_id)
VALUES ('공원', 6);
INSERT INTO Keyword (name, topic_id)
VALUES ('도서관', 6);
INSERT INTO Keyword (name, topic_id)
VALUES ('박물관', 6);
INSERT INTO Keyword (name, topic_id)
VALUES ('미술관', 6);
INSERT INTO Keyword (name, topic_id)
VALUES ('영화관', 6);
INSERT INTO Keyword (name, topic_id)
VALUES ('학교', 6);
INSERT INTO Keyword (name, topic_id)
VALUES ('대학교', 6);
INSERT INTO Keyword (name, topic_id)
VALUES ('병원', 6);
INSERT INTO Keyword (name, topic_id)
VALUES ('경찰서', 6);
INSERT INTO Keyword (name, topic_id)
VALUES ('소방서', 6);
INSERT INTO Keyword (name, topic_id)
VALUES ('은행', 6);
INSERT INTO Keyword (name, topic_id)
VALUES ('마트', 6);
INSERT INTO Keyword (name, topic_id)
VALUES ('백화점', 6);
INSERT INTO Keyword (name, topic_id)
VALUES ('시장', 6);
INSERT INTO Keyword (name, topic_id)
VALUES ('카페', 6);
INSERT INTO Keyword (name, topic_id)
VALUES ('레스토랑', 6);
INSERT INTO Keyword (name, topic_id)
VALUES ('호텔', 6);
INSERT INTO Keyword (name, topic_id)
VALUES ('공항', 6);
INSERT INTO Keyword (name, topic_id)
VALUES ('기차역', 6);
INSERT INTO Keyword (name, topic_id)
VALUES ('버스터미널', 6);
INSERT INTO Keyword (name, topic_id)
VALUES ('지하철역', 6);
INSERT INTO Keyword (name, topic_id)
VALUES ('해변', 6);
INSERT INTO Keyword (name, topic_id)
VALUES ('산', 6);
INSERT INTO Keyword (name, topic_id)
VALUES ('강', 6);
INSERT INTO Keyword (name, topic_id)
VALUES ('호수', 6);
INSERT INTO Keyword (name, topic_id)
VALUES ('섬', 6);
INSERT INTO Keyword (name, topic_id)
VALUES ('교회', 6);
INSERT INTO Keyword (name, topic_id)
VALUES ('사찰', 6);
INSERT INTO Keyword (name, topic_id)
VALUES ('성당', 6);
INSERT INTO Keyword (name, topic_id)
VALUES ('외계인 기지', 6);

-- 상황 (Topic ID: 7)
INSERT INTO Keyword (name, topic_id)
VALUES ('결혼식', 7);
INSERT INTO Keyword (name, topic_id)
VALUES ('생일 파티', 7);
INSERT INTO Keyword (name, topic_id)
VALUES ('졸업식', 7);
INSERT INTO Keyword (name, topic_id)
VALUES ('입학식', 7);
INSERT INTO Keyword (name, topic_id)
VALUES ('퇴사식', 7);
INSERT INTO Keyword (name, topic_id)
VALUES ('회의', 7);
INSERT INTO Keyword (name, topic_id)
VALUES ('발표회', 7);
INSERT INTO Keyword (name, topic_id)
VALUES ('면접', 7);
INSERT INTO Keyword (name, topic_id)
VALUES ('시험', 7);
INSERT INTO Keyword (name, topic_id)
VALUES ('운동회', 7);
INSERT INTO Keyword (name, topic_id)
VALUES ('콘서트', 7);
INSERT INTO Keyword (name, topic_id)
VALUES ('연극 공연', 7);
INSERT INTO Keyword (name, topic_id)
VALUES ('영화 상영', 7);
INSERT INTO Keyword (name, topic_id)
VALUES ('가족 모임', 7);
INSERT INTO Keyword (name, topic_id)
VALUES ('친구 모임', 7);
INSERT INTO Keyword (name, topic_id)
VALUES ('데이트', 7);
INSERT INTO Keyword (name, topic_id)
VALUES ('여행', 7);
INSERT INTO Keyword (name, topic_id)
VALUES ('캠핑', 7);
INSERT INTO Keyword (name, topic_id)
VALUES ('피크닉', 7);
INSERT INTO Keyword (name, topic_id)
VALUES ('바비큐 파티', 7);
INSERT INTO Keyword (name, topic_id)
VALUES ('쇼핑', 7);
INSERT INTO Keyword (name, topic_id)
VALUES ('이사', 7);
INSERT INTO Keyword (name, topic_id)
VALUES ('집들이', 7);
INSERT INTO Keyword (name, topic_id)
VALUES ('병문안', 7);
INSERT INTO Keyword (name, topic_id)
VALUES ('취업', 7);
INSERT INTO Keyword (name, topic_id)
VALUES ('승진', 7);
INSERT INTO Keyword (name, topic_id)
VALUES ('퇴직', 7);
INSERT INTO Keyword (name, topic_id)
VALUES ('봉사활동', 7);
INSERT INTO Keyword (name, topic_id)
VALUES ('기념일', 7);
INSERT INTO Keyword (name, topic_id)
VALUES ('좀비 공격', 7);

-- 전자제품 (Topic ID: 8)
INSERT INTO Keyword (name, topic_id)
VALUES ('스마트폰', 8);
INSERT INTO Keyword (name, topic_id)
VALUES ('태블릿', 8);
INSERT INTO Keyword (name, topic_id)
VALUES ('노트북', 8);
INSERT INTO Keyword (name, topic_id)
VALUES ('데스크탑 컴퓨터', 8);
INSERT INTO Keyword (name, topic_id)
VALUES ('텔레비전', 8);
INSERT INTO Keyword (name, topic_id)
VALUES ('라디오', 8);
INSERT INTO Keyword (name, topic_id)
VALUES ('카메라', 8);
INSERT INTO Keyword (name, topic_id)
VALUES ('비디오카메라', 8);
INSERT INTO Keyword (name, topic_id)
VALUES ('헤드폰', 8);
INSERT INTO Keyword (name, topic_id)
VALUES ('이어폰', 8);
INSERT INTO Keyword (name, topic_id)
VALUES ('스피커', 8);
INSERT INTO Keyword (name, topic_id)
VALUES ('마이크', 8);
INSERT INTO Keyword (name, topic_id)
VALUES ('프린터', 8);
INSERT INTO Keyword (name, topic_id)
VALUES ('스캐너', 8);
INSERT INTO Keyword (name, topic_id)
VALUES ('모니터', 8);
INSERT INTO Keyword (name, topic_id)
VALUES ('키보드', 8);
INSERT INTO Keyword (name, topic_id)
VALUES ('마우스', 8);
INSERT INTO Keyword (name, topic_id)
VALUES ('게임 콘솔', 8);
INSERT INTO Keyword (name, topic_id)
VALUES ('드론', 8);
INSERT INTO Keyword (name, topic_id)
VALUES ('전자책 리더', 8);
INSERT INTO Keyword (name, topic_id)
VALUES ('스마트워치', 8);
INSERT INTO Keyword (name, topic_id)
VALUES ('스마트밴드', 8);
INSERT INTO Keyword (name, topic_id)
VALUES ('가습기', 8);
INSERT INTO Keyword (name, topic_id)
VALUES ('공기청정기', 8);
INSERT INTO Keyword (name, topic_id)
VALUES ('전자레인지', 8);
INSERT INTO Keyword (name, topic_id)
VALUES ('전기밥솥', 8);
INSERT INTO Keyword (name, topic_id)
VALUES ('전기포트', 8);
INSERT INTO Keyword (name, topic_id)
VALUES ('헤어드라이어', 8);
INSERT INTO Keyword (name, topic_id)
VALUES ('전기다리미', 8);
INSERT INTO Keyword (name, topic_id)
VALUES ('로봇 청소기', 8);