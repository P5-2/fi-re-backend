package fi.re.firebackend.dao.cart;

import fi.re.firebackend.dto.cart.CartDto;
import fi.re.firebackend.dto.finance.fund.FundDto;
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
}
