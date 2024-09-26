package fi.re.firebackend.service.cart;

import fi.re.firebackend.dao.finance.fund.FundDao;
import fi.re.firebackend.dao.finance.savings.SavingsDao;
import fi.re.firebackend.dto.finance.fund.FundDto;
import fi.re.firebackend.dto.finance.savings.SavingsDto;
import org.springframework.beans.factory.annotation.Autowired;
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

    public void updateFundCartStatus(int prdNo, boolean isInCart) {
        System.out.println("CartService updateFundCartStatus");
        fundDao.updateFundCartStatus(prdNo, isInCart ? 1 : 0);
    }

    public void updateSavingCartStatus(int prdNo, boolean isInCart) {
        savingsDao.updateSavingsCartStatus(prdNo, isInCart ? 1 : 0);
    }

    public List<FundDto> getFundsInCart() {
        return fundDao.selectFundsInCart();
    }

    public List<SavingsDto> getSavingsInCart() {
        return savingsDao.selectSavingsInCart();
    }

}
