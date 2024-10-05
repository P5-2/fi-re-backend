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


    // 장바구니에 저축 상품 추가
    public void addSavingsToCart(CartDto cart) {
        cartDao.addSavingsToCart(cart);
    }

    // 장바구니에서 특정 저축 상품 개별 삭제
    public void removeSavingsFromCart(String username, String finPrdtCd) {
        cartDao.removeSavingsFromCart(username, finPrdtCd);
    }

    // 사용자가 특정 저축 상품을 장바구니에 담았는지 확인
    public boolean isSavingsInUserCart(String username, String finPrdtCd) {
        return cartDao.isSavingsInUserCart(username, finPrdtCd) > 0;
    }

    // 장바구니에 예금 상품 추가
    public void addDepositToCart(CartDto cart) {
        cartDao.addDepositToCart(cart);
    }

    // 장바구니에서 특정 예금 상품 개별 삭제
    public void removeDepositFromCart(String username, String finPrdtCd) {
        cartDao.removeDepositFromCart(username, finPrdtCd);
    }

    // 사용자가 특정 예금 상품을 장바구니에 담았는지 확인
    public boolean isDepositInUserCart(String username, String finPrdtCd) {
        return cartDao.isDepositInUserCart(username, finPrdtCd) > 0;
    }

    // 특정 사용자의 적금 상품 및 옵션 정보 조회
    public List<SavingsDepositWithOptionsDto> getSavingsDepositByUsername(String username) {
        // 1. 적금 상품 조회
        List<SavingsDepositDto> savingsDeposits = cartDao.getSavingsDepositByUsername(username);

        // 2. 각 적금 상품의 옵션 정보를 조회하고 결합
        return savingsDeposits.stream().map(savingsDeposit -> {
            List<OptionalDto> options = cartDao.getSavingsDepositOptionsByFinPrdtCd(savingsDeposit.getFinPrdtCd());
            return new SavingsDepositWithOptionsDto(savingsDeposit, options);
        }).collect(Collectors.toList());
    }

    // 특정 사용자의 예금 상품 및 옵션 정보 조회
    public List<SavingsDepositWithOptionsDto> getDepositByUsername(String username) {
        // 1. 예금 상품 조회
        List<SavingsDepositDto> deposits = cartDao.getDepositByUsername(username);

        // 2. 각 예금 상품의 옵션 정보를 조회하고 결합
        return deposits.stream().map(deposit -> {
            List<OptionalDto> options = cartDao.getSavingsDepositOptionsByFinPrdtCd(deposit.getFinPrdtCd());
            return new SavingsDepositWithOptionsDto(deposit, options);
        }).collect(Collectors.toList());
    }
}
