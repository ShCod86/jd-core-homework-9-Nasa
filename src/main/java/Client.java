import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpHeaders;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;


public class Client {
    public static final String REMOTE_SERVICE_URI = "https://api.nasa.gov/planetary/apod?api_key=HRjB33Bs13lpJloItf2fizzjzDvzOi5JhTYhBJDm";
    public static final ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args) throws IOException {
        CloseableHttpResponse responseHd;
        try (CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setUserAgent("My Test Service")
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setConnectTimeout(5000)
                        .setSocketTimeout(30000)
                        .setRedirectsEnabled(false)
                        .build())
                .build()) {

            HttpGet request = new HttpGet(REMOTE_SERVICE_URI);
            request.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());

            CloseableHttpResponse response = httpClient.execute(request);
            NasaResp body = mapper.readValue(response.getEntity().getContent(), new TypeReference<>() {
            });
            String url;
            if ("video".equalsIgnoreCase(body.getMedia_type())) {
                url = body.getUrl();
                Downloader.downloadVideo(url, "\\netology\\");
            } else {
                url = body.getHdurl();
                HttpGet requestHdUrl = new HttpGet(url);
                responseHd = httpClient.execute(requestHdUrl);
                String name = url.substring(url.lastIndexOf("/") + 1);
                byte[] buffer = responseHd.getEntity().getContent().readAllBytes();
                Path file = Files.write(Path.of("\\netology\\" + name), buffer);
                System.out.printf("Файл создан: %s", file.toAbsolutePath());
            }
            System.out.println(body);
        }
    }
}
