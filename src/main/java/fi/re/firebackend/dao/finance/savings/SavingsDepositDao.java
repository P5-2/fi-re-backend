package fi.re.firebackend.dao.finance.savings;

import fi.re.firebackend.dto.finance.savings.AllPageListDto;
import fi.re.firebackend.dto.finance.savings.SavingsDepositWithOptionsDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface SavingsDepositDao {

    //예적금 상세페이지
    List<SavingsDepositWithOptionsDto> getProductDetail(@Param("finPrdtCd") String finPrdtCd, @Param("intrRateTypeNm") String intrRateTypeNm, @Param("rsrvType") String rsrvType);

    //selectCount 증가
    int plusSelectCount(@Param("finPrdtCd") String finPrdtCd);

    //예적금 Hot 리스트
    List<SavingsDepositWithOptionsDto> getHotProducts(@Param("prdtDiv") String prdtDiv, @Param("limit") int limit);

    //예적금 전체 리스트(페이지네이션 포함)
    List<SavingsDepositWithOptionsDto> getAllProducts(@Param("limit") int limit, @Param("prdtDiv") String prdtDiv);

    List<SavingsDepositWithOptionsDto> getSavingsDepositPageList(AllPageListDto dto);

    //페이지 네이션을 위한 전체 상품 수 조회
    int getTotalProductCount(AllPageListDto dto);

    //예적금 상품이 DB에 update(갱신) or insert(추가) 됐을 경우
    void insertProduct(SavingsDepositWithOptionsDto product);

    void insertProductOptions(SavingsDepositWithOptionsDto product);

    void updateProduct(SavingsDepositWithOptionsDto product);

    void deleteProductOptions(String finPrdtCd);

    void insertUpdatedProductOptions(SavingsDepositWithOptionsDto product);

    //상품 존재여부 boolean으로 확인
    boolean checkProductExists(@Param("finPrdtCd") String finPrdtCd);
}




