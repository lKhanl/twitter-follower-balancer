package dev.oguzhanercelik.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.math.BigInteger;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Singleton {

    public static Object getPOJOFromJSON(String json, Class<?> clazz) {

        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(json, clazz);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getHttpResponseBody(String requestMediaType, Object reqBody, String url, String method, Map<String, String> reqHeader) throws IOException {
        Response response = getHttpResponse(requestMediaType, reqBody, url, method, reqHeader);
        return response.body().string();
    }

    public static Response getHttpResponse(String requestMediaType, Object reqBody, String url, String method, Map<String, String> reqHeader) throws IOException {
        if (requestMediaType == null) {
            requestMediaType = "application/json";
        }

        if (reqHeader == null) {
            reqHeader = Map.of("Content-Type", requestMediaType);
        }

        ObjectMapper objectMapper = new ObjectMapper();
        String bodyStr = objectMapper.writeValueAsString(reqBody);

        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        RequestBody body = RequestBody.create(MediaType.parse(requestMediaType), bodyStr);
        Request request = new Request.Builder()
                .url(url)
//                .method(method, body)
                .build();
        if (reqBody != null)
            request = request.newBuilder().url(url).method(method, body).build();

        if (reqBody == null && method.equalsIgnoreCase("DELETE")) {
            RequestBody body2 = RequestBody.create(MediaType.parse(requestMediaType), "");
            request = request.newBuilder().url(url).method(method, body2).build();
        }

        for (Map.Entry<String, String> entry : reqHeader.entrySet()) {
            request = request.newBuilder().addHeader(entry.getKey(), entry.getValue()).build();
        }

        return client.newCall(request).execute();
    }

}
