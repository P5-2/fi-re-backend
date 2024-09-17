package fi.re.firebackend.dao.gold;

import fi.re.firebackend.dto.gold.GoldInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface GoldDao {
    // 마지막 저장된 기준일자 가져오기
    String getLastBasDt();

    // 특정 날짜 범위의 데이터를 가져오기
    List<GoldInfo> getGoldInfoInPeriod(@Param("startDate") String startDate, @Param("endDate") String endDate);

    //테이블이 비었는지 확인
    int isTableEmpty();

    // 금종목이 존재하는지 확인
    int checkGoldCategoryExists(int srtnCd);

    // 금종목 삽입
    void insertGoldCategory(@Param("srtnCd") int srtnCd, @Param("itmsNm") String itmsNm);

    // 금 시세 데이터 삽입
    void insertGoldData(GoldInfo goldInfo);
}
