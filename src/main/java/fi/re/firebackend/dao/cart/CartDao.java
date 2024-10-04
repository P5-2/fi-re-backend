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

@Mapper
@Repository
public interface CartDao {
    // 특정 회원의 장바구니에서 펀드 상품 가져오기
    List<FundDto> getFundsInCart(String username) ;

    // 장바구니에 펀드 상품 추가
    void addFundToCart(CartDto cart) ;

    // 장바구니 펀드 상품 삭제
    public void removeFundFromCart(@Param("username") String username, @Param("prdNo") int prdNo);

    // 제품의 장바구니 여부 확인
    int isFundInUserCart(@Param("username") String username, @Param("prdNo") int prdNo);

    // 특정 사용자에 대한 적금 상품 정보 조회
    List<SavingsDepositDto> getSavingsDepositByUsername(@Param("username") String username);

    // 특정 사용자에 대한 예금 상품 정보 조회
    List<SavingsDepositDto> getDepositByUsername(@Param("username") String username);

    // 저축 및 예금 상품 옵션 정보 조회
    List<OptionalDto> getSavingsDepositOptionsByFinPrdtCd(@Param("finPrdtCd") String finPrdtCd);

    // 장바구니에 저축 상품 추가
    void addSavingsToCart(CartDto cart);

    // 장바구니 저축 상품 삭제
    public void removeSavingsFromCart(@Param("username") String username, @Param("finPrdtCd") String finPrdtCd);

    // 사용자가 특정 저축 상품을 장바구니에 담았는지 확인
    int isSavingsInUserCart(@Param("username") String username, @Param("finPrdtCd") String finPrdtCd);


    // 장바구니에 예금 상품 추가
    void addDepositToCart(CartDto cart);

    // 장바구니 예금 상품 삭제
    public void removeDepositFromCart(@Param("username") String username, @Param("finPrdtCd") String finPrdtCd);

    // 사용자가 특정 예금 상품을 장바구니에 담았는지 확인
    int isDepositInUserCart(@Param("username") String username, @Param("finPrdtCd") String finPrdtCd);
}
