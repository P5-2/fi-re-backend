package fi.re.firebackend.dao.finance.savings;

import fi.re.firebackend.dto.finance.savings.SavingsDepositDto;
import fi.re.firebackend.dto.finance.savings.SavingsDepositWithOptionsDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface SavingsDepositDao {

    //예적금 상세페이지
    SavingsDepositWithOptionsDto getProductDetail(@Param("finPrdtCd") String finPrdtCd);

    //예적금 Hot 리스트
    List<SavingsDepositWithOptionsDto> getHotProducts(@Param("prdtDiv") String productType, @Param("limit") int limit);

    //예적금 전체 리스트(페이지네이션 포함)
    List<SavingsDepositWithOptionsDto> getAllProducts(@Param("offset") int offset, @Param("limit") int limit, @Param("prdtDiv") String prdtDiv);

    //페이지 네이션을 위한 전체 상품 수 조회
    int getTotalProductCount(@Param("prdtDiv") String prdtDiv);

    //예적금 상품이 DB에 update(갱신) or insert(추가) 됐을 경우
    void insertProduct(SavingsDepositWithOptionsDto product);

    void updateProduct(SavingsDepositWithOptionsDto product);

    //상품 존재여부 boolean으로 확인
    boolean checkProductExists(@Param("finPrdtCd") String finPrdtCd);



//    //금융 그룹별 예적금 상품 조회를 위한 메서드 추가
//    List<SavingsDepositWithOptionsDto> getProductsByTopFinGrpNo(@Param("topFinGrpNo") String topFinGrpNo, @Param("offset") int offset, @Param("limit") int limit);
//
//    //비교함에 예적금 상품 담기
//    void addProductCartStatus(@Param("finPrdtCd") String finPrdtCd, @Param("InCart") String inCart);
//
//    //비교함에 있는 전체 예적금 데이터 조회
//    List<SavingsDepositWithOptionsDto> getProductsInCart();
//
//    //비교함 특정 상품 조회
//    String getProductsCartStatus(@Param("finPrdtCd") String finPrdtCd);

}


