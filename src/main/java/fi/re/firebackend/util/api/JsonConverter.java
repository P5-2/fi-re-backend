package fi.re.firebackend.util.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import fi.re.firebackend.dto.gold.GoldInfo;

import java.util.List;

public class JsonConverter {

    public static List<GoldInfo> convertJsonToList(String jsonString) {
        ObjectMapper objectMapper = new ObjectMapper();
        List<GoldInfo> goldInfoList = null;

        try {
            goldInfoList = objectMapper.readValue(jsonString, new TypeReference<List<GoldInfo>>() {});
        } catch (Exception e) {
            e.printStackTrace();
        }

        return goldInfoList;
    }
}
