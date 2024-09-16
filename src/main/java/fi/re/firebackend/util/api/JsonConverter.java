package fi.re.firebackend.util.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fi.re.firebackend.dto.gold.GoldInfo;

import java.util.List;

public class JsonConverter {

    public static List<GoldInfo> convertJsonToList(String jsonString) {
        ObjectMapper objectMapper = new ObjectMapper();
        List<GoldInfo> goldInfoList = null;

        try {
            // JSON 문자열을 JsonNode로 변환
            JsonNode rootNode = objectMapper.readTree(jsonString);

            // 'items.item' 배열 추출
            JsonNode itemsNode = rootNode.path("response").path("body").path("items").path("item");

            // 'items.item' 배열을 List<GoldInfo>로 변환
            goldInfoList = objectMapper.readValue(itemsNode.toString(), new TypeReference<List<GoldInfo>>() {
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        return goldInfoList;
    }
}

