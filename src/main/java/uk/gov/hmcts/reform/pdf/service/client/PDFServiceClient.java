package uk.gov.hmcts.reform.pdf.service.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.health.Health;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;
import uk.gov.hmcts.reform.pdf.service.client.exception.PDFServiceClientException;

import java.net.URI;
import java.util.Collections;
import java.util.Map;

import static java.util.Objects.requireNonNull;
import static uk.gov.hmcts.reform.pdf.service.client.util.Preconditions.requireNonEmpty;

public class PDFServiceClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(PDFServiceClient.class);

    public static final MediaType API_VERSION = MediaType
        .valueOf("application/vnd.uk.gov.hmcts.pdf-service.v2+json;charset=UTF-8");

    private final RestOperations restOperations;
    private final ObjectMapper objectMapper;

    private final URI htmlEndpoint;
    private final URI healthEndpoint;

    public PDFServiceClient(
        RestOperations restOperations,
        ObjectMapper objectMapper,
        URI pdfServiceBaseUrl
    ) {
        this.restOperations = requireNonNull(restOperations);
        this.objectMapper = requireNonNull(objectMapper);

        requireNonNull(pdfServiceBaseUrl);
        htmlEndpoint = pdfServiceBaseUrl.resolve("/pdfs");
        healthEndpoint = pdfServiceBaseUrl.resolve("/health");
    }

    public byte[] generateFromHtml(byte[] template, Map<String, Object> placeholders) {
        requireNonEmpty(template);
        requireNonNull(placeholders);

        try {
            return restOperations.postForObject(
                htmlEndpoint,
                createRequestEntityFor(template, placeholders),
                byte[].class
            );
        } catch (HttpClientErrorException e) {
            throw new PDFServiceClientException("Failed to request PDF from REST endpoint", e);
        }
    }

    /**
     * Calls the PDF service healthcheck.
     * @return health status
     */
    public Health serviceHealthy() {
        try {
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

            HttpEntity<?> entity = new HttpEntity<>("", httpHeaders);

            ResponseEntity<InternalHealth> exchange = restOperations.exchange(
                healthEndpoint,
                HttpMethod.GET,
                entity,
                InternalHealth.class);

            InternalHealth body = exchange.getBody();

            return new Health.Builder(body.status())
                .build();
        } catch (Exception ex) {
            LOGGER.error("Error on pdf service healthcheck", ex);
            return Health.down(ex)
                .build();
        }
    }

    private HttpEntity<String> createRequestEntityFor(
        byte[] template,
        Map<String, Object> placeholders) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(API_VERSION);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_PDF));

        GeneratePdfRequest request = new GeneratePdfRequest(new String(template), placeholders);
        try {
            return new HttpEntity<>(objectMapper.writeValueAsString(request), headers);
        } catch (JsonProcessingException e) {
            throw new PDFServiceClientException("Failed to convert PDF request into JSON", e);
        }
    }

    public static class Builder {
        private RestOperations restOperations = new RestTemplate();
        private ObjectMapper objectMapper = new ObjectMapper();

        public PDFServiceClient build(URI pdfServiceBaseUrl) {
            return new PDFServiceClient(restOperations, objectMapper, pdfServiceBaseUrl);
        }

        public Builder restOperations(RestOperations restOperations) {
            this.restOperations = restOperations;
            return this;
        }

        public Builder objectMapper(ObjectMapper objectMapper) {
            this.objectMapper = objectMapper;
            return this;
        }
    }

    public static Builder builder() {
        return new Builder();
    }

}
