package de.leanovate.cucumber.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.Charsets;
import org.apache.http.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.stream.Stream;

public class Utils {
    public static ObjectMapper mapper = new ObjectMapper();

    public static String formatRequest(HttpRequest request) {
        final StringBuilder sb = new StringBuilder();

        sb.append(request.getRequestLine()).append("\n");

        Stream.of(request.getAllHeaders()).map(Utils::formatHeader).forEach(sb::append);

        if (request instanceof HttpEntityEnclosingRequest) {
            HttpEntity entity = ((HttpEntityEnclosingRequest) request).getEntity();

            sb.append("\n");

            if (entity != null) {
                try {
                    String body = EntityUtils.toString(entity, Charsets.UTF_8);

                    ((HttpEntityEnclosingRequest) request).setEntity(new StringEntity(body, Charsets.UTF_8));

                    sb.append(formatBody(body));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        return sb.toString();
    }

    public static String formatResponse(HttpResponse response) {
        final StringBuilder sb = new StringBuilder();

        sb.append(response.getStatusLine().toString()).append("\n");

        Stream.of(response.getAllHeaders()).map(Utils::formatHeader).forEach(sb::append);

        if (response.getEntity() != null) {
            sb.append("\n");

            try {
                String body = EntityUtils.toString(response.getEntity(), Charsets.UTF_8);

                EntityUtils.updateEntity(response, new StringEntity(body, Charsets.UTF_8));

                sb.append(formatBody(body));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return sb.toString();
    }

    public static String formatHeader(Header header) {
        final String name = header.getName();
        String value = header.getValue();

        if (HttpHeaders.AUTHORIZATION.equalsIgnoreCase(name)) {
            int half = value.length() / 2;
            value = value.substring(0, value.length() - half) + repeatChar('*', half);
        }

        return String.format("%s: %s\n", name, value);
    }

    public static String formatBody(String body) {
        if (body.length() > 0) {
            try {
                Object obj = mapper.readTree(body);

                return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
            } catch (Exception e) {
                return "<<<<<< Non-json entity >>>>>";
            }
        }
        return "<<<< empty body >>>>";
    }

    public static String repeatChar(char ch, int count) {
        final StringBuilder builder = new StringBuilder(count);

        for (int i = 0; i < count; i++) {
            builder.append(ch);
        }

        return builder.toString();
    }
}
