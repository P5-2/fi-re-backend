use firedb;

-- 1. 예적금 테이블 (Savings)
create table savings(
                        prdNo int primary key auto_increment,
                        pname varchar(50),
                        type char(12),
                        bname varchar(50),
                        minRate decimal(5, 2),
                        maxRate decimal(5, 2),
                        subPeriod int,
                        subAmount varchar(50),
                        target varchar(80),
                        benefit varchar(200),
                        description text,
                        selectCount int,
                        keyword varchar(200)
);

-- 예적금 더미 데이터 삽입
-- 예적금 데이터 삽입(국민은행)
insert into savings(pname, type, bname, minRate, maxRate, subPeriod, subAmount, target, benefit, description, selectCount, keyword)
values ("KB Star 정기예금", "예금", "국민은행", 2.7, 3.4, 36, "1백만원 이상", "개인", "금리우대쿠폰 사용시 우대 이율 제공", "목돈 불리는 예금, 만기되면 자동으로 재예치! 급할 땐 해지 하지 않아도 분할 인출 가능!!", 0, "소액,장기"),
       ("KB국민UP정기예금", "예금", "국민은행", 3.2, 3.8, 48, 3000000, "3백만원 이상", "KB카드 이용실적에 따라 우대 이율 제공", "이율이 높은 목돈 굴리는 예금, 급할 땐 해지 하지 않아도 일부 출금 가능!", 0, "고액,장기"),
       ("국민수퍼정기예금", "예금", "국민은행", 1.85, 2.8, 24, 1000000, "1백만원 이상", "금리우대쿠폰을 적용한 경우 우대 이율 제공", "가입자가 이율, 이자지급, 만기일 등을 직접 설계하여 저축할 수 있는 다기능 맞춤식 정기예금", 0, "소액,단기"),
       ("KB내맘대로적금", "적금", "국민은행", 2.3, 3.75, 36, "월 1만원 이상 3백만원 이하", "개인", "급여이체, 카드결제계좌, 자동이체 저축, 아파트관리비 이체, KB스타뱅킹 이체, 장기거래, 첫 거래, 주택청약종합저축, 소중한 날 총 9가지 조건 중 6가지에 만족할 경우 우대 이율 제공", "내 맘대로 선택하는 우대 이율 조건, 다양한 보험 서비스 무료 가입 혜택을 제공하는 적금", 0, "소액,장기"),
       ("KB스타적금", "적금", "국민은행", 2, 8, 12, "월 1만원 이상 30만원 이하", "개인", "적금 가입일부터 만기일까지 월1회 스탬프 찍기를 한 경우 연 1% 이율 증가, 적금 신규 전일 기준 최근 6개월동안 국민은행 상품을 보유하지 않았거나 입출금이 자유로운 예금만 보유한 고객의 경우 연 3% 이율 증가", "신규 또는 장기미거래 고객에게 우대이율을 제공하는 적금", 0, "소액,단기");

-- 예적금 데이터 삽입(우리은행)
insert into savings(pname, type, bname, minRate, maxRate, subPeriod, subAmount, target, benefit, description, selectCount, keyword)
values ("WON 적금", "적금", "우리은행", 3.7, 3.9, 12, "월 최대 50만원", "개인", "WON통장/우리꿈통장에서 출금하여 가입시 연 0.1%, 만기해지 시 우리 오픈뱅킹 서비스에 타행계좌가 등록되어 있는 경우 연 0.1% 우대금리 제공", "스마트폰으로 간편하게, 복잡하지 않고 간단한 목금모으는 적금!", 0, "소액,단기"),
       ("우리 SUPER주거래 정기적금", "적금", "우리은행", 2.85, 4.75, 36, "월 최대 50만원", "개인", "우리은행 첫거래 고객 연 1.0%, 급여이체 또는 연금이체 실적 연 0.5%, 공과금 자동이체 연 0.2%, 우리카드결제(월 10만원이상) 및 당행 결제계좌 지정 연 0.2% 우대금리 제공", "주거래고객님께 더 높은 금리를, 급여·연금·공과금 이체하고 주거래고객 되시면 실적에 따라 우대금리 제공! ", 0, "소액,장기"),
       ("WON플러스 예금", "예금", "우리은행", 2.7, 3.42, 12, "1만원 이상", "개인", "가입 기간에 따라 우대금리 제공", "기간도 금액도 내맘대로 예금, 만기를 선택하여 자유롭게 가입이 가능한 예금상품", 0, "고액,단기"),
       ("우리 퍼스트 정기적금", "적금", "우리은행", 2.2, 5.2, 12, "월 최대 50만원", "개인", "직전 1년 동안 우리은행 예적금 밉유 고객 : 연 3.0% 우대금리 제공", "예적금 미보유 고객에게 우대금리 혜택을 주는 적금상품", 0, "소액,단기");

