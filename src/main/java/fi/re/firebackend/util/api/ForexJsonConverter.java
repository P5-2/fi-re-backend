package fi.re.firebackend.util.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import fi.re.firebackend.dto.forex.ForexDto;

import java.io.IOException;
import java.util.List;

public class ForexJsonConverter {

    public static List<ForexDto> convertJsonToList(String jsonString) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();

        // JSON 문자열을 List<ForexDto>로 변환
        return objectMapper.readValue(jsonString, new TypeReference<List<ForexDto>>() {
        });
    }
}
