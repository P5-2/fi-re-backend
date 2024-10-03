package fi.re.firebackend.service.cart;

import fi.re.firebackend.dao.cart.CartDao;
import fi.re.firebackend.dto.cart.CartDto;
import fi.re.firebackend.dto.finance.fund.FundDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartService {
    @Autowired
    private CartDao cartDao;

    // 특정 회원의 장바구니에 담긴 펀드 상품 목록 조회
    public List<FundDto> getFundsInCart(String username) {
        return cartDao.getFundsInCart(username);
    }

    // 장바구니에 펀드 상품 추가
    public void addFundToCart(CartDto cart) {
        cartDao.addFundToCart(cart);
    }

    // 장바구니에서 특정 펀드 상품 개별 삭제
    public void removeFundFromCart(String username, int prdNo) {
        cartDao.removeFundFromCart(username, prdNo);
    }

    // 사용자가 특정 펀드를 장바구니에 담았는지 확인
    public boolean isFundInUserCart(String username, int prdNo) {
        return cartDao.isFundInUserCart(username, prdNo) > 0;
    }
}
