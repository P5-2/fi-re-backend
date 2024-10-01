package fi.re.firebackend.dao.finance.savings;

import fi.re.firebackend.dto.finance.savings.SavingsDepositDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface DepositV1Dao {
    //예금 상세정보
    SavingsDepositDto getDepositByCode(@Param("finPrdtCd") String finPrdtCd);

    //예금 Hot리스트
    List<SavingsDepositDto> getHotDeposit();

    //예금 전체 리스트
    List<SavingsDepositDto> getAllDeposit();

    // 페이지 네이션
    List<SavingsDepositDto> selectDeposit(@Param("size") int size, @Param("offset") int offset);
    int countDeposit(); // 총 적금 개수

    //비교함에 예금 상품 담기
    void updateDepositCartStatus(@Param("finPrdtCd") String finPrdtCd, @Param("InCart") int InCart);
    //비교함에 있는 예금 데이터 조회
    List<SavingsDepositDto> getDepositsInCart();
    // 비교함 상태 조회
    int getDepositCartStatus(@Param("prdNo") int prdNo);
}
