package fi.re.firebackend.dao.finance.savings;

import fi.re.firebackend.dto.finance.savings.SavingsDepositDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Mapper
@Repository
public interface SavingsDao {
    //예적금 상세정보
    SavingsDepositDto getSavingsById(int prdNo);

    //예적금 Hot리스트
    List<SavingsDepositDto> hot();

    //에적금 전체 리스트
    List<SavingsDepositDto> all();

    // type에 따른 목록 가져오기
    List<SavingsDepositDto> selectSavingsByType(String type);

    // 비교함 담기
    void updateSavingsCartStatus(@Param("prdNo") int prdNo, @Param("InCart") int InCart);

    // 비교함에 있는 데이터 조회
    List<SavingsDepositDto> selectSavingsInCart();

    // 비교함 상태 조회
    int getSavingsCartStatus(@Param("prdNo") int prdNo);
}