-- 예적금 데이터 삽입(신한은행)
insert into savings(pname, type, bname, minRate, maxRate, subPeriod, subAmount, target, benefit, description, selectCount, keyword)
values ("신한 My플러스 정기예금", "예금", "신한은행", 2.65, 2.85, 12, "50만원부터 1억원까지", "개인", "예금 가입일 기준 최근 6개월간 정기예금 미보유 고객인 경우 0.1%, 예금 보유기간 중 예금주 명의의 신한은행 입출금 통장에 건 별 50만원 이상 소득 입금 발생 시 연 0.1% 우대금리 제공", "요건 달성 시 우대이자율을 제공하는 정기예금", 0, "고액,단기"),
       ("쏠편한 정기예금", "예금", "신한은행", 2.7, 2.95, 60, "1만원부터 제한없음", "개인", "인터넷으로 가입 시 우대이율 제공", "만기일 연장 서비스를 통해 여유롭게 관리할 수 있는 정기예금", 0, "고액,장기"),
       ("신한 S드림 정기예금", "예금", "신한은행", 2.15, 2.7, 60, "300만원부터 제한없음", "개인", "가입한 기간에 따라 이율 적용", "다양한 기간 설정, 이자지급방법 및 재예치 설정기능으로 고객님 자금운용계획에 맞추어 운용할 수 있는 맞춤형 정기예금", 0, "고액,장기"),
       ("청년 처음적금", "적금", "신한은행", 3.5, 8.0, 12, "월 1만원 이상 30만원 이하", "개인", "상품 가입 후 급여이체 실적 또는 급여클럽 월급 봉투를 6개월 이상 받은 경우 연 1.0%, 본인명의 신한은행 입출금 통장으로 신한카드 결제 실적 6개월 이상 연 0.5%, 신한 슈퍼SOL 앱 회원가입 연 0.5%, 이 예금 신규 직전 1년 간 신한은행 금융상품을 보유하지 않은 경우 연 2.5% 우대이율 적용", "만 18세~39세 모든 청년을 위한 적금! 목돈모으기는 처음적금과 함께", 0, "소액,단기"),
       ("쓸수록 모이는 소비적금", "적금", "신한은행", 1.8, 6, 6, "월 1만원 이상 10만원 이하", "개인", "신한은행의 개인정보 수집·이용 동의서에 동의 시 연 0.5%, 적금 거래 기간 중 당행 입출금계좌를 카카오페이 충전 주계좌로 등록 시 연 0.5%, 카카오페이 월별 충전 실적이 월 1회 이상이면서 월 1만원 이상인 경우 연 3.2% 우대이율 제공", "카카오페이의 간편결제 서비스를 이용하는 고객을 대상으로, 신한은행 유동성 계좌를 통한 카카오페이 간편겨려제 서비스 이용 현황에 따라 우대이율을 제공하는 정기 적금 상품", 0, "소액,단기"),
       ("신한 슈퍼SOL 포인트 적금", "적금", "신한은행", 2, 5, 6, "월 1만원 이상 30만원 이하", "개인", "신한카드 결제계좌를 신한은행으로 지정한 경우 연 0.5%, 이 상품에 마이신한포인트 월 1,000포인트 이상 입금할 경우 연 2.5% 우대이율 제공", "신한 슈퍼SOL에서만 가입할 수 있는 전용상품으로, 마이신한포인트 입금고객에게 우대이율을 제공하는 자유적립식 상품", 0, "소액,단기");

