package fi.re.firebackend.dao.finance.savings;

import fi.re.firebackend.dto.finance.fund.FundDto;
import fi.re.firebackend.dto.finance.savings.SavingsDepositDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface SavingsV1Dao {
    //적금 상세정보
    SavingsDepositDto getSavingsByCode(@Param("finPrdtCd") String finPrdtCd);

    //적금 Hot리스트
    List<SavingsDepositDto> getHotSavings();

    //적금 전체 리스트
    List<SavingsDepositDto> getAllSavings();

    // 페이지 네이션
    List<SavingsDepositDto> selectSavings(@Param("size") int size, @Param("offset") int offset);
    int countSavings(); // 총 적금 개수

    //비교함에 적금 상품 담기
    void updateSavingsCartStatus(@Param("finPrdtCd") String finPrdtCd, @Param("InCart") int InCart);

    //비교함에 있는 적금 데이터 조회
    List<SavingsDepositDto> getSavingsInCart();

    // 비교함 상태 조회
    int getSavingsCartStatus(@Param("prdNo") int prdNo);

    void insertOrUpdateSavings(SavingsDepositDto savings);

    // 금융그룹별 적금 상품 조회를 위한 메서드 추가
    List<SavingsDepositDto> getSavingsByTopFinGrpNo(@Param("topFinGrpNo") String topFinGrpNo);
}

