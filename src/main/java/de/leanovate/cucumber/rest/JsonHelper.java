package de.leanovate.cucumber.rest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.Charsets;
import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class JsonHelper {
    public static ObjectMapper mapper = new ObjectMapper();

    public static String tryFormatJson(String maybeJson) {
        if (maybeJson.length() > 0) {
            try {
                Object obj = mapper.readTree(maybeJson);

                return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
            } catch (Exception e) {
                return "<<<<<< Non-json entity >>>>>";
            }
        }
        return "<<<< empty body >>>>";
    }

    public static <T> T entityToValue(HttpEntity entity, Class<T> expected) {
        try {
            return mapper.readValue(EntityUtils.toString(entity, Charsets.UTF_8), expected);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static JsonNode entityToTree(HttpEntity entity) {
        try {
            return mapper.readTree(EntityUtils.toString(entity, Charsets.UTF_8));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static HttpEntity valueToEntity(Object obj) {
        try {
            return new StringEntity(mapper.writeValueAsString(obj), ContentType.APPLICATION_JSON);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static HttpEntity valueToEntity(String contentType, Object obj) {
        try {
            return new StringEntity(mapper.writeValueAsString(obj), ContentType.create(contentType, "UTF-8"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