-- 예적금 데이터 삽입(하나은행)
insert into savings(pname, type, bname, minRate, maxRate, subPeriod, subAmount, target, benefit, description, selectCount, keyword)
values ("3·6·9 정기예금", "예금", "하나은행", 1.5, 3, 12, "3백만원 이상 1억원 이하", "개인", "가입 기간에 따라 이율 제공", "3개월마다 높은 금리로 갈아탈 수 있는 옵션 보너스 제공, 입출금과 거치식 예금의 장점만을 모은 편리하고 유용한 상품", 0, "고액,단기"),
       ("고단위 플러스", "예금", "하나은행", 2.0, 2.7, 60, "1백만원 이상", "개인", "가입 기간에 따라 이율 제공", "만기해지시 원금과 함께 지급되는 기본적인 적금 상품", 0, "고액,장기"),
       ("하나의 정기예금", "예금", "하나은행", 2.0, 2.7, 60, "1백만원 이상", "개인", "가입 기간에 따라 이율 제공", "계약기간 및 가입금액이 자유롭고 자동재예치를 통해 자금관리가 가능한 하나원큐 전용 정기예금", 0, "고액,장기"),
       ("하나 청년도약 적금", "적금", "하나은행", 4.5, 5.5, 60, "1만원 이상 70만원 이하", "개인", "예금 가입 후 본행 입출금통장을 통해 36회차 이상 급여 입금 시 연 0.6%, 예금 가입 후 하나은행 입출금 통장을 통해 월 합산 10만원 이상의 하나카드 결제 실적을 보유한 경우 연 0.2%, 예금 가입일로부터 직전 1년간 적금 또는 예금을 미 보유한 경우 연 0.1%, 예금 가입 전 하나은행 상품,서비스 마케팅에 동의한 경우 연 0.1% 우대금리 제공", "청년의 중장기 자산형성 지원을 위한 금융 상품으로, 정부 기여금 및 비과세 혜택을 제공하는 적립식 상품", 0, "고액,장기"),
       ("급여하나 월복리 적금", "적금", "하나은행", 3.35, 4.35, 12, "1만원 이상 300만원 이하", "개인", "계약기간의 1/2 이상 본인 명의 하나은행 입출금통장을 통해 급여 입금실적을 보유한 경우 연 0.9%, 이 예금을 온라인채널을 통해 가입 또는 만기재예치하는 경우 연 0.1% 우대이율 제공", "급여 하나로 OK! 월복리로 이자에 이자가 OK!", 0,"소액,단기"),
       ("(내맘) 적금", "적금", "하나은행", 2.8, 3.3, 60, "월 1만원 이상 1천만원 이하", "개인", "예금 가입 후 만기 전까지 본행의 입출금통장을 통해ㅐ 계약기간의 1/2이상 월부금 자동이체실적을 충족한 경우 연 0.5% 우대금리 제공", "저축금액, 만기일, 자동이체 구간까지 내 맘대로 디자인하는 DLY적금", 0, "고액,장기");

-- 2. 펀드 테이블 (Fund)

