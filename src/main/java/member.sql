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

CREATE TABLE MemberSavings (
                               id BIGINT AUTO_INCREMENT PRIMARY KEY,  -- 고유 ID
                               username VARCHAR(200),  -- 회원의 SNS User ID
                               goalName VARCHAR(50),  -- 회원이 설정한 목표 이름
                               finPrdtCd VARCHAR(30),  -- 상품 번호 (고유 상품 코드)
                               accountNum VARCHAR(100), -- 계좌번호
                               savedAmount INT,  -- 현재까지 저금한 금액
                               monthlyDeposit INT,  -- 이번 달 입금 금액
                               startDate DATE,  -- 가입일
                               endDate DATE,  -- 만기일
                               targetAmount INT, -- 상품의 목표 저금 금액
                               CONSTRAINT fk_membersavings_username FOREIGN KEY (username) REFERENCES Member(username) ON DELETE CASCADE
);
CREATE TABLE savingsDeposit (
                                fin_prdt_cd VARCHAR(60) PRIMARY KEY,
                                kor_co_nm VARCHAR(100),
                                fin_prdt_nm VARCHAR(100),
                                join_way VARCHAR(100),
                                spcl_cnd TEXT,
                                join_member VARCHAR(100),
                                etc_note TEXT,
                                max_limit BIGINT,
                                prdt_div CHAR(1),
                                selectCount INT DEFAULT 0
);

-- 예적금 상품 옵션 테이블
CREATE TABLE savingsDeposit_options (
                                        id INT AUTO_INCREMENT PRIMARY KEY,
                                        fin_prdt_cd VARCHAR(60),
                                        intr_rate_type_nm VARCHAR(20),
                                        save_trm VARCHAR(10),
                                        intr_rate DECIMAL(5,2),
                                        intr_rate2 DECIMAL(5,2),
                                        FOREIGN KEY (fin_prdt_cd) REFERENCES savingsDeposit(fin_prdt_cd) ON DELETE CASCADE
);

-- 인덱스 추가
CREATE INDEX idx_prdt_div ON savingsDeposit(prdt_div);
CREATE INDEX idx_selectCount ON savingsDeposit(selectCount);
CREATE INDEX idx_fin_prdt_cd ON savingsDeposit_options(fin_prdt_cd);
ALTER TABLE savingsDeposit_options ADD COLUMN rsrv_type CHAR(1);

— 거래 내역 테이블

CREATE TABLE Transactions (
                              transactionId INT AUTO_INCREMENT PRIMARY KEY,  -- 거래의 고유 ID
                              username VARCHAR(200),                          -- 이용자의 ID (Member 테이블의 외래키)
                              type VARCHAR(10),                              -- 거래 유형 (출금, 입금 등)
                              description VARCHAR(255),                      -- 거래 설명 (예: A통장 입금, 쇼핑몰 결제 등)
                              amount DECIMAL(10, 2),                         -- 거래 금액
                              remitaccount VARCHAR(100), -- 송금 계좌번호
                              transactionDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- 거래 발생 시간
                              FOREIGN KEY (username) REFERENCES Member(username)  -- Member 테이블과의 관계 설정
);

INSERT INTO Transactions (username, type, description, amount, remitaccount)
VALUES (’username', '출금', '식당 결제', 50000, '987-6543-2101'),
('username', '출금', '유럽여행을가자', 200000, '001-2345-6789'),
('username', '출금', '유럽여행을가자', 150000, '001-2345-6789'),
('username', '출금', '대전역CU', 30000, '987-6543-2101'),
('username', '출금', '시드머니모으기', 50000, '100-1233-4242');


