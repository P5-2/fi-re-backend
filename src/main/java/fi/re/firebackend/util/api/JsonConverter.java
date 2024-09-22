package fi.re.firebackend.util.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fi.re.firebackend.dto.gold.GoldInfo;

import java.util.List;

public class JsonConverter {

    // 제네릭 타입 T로 변경하여 어떤 클래스에도 사용할 수 있게 수정
    public static <T> List<T> convertJsonToList(String jsonString, Class<T> clazz) {
        ObjectMapper objectMapper = new ObjectMapper();
        List<T> resultList = null;

        try {
            // JSON 문자열을 JsonNode로 변환
            JsonNode rootNode = objectMapper.readTree(jsonString);

            // 'items.item' 배열 추출
            JsonNode itemsNode = rootNode.path("response").path("body").path("items").path("item");

            // 'items.item' 배열을 List<T>로 변환
            resultList = objectMapper.readValue(
                    itemsNode.toString(),
                    objectMapper.getTypeFactory().constructCollectionType(List.class, clazz)
            );
        } catch (Exception e) {
            e.printStackTrace();
        }

        return resultList;
    }
}