DROP TABLE IF EXISTS `fund`;
-- fund 테이블 생성
CREATE TABLE `fund` (
  `prdNo` int NOT NULL AUTO_INCREMENT,
  `pname` varchar(100) DEFAULT NULL,
  `type` varchar(50) DEFAULT NULL,
  `rate` decimal(5,2) DEFAULT NULL,
  `dngrGrade` int DEFAULT NULL,
  `region` char(12) DEFAULT NULL,
  `bseDt` timestamp NULL DEFAULT NULL,
  `selectCount` int DEFAULT NULL,
  `NAV` decimal(10,2) DEFAULT NULL,
  `sixMRate` decimal(5,2) DEFAULT NULL,
  `oneYRate` decimal(5,2) DEFAULT NULL,
  PRIMARY KEY (`prdNo`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

insert into fund(pname, type, rate, dngrGrade, region, bseDt, selectCount)
values ("하나 파워 e단기채 증권자투자신탁", "채권형", 1.3, 6, "국내", "2017/02/07", 0),
       ("한화 스마트 법인 MMF 1호", "MMF", 3.5, 6, "국내", "2011/01/25", 0),
       ("우리 단기채권 증권투자신탁", "채권형", 1.2, 6, "국내", "2014/02/05", 0),
       ("파인만 신종 개인 MMF A-1호", "MMF", 2.8, 5, "해외", "2012/08/12", 0),
       ("KB스타 법인용 MMF I-101호", "MMF", 3.2, 4, "해외", "2015/06/15", 0),
       ("파인만 신우량 개인 MMF 3호", "MMF", 2.24, 5, "국내", "2014/05/25", 0),
       ("NH-Amundi 개인 MMF 1호", "MMF", 3.08, 4, "국내", "2016/07/21", 0),
       ("미래에셋 개인솔로몬 MMF", "MMF", 2.94, 5, "국내", "2013/10/12", 0),
       ("NH-Amundi 하나로 단기채 증권투자신탁", "채권형", 1.16, 3, "국내", "2015/05/10", 0),
       ("KB 스타 단기채 증권자투자신탁", "채권형", 3.8, 3, "국내", "2017/08/24", 0),
       ("KB 연금 단기채 증권 전환형 자투자신탁", "채권형", 1.7, 4, "국내", "2016/04/10", 0),
       ("KB AI주식 분석 투자", "주식형", 1.5, 3, "국내", "2022/10/15", 0),
       ("KB스타 단기 국공채 증권자투자신탁", "채권형", 1.15, 3, "국내", "2016/01/11", 0),
       ("한화 단기회사채 증권자투자신탁", "채권형", 1.9, 4, "국내", "2018/08/18", 0),
       ("유진 챔피언 단기채 증권자투자신탁", "채권형", 2.84, 2, "국내", "2020/06/18", 0),
       ("한화 단기회사채 증권자투자신탁", "채권형", 1.02, 2, "국내", "2016/06/20", 0),
       ("미래에셋 장기투자신탁", "주식형", 5.07, 1, "해외", "2018/04/22", 0),
       ("파인만 해외분석투자신탁", "채권형", 3.08, 1, "해외", "2017/11/05", 0),
       ("우리 신우량 법인 MMF", "MMF", 0.87, 2, "국내", "2015/09/21", 0),
       ("NH-Amundi 하나로 해외시장 AI분석 투자", "주식형", 2.28, 1, "해외", "2023/12/21", 0);

-- 3. 뉴스 테이블 (News)
CREATE TABLE News (
                      no INT PRIMARY KEY, -- 뉴스 번호
                      lastBuildDate DATE, -- 검색결과 생성 시간
                      display INT, -- 검색결과 표시 개수
                      total INT, -- 뉴스기사 총 개수
                      itemTitle VARCHAR(255), -- 뉴스기사 제목
                      itemDescription TEXT, -- 뉴스기사 내용
                      itemPubDate DATE -- 뉴스기사 발행 시간
);

-- 4. 금융 용어 테이블 (Finance_Word)

CREATE TABLE Finance_Word (
                              id INT PRIMARY KEY auto_increment, -- ID
                              fnceDictNm VARCHAR(100), -- 금융용어
                              ksdFnceDictDescContent TEXT -- 금융용어 설명
);


CREATE TABLE snsUser (
                         username VARCHAR(200) PRIMARY KEY,
                         password VARCHAR(200),
                         name VARCHAR(200),
                         auth VARCHAR(200)
);

-- 5. 회원 테이블 (Member)
CREATE TABLE Member (
    username VARCHAR(200) PRIMARY KEY, -- snsUser ID
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



-- sns 유저 테이블 (snsUser)



-- 6. 금_종목_테이블 (Gold_Category)
CREATE TABLE Gold_Category (
                               srtnCd INT PRIMARY KEY, -- 단축코드 (Primary Key)
                               itmsNm VARCHAR(150) NOT NULL -- 종목명
);

-- 7. 금_거래_테이블 (Gold_Trading)
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

-- 8. 환율 종목 테이블 (Exchange_Rate_Category)
CREATE TABLE Exchange_Rate_Category (
                                        cur_unit VARCHAR(15) NOT NULL PRIMARY KEY, -- 통화 코드 (유니크 키로 유지)
                                        cur_nm VARCHAR(50) NOT NULL -- 통화명
);

-- 9. 환율 테이블 (Exchange_Rate)
CREATE TABLE Exchange_Rate (
                               searchdate DATE NOT NULL, -- 검색 요청 날짜 (DATE 타입으로 변경)
                               cur_unit VARCHAR(15) NOT NULL, -- 통화 코드
                               result INT, -- 결과 코드 (1: 성공)
                               ttb DOUBLE, -- 매도 환율
                               tts DOUBLE, -- 매수 환율
                               deal_bas_r DOUBLE, -- 기준 환율
                               bkpr INT, -- 은행 환율
                               yy_efe_r INT, -- 연간 수수료율
                               ten_dd_efe_r INT, -- 10년 수수료율
                               kftc_bkpr INT, -- KFTC 은행 환율
                               kftc_deal_bas_r DOUBLE, -- KFTC 기준 환율
                               PRIMARY KEY (searchdate, cur_unit), -- 복합 기본 키
                               FOREIGN KEY (cur_unit) REFERENCES Exchange_Rate_Category(cur_unit) -- 통화 코드에 대한 외래 키 제약
);

-- 10. 금 시세 예측 값 저장 테이블
CREATE TABLE Gold_Predict_Prices (
                                     PbasDt VARCHAR(20) PRIMARY KEY, -- 기준 일자
                                     dayPrc INT -- 기준 일자 가격
);

-- 11. FUND테이블 기준가격 열 추가
ALTER TABLE fund
    ADD COLUMN NAV DECIMAL(10, 2);

-- 12. fund 테이블 기준가격 데이터 추가
UPDATE fund SET NAV = 1047.87 WHERE prdNo = 1;
UPDATE fund SET NAV = 1038.36 WHERE prdNo = 2;
UPDATE fund SET NAV = 1043.17 WHERE prdNo = 3;
UPDATE fund SET NAV = 1045.68 WHERE prdNo = 4;
UPDATE fund SET NAV = 1014.94 WHERE prdNo = 5;
UPDATE fund SET NAV = 1024.62 WHERE prdNo = 6;
UPDATE fund SET NAV = 1011.81 WHERE prdNo = 7;
UPDATE fund SET NAV = 1015.66 WHERE prdNo = 8;
UPDATE fund SET NAV = 1045.33 WHERE prdNo = 9;
UPDATE fund SET NAV = 1041.02 WHERE prdNo = 10;
UPDATE fund SET NAV = 1037.38 WHERE prdNo = 11;
UPDATE fund SET NAV = 1125.72 WHERE prdNo = 12;
UPDATE fund SET NAV = 1010.08 WHERE prdNo = 13;
UPDATE fund SET NAV = 1019.73 WHERE prdNo = 14;
UPDATE fund SET NAV = 1102.84 WHERE prdNo = 15;
UPDATE fund SET NAV = 1050.92 WHERE prdNo = 16;
UPDATE fund SET NAV = 1248.72 WHERE prdNo = 17;
UPDATE fund SET NAV = 1037.91 WHERE prdNo = 18;
UPDATE fund SET NAV = 1012.35 WHERE prdNo = 19;
UPDATE fund SET NAV = 1087.34 WHERE prdNo = 20;
-- savings, fund 테이블에 isInCart열 추가
ALTER TABLE savings ADD COLUMN isInCart TINYINT(1) DEFAULT 0;
ALTER TABLE fund ADD COLUMN isInCart TINYINT(1) DEFAULT 0;

-- 외부 api를 이용한 savings(적금)테이블과 deposit(예금) 테이블
CREATE TABLE savingsV1 (
                           finPrdtCd VARCHAR(20) PRIMARY KEY,
                           bankName VARCHAR(100),
                           finPrdtNm VARCHAR(100),
                           joinWay VARCHAR(100),
                           spclCnd TEXT,
                           joinMember VARCHAR(100),
                           etcNote TEXT,
                           maxLimit BIGINT,
                           selectCount INT DEFAULT 0,
                           intrRateTypeNm VARCHAR(50),
                           minRate DECIMAL(5, 2),
                           maxRate DECIMAL(5, 2),
                           saveTrm INT,
                           InCart TINYINT(1) DEFAULT 0

);

CREATE TABLE depositV1 (
                           finPrdtCd VARCHAR(20) PRIMARY KEY,
                           bankName VARCHAR(100),
                           finPrdtNm VARCHAR(100),
                           joinWay VARCHAR(100),
                           spclCnd TEXT,
                           joinMember VARCHAR(100),
                           etcNote TEXT,
                           maxLimit BIGINT,
                           selectCount INT DEFAULT 0,
                           intrRateTypeNm VARCHAR(50),
                           minRate DECIMAL(5, 2),
                           maxRate DECIMAL(5, 2),
                           saveTrm INT,
                           InCart TINYINT(1) DEFAULT 0

);

create table tip(
                    id int primary key auto_increment,
                    name varchar(200),
                    type varchar(100),
                    path varchar(100),
                    count int
);

insert into tip(name, type, path, count)
values ("착오송금 예방 및 대응요령", "은행", "/src/assets/tip/bankTip/tip01", 8),
       ("은행거래 100% 활용법(1) : 우대혜택", "은행", "/src/assets/tip/bankTip/tip02", 7),
       ("은행거래 100% 활용법(2) : 예·적금 수익률 높이기", "은행", "/src/assets/tip/bankTip/tip03", 10),
       ("은행거래 100% 활용법(3) : 예·적금 편리한 서비스", "은행", "/src/assets/tip/bankTip/tip04", 14),
       ("은행거래 100% 활용법(4) : 대출이자 부담 줄이기", "은행", "/src/assets/tip/bankTip/tip05", 8),
       ("금리인하요구권 활용하기", "은행", "/src/assets/tip/bankTip/tip06", 7),
       ("은행거래 100% 활용법(5) : 알아두면 유용한 서비스", "은행", "/src/assets/tip/bankTip/tip07", 8),
       ("대표적인 휴면예금 사례", "은행", "/src/assets/tip/bankTip/tip08", 13),
       ("전세자금대출자를 위한 금융꿀팁", "은행", "/src/assets/tip/bankTip/tip09", 11),
       ("은행거래 100% 활용법(6) : 디지털뱅킹 서비스", "은행", "/src/assets/tip/bankTip/tip10", 11),
       ("외국환거래법규 위반 10대 사례 및 유의사항", "은행", "/src/assets/tip/bankTip/tip11", 12),
       ("외국환거래법규 유의사항(해외직접투자편)", "은행", "/src/assets/tip/bankTip/tip12", 13),
       ("전세가 하락기, 세입자가 꼭 알아야 할 반환보증", "은행", "/src/assets/tip/bankTip/tip13", 13),
       ("외국환거래법규상 금융소비자 유의사항", "은행", "/src/assets/tip/bankTip/tip14", 9),
       ("은행권 금융거래종합보고서 조회", "은행", "/src/assets/tip/bankTip/tip15", 5),
       ("전세 계약시 유의사항 및 전세보증금 반환보증 활용법", "은행", "/src/assets/tip/bankTip/tip16", 8),
       ("신입사원의 금융상품 현명하게 가입하기", "은행", "/src/assets/tip/bankTip/tip17", 16),
       ("ELS 등에 대한 투자시 유의사항", "금융상품", "/src/assets/tip/financeTip/tip01", 12),
       ("주식·채권투자에 실패하지 않으려면", "금융상품", "/src/assets/tip/financeTip/tip02", 13),
       ("주식투자시 요주의할 5적", "금융상품", "/src/assets/tip/financeTip/tip03", 9),
       ("유망기업 성공투자법 1.클라우드펀딩편", "금융상품", "/src/assets/tip/financeTip/tip04", 8),
       ("ETF 투자시 유의사항 8가지", "금융상품", "/src/assets/tip/financeTip/tip05", 11),
       ("감사보고서 제대로 활용하기", "금융상품", "/src/assets/tip/financeTip/tip06", 8),
       ("주식투자 시 수수료 등 절감 노하우", "금융상품", "/src/assets/tip/financeTip/tip07", 9),
       ("주식투자시 수익률 제고 노하우", "금융상품", "/src/assets/tip/financeTip/tip08", 7),
       ("펀드 투자시 비용절감 노하우 7가지", "금융상품", "/src/assets/tip/financeTip/tip09", 15),
       ("회계에 관한 정보 금융감독원 회계포탈에서 확인하세요", "금융상품", "/src/assets/tip/financeTip/tip10", 10),
       ("IPO 공모주 투자시 알아두면 유익한 공시정보", "금융상품", "/src/assets/tip/financeTip/tip11", 13),
       ("사모펀드 투자시 유의사항", "금융상품", "/src/assets/tip/financeTip/tip12", 13),
       ("재무제표 확인시 주요 체크포인트", "금융상품", "/src/assets/tip/financeTip/tip13", 22),
       ("확 달라진 감사보고서, 현명한 투자를 위하 100% 활용하기", "금융상품", "/src/assets/tip/financeTip/tip14", 13),
       ("채권 투자 이것만은 꼭 확인 후 투자하세요(기초편)", "금융상품", "/src/assets/tip/financeTip/tip15", 14),
       ("채권 투자 이것만은 꼭 확인 후 투자하세요(심화편)", "금융상품", "/src/assets/tip/financeTip/tip16", 12),
       ("해외상장 ETF 투자 시 유의사항", "금융상품", "/src/assets/tip/financeTip/tip17", 5),
       ("신입사원의 금융상품 현명하게 가입하기", "금융상품", "/src/assets/tip/financeTip/tip18", 9);

-- savingsV1 데이터 삽입
INSERT INTO savingsV1 (finPrdtCd, bankName, finPrdtNm, joinWay, spclCnd, joinMember, etcNote, maxLimit, selectCount, intrRateTypeNm, minRate, maxRate, saveTrm, InCart)
VALUES
    ('WR0001B', '우리은행', 'WON플러스예금', '인터넷,스마트폰,전화(텔레뱅킹)', '해당사항 없음', '실명의 개인', '- 가입기간: 1~36개월\n- 최소가입금액: 1만원 이상\n- 만기일을 일,월 단위로 자유롭게 선택 가능\n- 만기해지 시 신규일 당시 영업점과 인터넷 홈페이지에 고시된 계약기간별 금리 적용', NULL, 0, '단리', 1, 3.5, 12, 0),
    ('10511008000996000', '아이엠뱅크', 'iM주거래우대예금(첫만남고객형)', '영업점,인터넷,스마트폰', '해당사항 없음', '실명의 개인', '- 가입기간: 1~36개월\n- 최소가입금액: 1만원 이상\n- 만기일을 일,월 단위로 자유롭게 선택 가능\n- 만기해지 시 신규일 당시 영업점과 인터넷 홈페이지에 고시된 계약기간별 금리 적용', NULL, 0, '단리', 3, 3.47, 3, 0),
    ('01030500510002', '부산은행', 'LIVE정기예금', '영업점,인터넷', '해당사항 없음', '제한없음', '- 가입기간: 1~36개월\n- 최소가입금액: 1만원 이상\n- 만기일을 일,월 단위로 자유롭게 선택 가능\n- 만기해지 시 신규일 당시 영업점과 인터넷 홈페이지에 고시된 계약기간별 금리 적용', NULL, 0, '단리', 6, 3.52, 6, 0),
    ('10511008001004000', '아이엠뱅크', 'iM행복파트너예금(일반형)', '영업점,인터넷,스마트폰', '* 최고우대금리 : 연0.45%p\n- 지난달 당행 통장으로 연금 입금 실적 보유 : 연0.10%p\n- 상품 가입 전 당행 신용(체크)카드 보유 : 연0.10%p\n- 지난 3개월 예금 평잔 30만원 이상 : 연0.10%p\n- iM행복파트너적금 동시 가입 및 만기 보유 : 연0.10%p\n* 해당 상품을 인터넷/모바일뱅킹을 통해 가입 : 연0.05%p', '만50세 이상 실명의 개인', '계좌당 가입 최저한도 : 100만원', NULL, 0, '단리', 1, 3, 1, 0),
    ('10511008001166004', '아이엠뱅크', 'iM함께예금', '영업점,인터넷,스마트폰', '*최고우대금리 : 연0.45%p\n-전월 총 수신 평잔실적 또는 상품 가입 전 첫만남플러스 통장 보유시 \n-당행 주택청약상품보유 \n-신규일 "iM함께적금" 동시 가입 및 만기 보유 \n-당행 오픈뱅킹서비스에 다른 은행 계좌 등록시 \n각  연0.10%p                       \n*해당 상품을 인터넷/모바일뱅킹을 통해 가입 시 : 연0.05%p', '실명의 개인 및 개인사업자', '계좌당 가입 최저한도 : 100만원', NULL, 0, '단리', 1, 3, 1, 0);
-- depositV1 데이터 삽입
INSERT INTO depositV1 (finPrdtCd, bankName, finPrdtNm, joinWay, spclCnd, joinMember, etcNote, maxLimit, selectCount, intrRateTypeNm, minRate, maxRate, saveTrm, InCart)
VALUES
    ('WR0001F', '우리은행', '우리SUPER주거래적금', '영업점,인터넷,스마트폰', '1. 가, 나의 조건을 충족하는 경우: 최대 1.9%p 우대\n가. 우리은행 첫 거래 고객: 연 1.0%p\n나. 거래실적 인정기간 동안 아래 거래실적을 계약기간별 필수기간(1년: 6개월, 2년: 12개월, 3년: 18개월)이상 충족하는 경우 최대 연 0.9%p', '실명의 개인', '1. 가입기간 : 1년, 2년, 3년\n(가입기간별 금리 차등적용)\n2. 가입금액 : 월 50만원 이내', NULL, 0, '단리', 2.75, 4.65, 12, 0),
    ('10-01-30-355-0006', '주식회사 카카오뱅크', '카카오뱅크 한달적금', '스마트폰', '- 만기 후 1개월 이내 : 가입시점 기본금리x50%<br>- 만기 후 1개월초과 3개월 이내 : 가입시점 기본금리x30%<br>- 만기 후 3개월 초과 : 0.20%', '만 17세 이상의 실명의 개인', '1. 가입방법 : 스마트폰<br>2. 납입금액 : 1회 100원 이상 3만원 이하<br>3. 가입기간 : 31일<br>4. 직접 납입을 통해서 1일 1회만 입금 가능하며, 그 외의 입금은 모두 제한됨', NULL, 0, '단리', 3.45, 3.45, 3, 0),
    ('1001303001001', '토스뱅크 주식회사', '토스뱅크 키워봐요 적금', '스마트폰', ' · 만기 후 1개월 이내 : 만기시점 기본금리 X 50%<br>· 만기 후 1개월 초과 3개월 이내 : 만기시점 기본금리 X 20%<br>· 만기 후 3개월 초과 : 연 0.10%', '토스뱅크 통장 또는 서브 통장을 보유한 개인', '1인 1계좌<br>가입금액 : 0원 이상 300만원 이하<br>우대금리는 만기 해지 시 제공됨',  NULL, 0, '단리', 3.1, 3.1, 6, 0),
    ('00320342', '한국스탠다드차타드은행', 'e-그린세이브적금', '인터넷,스마트폰', '1.SC제일은행 최초 거래 신규고객에 대하여 우대 이율을 제공함 (보너스이율0.2%)                     2.SC제일마이백통장에서 출금하여 이 예금을 신규하는경우에 보너스이율을 제공함\n(가입기간:1년제/ 보너스이율:0.1% / 만기해약하는 경우에 한해 보너스이율을 적용함)', '개인(개인사업자 포함)', '디지털채널 전용상품 (인터넷, 모바일뱅킹)', 1000000000, 0, '단리', 1, 3, 1, 0),
    ('1001303001003', '토스뱅크 주식회사', '토스뱅크 굴비 적금', '스마트폰', ' · 만기 후 1개월 이내 : 만기시점 기본금리 X 50%<br>· 만기 후 1개월 초과 3개월 이내 : ', '토스뱅크 통장 또는 서브 통장을 보유한 개인', '1인 1계좌<br>가입금액 : 0원 이상 300만원 이하<br>우대금리는 만기 해지 시 제공됨', NULL, 0, '단리', 3.2, 3.2, 6, 0);


CREATE TABLE visitedpage (
                             username VARCHAR(200),
                             page VARCHAR(200),
                             visitDate VARCHAR(20),
                             PRIMARY KEY (username, page)  -- username과 page를 복합키로 설정
);