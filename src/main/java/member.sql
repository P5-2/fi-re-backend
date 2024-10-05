use firedb;

CREATE TABLE snsuser (
                         username VARCHAR(200) NOT NULL,
                         password VARCHAR(200),
                         name VARCHAR(200),
                         auth VARCHAR(200),
                         PRIMARY KEY (username)
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