package com.example.demo;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.HttpProcessorBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoController {

    @Autowired
    private HttpServletRequest request;

    @RequestMapping("/headers")
    @ResponseBody
    private List<List<String>> getHeaders() {
        List<List<String>> headers = new ArrayList<>();

        Enumeration<String> keys = request.getHeaderNames();
        while (keys.hasMoreElements()) {
            String key = keys.nextElement();
            Enumeration<String> values = request.getHeaders(key);
            while (values.hasMoreElements()) {
                String value = values.nextElement();
                List<String> kv = new ArrayList<>();
                kv.add(key);
                kv.add(value);
                headers.add(kv);
            }
        }

        return headers;
    }

    @RequestMapping("/proxy")
    private ResponseEntity<String> getProxy() throws Exception {
        CloseableHttpClient httpclient = HttpClients.custom().setHttpProcessor(HttpProcessorBuilder.create().build())
                .build();

        try {
            HttpGet httpGet = new HttpGet("http://localhost:8080/headers");
            httpGet.setConfig(RequestConfig.custom().build());
            List<List<String>> headers = getHeaders();
            for (List<String> header : headers) {
                httpGet.addHeader(header.get(0), header.get(1));
            }

            try (CloseableHttpResponse httpResponse = httpclient.execute(httpGet);) {
                if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    String key = HttpHeaders.CONTENT_TYPE;
                    String value = httpResponse.getFirstHeader(key).getValue();
                    HttpHeaders httpHeaders = new HttpHeaders();
                    httpHeaders.set(key, value);
                    String body = EntityUtils.toString(httpResponse.getEntity());
                    return new ResponseEntity<String>(body, httpHeaders, HttpStatus.SC_OK);
                }
            }
        } finally {
            httpclient.close();
        }

        return new ResponseEntity<String>("", new HttpHeaders(), HttpStatus.SC_OK);
    }

}