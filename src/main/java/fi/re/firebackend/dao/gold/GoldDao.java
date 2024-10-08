package fi.re.firebackend.dao.gold;

import fi.re.firebackend.dto.gold.GoldInfo;
import fi.re.firebackend.dto.gold.GoldPredicted;
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

    //예측한 금 시세 테이블에 저장
    void insertGoldPredictData(GoldPredicted goldPredicted);

    //기간이 지난 금 예측 시세 row 삭제
    int deleteGoldPredictData(String pBasDt);

    //금 시세 예측 데이터 불러오기
    List<GoldPredicted> getGoldPredictData(@Param("today") String today);

    // 예측값 테이블에서 마지막 저장된 기준일자 가져오기
    String getLastPBasDt();
}
