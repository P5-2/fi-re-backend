package fi.re.firebackend.dto.cart;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartDto {
    private int cartId;            // Cart 테이블의 ID
    private String username;       // snsUser 테이블의 username
    private int prdNo;             // 펀드 상품 번호 (fund 테이블과 연관)
    private String finPrdtCd;      // 예적금 상품 코드 (savingsDeposit 테이블과 연관)
    private String productType;    // 상품 타입 ('fund', 'savings', 'deposit')
    private String intrRateTypeNm; // 이자율 유형 ('단리' 또는 '복리')
}
