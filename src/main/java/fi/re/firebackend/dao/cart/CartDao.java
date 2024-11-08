package fi.re.firebackend.dao.cart;

import fi.re.firebackend.dto.cart.CartDto;
import fi.re.firebackend.dto.finance.fund.FundDto;
import fi.re.firebackend.dto.finance.savings.OptionalDto;
import fi.re.firebackend.dto.finance.savings.SavingsDepositDto;
import fi.re.firebackend.dto.finance.savings.SavingsDepositWithOptionsDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
@Mapper
@Repository
public interface CartDao {

    // 특정 회원의 장바구니에서 펀드 상품 가져오기
    List<FundDto> getFundsInCart(String username);

    // 장바구니에 펀드 상품 추가
    void addFundToCart(CartDto cart);

    // 장바구니 펀드 상품 삭제
    void removeFundFromCart(@Param("username") String username, @Param("prdNo") int prdNo);

    // 제품의 장바구니 여부 확인
    int isFundInUserCart(@Param("username") String username, @Param("prdNo") int prdNo);

    // 장바구니에 적금 상품 추가 (단리/복리 반영)
    void addSavingsToCart(CartDto cart);

    // 장바구니 적금 상품 삭제 (단리/복리 반영)
    void removeSavingsFromCart(@Param("username") String username, @Param("finPrdtCd") String finPrdtCd, @Param("intrRateTypeNm") String intrRateTypeNm);

    // 사용자가 특정 적금 상품을 장바구니에 담았는지 확인 (단리/복리 반영)
    int isSavingsInUserCart(@Param("username") String username, @Param("finPrdtCd") String finPrdtCd, @Param("intrRateTypeNm") String intrRateTypeNm);

    // 장바구니에 예금 상품 추가 (단리/복리 반영)
    void addDepositToCart(CartDto cart);

    // 장바구니 예금 상품 삭제 (단리/복리 반영)
    void removeDepositFromCart(@Param("username") String username, @Param("finPrdtCd") String finPrdtCd, @Param("intrRateTypeNm") String intrRateTypeNm);

    // 사용자가 특정 예금 상품을 장바구니에 담았는지 확인 (단리/복리 반영)
    int isDepositInUserCart(@Param("username") String username, @Param("finPrdtCd") String finPrdtCd, @Param("intrRateTypeNm") String intrRateTypeNm);


    // cart 테이블에서 해당 상품의 단리 또는 복리 여부를 조회하는 메서드
    public List<String> getIntrRateTypeByCart(@Param("username") String username, @Param("finPrdtCd") String finPrdtCd);

    // 1. username에 맞는 cartId와 단리/복리 정보 조회
    List<CartDto> getCartDetailsByUsername(@Param("username") String username);

    // 2. 특정 cartId에 맞는 적금 상품 정보 조회
    SavingsDepositDto getSavingsDepositByCartId(@Param("cartId") Integer cartId);

    // 3. 특정 cartId에 맞는 예금 상품 정보 조회
    SavingsDepositDto getDepositByCartId(@Param("cartId") Integer cartId);

    // 4. 특정 cartId와 단리/복리 여부에 맞는 옵션 정보 조회
    List<OptionalDto> getSavingsDepositOptionsByCartIdAndRateType(@Param("cartId") Integer cartId, @Param("rateType") String rateType);
}


