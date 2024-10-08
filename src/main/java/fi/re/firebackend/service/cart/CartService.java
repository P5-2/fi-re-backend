package fi.re.firebackend.service.cart;

import fi.re.firebackend.dao.cart.CartDao;
import fi.re.firebackend.dto.cart.CartDto;
import fi.re.firebackend.dto.finance.fund.FundDto;
import fi.re.firebackend.dto.finance.savings.OptionalDto;
import fi.re.firebackend.dto.finance.savings.SavingsDepositDto;
import fi.re.firebackend.dto.finance.savings.SavingsDepositWithOptionsDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Stream;

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

    // 장바구니에 저축 상품 추가 (단리/복리 구분 추가)
    public void addSavingsToCart(CartDto cart) {
        cartDao.addSavingsToCart(cart);
    }

    // 장바구니에서 특정 저축 상품 개별 삭제 (단리/복리 구분 추가)
    public void removeSavingsFromCart(String username, String finPrdtCd, String intrRateTypeNm) {
        cartDao.removeSavingsFromCart(username, finPrdtCd, intrRateTypeNm);
    }

    // 사용자가 특정 저축 상품을 장바구니에 담았는지 확인 (단리/복리 구분 추가)
    public boolean isSavingsInUserCart(String username, String finPrdtCd, String intrRateTypeNm) {
        return cartDao.isSavingsInUserCart(username, finPrdtCd, intrRateTypeNm) > 0;
    }

    // 장바구니에 예금 상품 추가 (단리/복리 구분 추가)
    public void addDepositToCart(CartDto cart) {
        cartDao.addDepositToCart(cart);
    }

    // 장바구니에서 특정 예금 상품 개별 삭제 (단리/복리 구분 추가)
    public void removeDepositFromCart(String username, String finPrdtCd, String intrRateTypeNm) {
        cartDao.removeDepositFromCart(username, finPrdtCd, intrRateTypeNm);
    }

    // 사용자가 특정 예금 상품을 장바구니에 담았는지 확인 (단리/복리 구분 추가)
    public boolean isDepositInUserCart(String username, String finPrdtCd, String intrRateTypeNm) {
        return cartDao.isDepositInUserCart(username, finPrdtCd, intrRateTypeNm) > 0;
    }


    // 특정 사용자의 적금 상품 및 옵션 정보 조회
    public List<SavingsDepositWithOptionsDto> getSavingsDepositByUsername(String username) {
        // 1. CartDto 리스트로 cartDetails를 가져옴
        List<CartDto> cartDetails = cartDao.getCartDetailsByUsername(username);

        // 2. 적금 상품만 필터링하여 처리
        return cartDetails.stream()
                .filter(cartDetail -> "savings".equals(cartDetail.getProductType())) // productType이 "savings"인 것만 필터링
                .flatMap(cartDetail -> {
                    Integer cartId = cartDetail.getCartId();
                    String rateType = cartDetail.getIntrRateTypeNm();

                    // 3. 적금 상품 정보와 옵션 조회
                    SavingsDepositDto savingsDeposit = cartDao.getSavingsDepositByCartId(cartId);
                    List<OptionalDto> options = cartDao.getSavingsDepositOptionsByCartIdAndRateType(cartId, rateType);

                    // 4. SavingsDepositWithOptionsDto 생성하여 반환
                    return Stream.of(new SavingsDepositWithOptionsDto(savingsDeposit, options));
                })
                .collect(Collectors.toList());
    }

    // 특정 사용자의 예금 상품 및 옵션 정보 조회
    public List<SavingsDepositWithOptionsDto> getDepositByUsername(String username) {
        // 1. CartDto 리스트로 cartDetails를 가져옴
        List<CartDto> cartDetails = cartDao.getCartDetailsByUsername(username);

        // 2. 예금 상품만 필터링하여 처리
        return cartDetails.stream()
                .filter(cartDetail -> "deposit".equals(cartDetail.getProductType())) // productType이 "deposit"인 것만 필터링
                .flatMap(cartDetail -> {
                    Integer cartId = cartDetail.getCartId();
                    String rateType = cartDetail.getIntrRateTypeNm();

                    // 3. 예금 상품 정보와 옵션 조회
                    SavingsDepositDto deposit = cartDao.getDepositByCartId(cartId);
                    List<OptionalDto> options = cartDao.getSavingsDepositOptionsByCartIdAndRateType(cartId, rateType);

                    // 4. SavingsDepositWithOptionsDto 생성하여 반환
                    return Stream.of(new SavingsDepositWithOptionsDto(deposit, options));
                })
                .collect(Collectors.toList());
    }
}
