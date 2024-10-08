package fi.re.firebackend.dao.finance.fund;

import fi.re.firebackend.dto.finance.fund.FundDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface FundDao {
    //펀드상품 상세정보 받아오기
    FundDto getFundById(int prdNo);

    int plusSelectCount(int prdNo);

    //펀드 Hot 리스트
    List<FundDto> hot();

    //펀드 전체 리스트
    List<FundDto> all();

    // 페이지 네이션
    List<FundDto> selectFunds(@Param("size") int size, @Param("offset") int offset);

    int countFunds(); // 총 펀드 개수
}
