package fi.re.firebackend.util.api;

import fi.re.firebackend.dto.forex.ForexDto;
import fi.re.firebackend.dto.forex.ForexWrapper;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.lang.reflect.Field;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class ForexApiTest {

    @InjectMocks
    private ForexApi forexApi;

    @Mock
    private RestTemplate restTemplate;

    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        MockitoAnnotations.openMocks(this);

        // API_URL 설정 (리플렉션 사용)
        setPrivateField(forexApi, "API_URL", "http://fakeapi.com");
        setPrivateField(forexApi, "AUTH_KEY", "fakeAuthKey");
    }

    // 리플렉션을 통해 private 필드에 값을 설정하는 헬퍼 메서드
    private void setPrivateField(Object targetObject, String fieldName, String value) throws NoSuchFieldException, IllegalAccessException {
        Field field = targetObject.getClass().getDeclaredField(fieldName);
        field.setAccessible(true); // private 필드 접근 허용
        field.set(targetObject, value);
    }

    @DisplayName("외화 정보를 가져오기 성공했을 때 테스트")
    @Test
    void testGetForexData_success() throws ParseException {
        // Mock API response
        String mockApiResponse = "[{\"cur_unit\": \"USD\", \"deal_bas_r\": \"1100\", \"cur_nm\": \"US Dollar\"}]";
        ResponseEntity<String> responseEntity = new ResponseEntity<>(mockApiResponse, HttpStatus.OK);
        when(restTemplate.getForEntity(anyString(), eq(String.class))).thenReturn(responseEntity);

        // Call the method
        ForexWrapper result = forexApi.getForexData("20231001");

        // Verify the response
        assertNotNull(result);
        assertFalse(result.getForexData().isEmpty());
        assertEquals(1, result.getForexData().size());

        ForexDto forexDto = result.getForexData().get(0);
        assertEquals("USD", forexDto.getCurUnit());
        assertEquals("1100", forexDto.getDealBasR());
        assertEquals("US Dollar", forexDto.getCurNm());
    }

    @Test
    void testGetForexData_noData() throws ParseException {
        // Mock API response with no data
        String mockApiResponse = "[]";
        ResponseEntity<String> responseEntity = new ResponseEntity<>(mockApiResponse, HttpStatus.OK);
        when(restTemplate.getForEntity(anyString(), eq(String.class))).thenReturn(responseEntity);

        // Call the method
        ForexWrapper result = forexApi.getForexData("20231001");

        // Verify the response
        assertNotNull(result);
        assertTrue(result.getForexData().isEmpty());

        // Check that the request was successful
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        // Expecting one day before due to empty data
        assertEquals(LocalDate.parse("20230930", DateTimeFormatter.ofPattern("yyyyMMdd")), result.getSearchDate());
    }


    @Test
    void testGetForexData_apiError() throws ParseException {
        // Mock API response with error
        when(restTemplate.getForEntity(anyString(), eq(String.class)))
                .thenThrow(new RuntimeException("API Error"));

        // Call the method
        ForexWrapper result = forexApi.getForexData("20231001");

        // Verify the response
        assertNotNull(result);
        assertTrue(result.getForexData().isEmpty()); // No data due to error
    }

    @Test
    void testBuildUrl() {
        // Prepare test data
        LocalDate testDate = LocalDate.of(2023, 10, 1);
        String expectedUrl = "http://fakeapi.com?authkey=fakeAuthKey&data=AP01&searchdate=20231001";

        // Call the method
        String resultUrl = forexApi.buildUrl("AP01", testDate);

        // Verify the URL is correctly built
        assertEquals(expectedUrl, resultUrl);
    }
}
