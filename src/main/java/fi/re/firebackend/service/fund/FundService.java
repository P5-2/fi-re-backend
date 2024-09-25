package fi.re.firebackend.service.fund;

import fi.re.firebackend.dao.finance.fund.FundDao;
import fi.re.firebackend.dto.finance.fund.FundDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Service
public class FundService {
    private final FundDao fundDao;

    public FundService(FundDao fundDao) {
        this.fundDao = fundDao;
    }

    public List<FundDto> getFunds(int page, int size) {
        int offset = (page - 1) * size;
        return fundDao.selectFunds(size, offset);
    }

    public int getTotalCount() {
        return fundDao.countFunds();
    }
}
