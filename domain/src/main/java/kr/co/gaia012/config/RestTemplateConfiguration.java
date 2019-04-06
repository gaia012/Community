package kr.co.gaia012.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

@Slf4j
@Configuration
public class RestTemplateConfiguration {

    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        ClientHttpRequestInterceptor interceptor = (HttpRequest request, byte[] body, ClientHttpRequestExecution execution) -> {
            log.info(String.format("request to URI %s with HTTP verb '%s'", request.getURI(), request.getMethod().toString()));
            return execution.execute(request, body);
        };
        RestTemplate restTemplate = new RestTemplate(new TrustEverythingClientHttpRequestFactory());
        restTemplate.setErrorHandler(new NoErrorResponseErrorHandler());

//        RestTemplate restTemplate = new RestTemplateBuilder()
//                .additionalInterceptors(interceptor)
//                .requestFactory(new TrustEverythingClientHttpRequestFactory())
//                .errorHandler(new NoErrorResponseErrorHandler()).build();
        return restTemplate;
    }

    private static class NoErrorResponseErrorHandler extends DefaultResponseErrorHandler {
        @Override
        public boolean hasError(ClientHttpResponse response) throws IOException {
            return false;
        }
    }

    private static final class TrustEverythingClientHttpRequestFactory extends SimpleClientHttpRequestFactory {
        private static SSLContext getSslContext(TrustManager trustManager) {
            try {
                SSLContext sslContext = SSLContext.getInstance("SSL");
                sslContext.init(null, new TrustManager[]{trustManager}, null);
                return sslContext;
            } catch (KeyManagementException | NoSuchAlgorithmException e) {
                throw new RuntimeException();
            }
        }

        @Override
        protected HttpURLConnection openConnection(URL url, Proxy proxy) throws IOException {
            HttpURLConnection connection = super.openConnection(url, proxy);

            if (connection instanceof HttpsURLConnection) {
                HttpsURLConnection httpsURLConnection = (HttpsURLConnection) connection;
                SSLContext sslContext = getSslContext(new TrustEverythingTrustManager());
                httpsURLConnection.setSSLSocketFactory(sslContext.getSocketFactory());
                httpsURLConnection.setHostnameVerifier((s, session) -> true);
            }
            return connection;
        }
    }

    private static final class TrustEverythingTrustManager implements X509TrustManager {

        @Override
        public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

        }

        @Override
        public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }


}
