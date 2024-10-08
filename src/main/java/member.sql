use firedb;

CREATE TABLE snsuser (
                         username VARCHAR(200) NOT NULL,
                         password VARCHAR(200),
                         name VARCHAR(200),
                         auth VARCHAR(200),
                         PRIMARY KEY (username)
);
CREATE TABLE Gold_Category (
                               srtnCd INT PRIMARY KEY, -- 단축코드 (Primary Key)
                               itmsNm VARCHAR(150) NOT NULL -- 종목명
);
CREATE TABLE Gold_Predict_Prices (
                                     PbasDt VARCHAR(20) PRIMARY KEY, -- 기준 일자
                                     dayPrc INT -- 기준 일자 가격
);
CREATE TABLE Gold_Trading (
                              srtnCd INT, -- 단축코드 (Foreign Key from Gold_Category)
                              clpr VARCHAR(20), -- 종가
                              vs VARCHAR(20), -- 대비
                              fltRt VARCHAR(20), -- 등락률
                              mkp VARCHAR(20), -- 시가
                              hipr VARCHAR(20), -- 고가
                              lopr VARCHAR(20), -- 저가
                              trqu VARCHAR(20), -- 거래량
                              BasDt VARCHAR(20), -- 기준 일자
                              PRIMARY KEY (srtnCd, BasDt), -- 단축코드와 기준일자로 복합키 설정
                              FOREIGN KEY (srtnCd) REFERENCES Gold_Category(srtnCd) -- 외래키 설정
);

-- 5. 회원 테이블 (Member)
CREATE TABLE Member (
                        username VARCHAR(100) PRIMARY KEY, -- snsUser ID
                        platform VARCHAR(20), -- API유형(카카오/구글)
                        nickname VARCHAR(20), -- 닉네임
                        age VARCHAR(10), -- 나이
                        salary INT, -- 급여
                        assets INT, -- 자산
                        riskPoint INT, -- 리스크포인트
                        exp INT, -- 경험포인트
                        goalAmount INT, -- 연간투자목표금액
                        keyword VARCHAR(120), -- 키워드
                        email VARCHAR(100), -- 이메일
                        CONSTRAINT fk_member_username FOREIGN KEY (username) REFERENCES snsUser(username)
);




SELECT COUNT(*) FROM gold_trading;