package fi.re.firebackend.service.cart;

import fi.re.firebackend.dao.finance.fund.FundDao;
import fi.re.firebackend.dao.finance.savings.SavingsDao;
import fi.re.firebackend.dto.finance.fund.FundDto;
import fi.re.firebackend.dto.finance.savings.SavingsDepositDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartService {
    private final FundDao fundDao;
    private final SavingsDao savingsDao;

    public CartService(FundDao fundDao, SavingsDao savingsDao) {
        this.fundDao = fundDao;
        this.savingsDao = savingsDao;
    }

    public void updateFundCartStatus(int prdNo, boolean InCart) {
        System.out.println("CartService updateFundCartStatus");
        fundDao.updateFundCartStatus(prdNo, InCart ? 1 : 0);
    }

    public void updateSavingCartStatus(int prdNo, boolean InCart) {
        savingsDao.updateSavingsCartStatus(prdNo, InCart ? 1 : 0);
    }

    public List<FundDto> getFundsInCart() {
        return fundDao.selectFundsInCart();
    }

    public List<SavingsDepositDto> getSavingsInCart() {
        return savingsDao.selectSavingsInCart();
    }

    public boolean isSavingsInCart(int prdNo) {
        return savingsDao.getSavingsCartStatus(prdNo) == 1;
    }

    public boolean isFundInCart(int prdNo) {
        return fundDao.getFundCartStatus(prdNo) == 1;
    }

}
