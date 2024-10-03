package fi.re.firebackend.service.cart;

import fi.re.firebackend.dao.finance.fund.FundDao;
import fi.re.firebackend.dao.finance.savings.SavingsDepositDao;
import fi.re.firebackend.dto.finance.fund.FundDto;
import fi.re.firebackend.dto.finance.savings.SavingsDepositDto;
import org.springframework.stereotype.Service;

import java.util.List;

//@Service
//public class CartService {
//    private final FundDao fundDao;
//    private final SavingsDepositDao savingsDepositDao;
//
//    public CartService(FundDao fundDao, SavingsDepositDao savingsDepositDao) {
//        this.fundDao = fundDao;
//        this.savingsDepositDao = savingsDepositDao;
//    }
//
//    public void updateFundCartStatus(int prdNo, boolean InCart) {
//        System.out.println("CartService updateFundCartStatus");
//        fundDao.updateFundCartStatus(prdNo, InCart ? 1 : 0);
//    }
//
//    public void updateSavingCartStatus(int prdNo, boolean InCart) {
//        savingsDepositDao.updateSavingsCartStatus(prdNo, InCart ? 1 : 0);
//    }
//
//    public List<FundDto> getFundsInCart() {
//        return fundDao.selectFundsInCart();
//    }
//
//    public List<SavingsDepositDto> getSavingsInCart() {
//        return savingsDepositDao.selectSavingsInCart();
//    }
//
//    public boolean isSavingsInCart(int prdNo) {
//        return savingsDepositDao.getSavingsCartStatus(prdNo) == 1;
//    }
//
//    public boolean isFundInCart(int prdNo) {
//        return fundDao.getFundCartStatus(prdNo) == 1;
//    }
//
//}
