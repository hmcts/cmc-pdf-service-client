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
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private final RestOperations restTemplate;
    private final URI htmlEndpoint;
    private final URI healthEndpoint;

    public PDFServiceClient(URI pdfServiceBaseUrl) {
        this(new RestTemplate(), pdfServiceBaseUrl);
    }

    public PDFServiceClient(RestOperations restTemplate, URI pdfServiceBaseUrl) {
        requireNonNull(pdfServiceBaseUrl);

        this.restTemplate = restTemplate;
        htmlEndpoint = pdfServiceBaseUrl.resolve("/pdfs");
        healthEndpoint = pdfServiceBaseUrl.resolve("/health");
    }

    public byte[] generateFromHtml(byte[] template, Map<String, Object> placeholders) {
        requireNonEmpty(template);
        requireNonNull(placeholders);

        try {
            return restTemplate.postForObject(
                htmlEndpoint,
                requestEntityFor(template, placeholders),
                byte[].class);
        } catch (HttpClientErrorException e) {
            throw new PDFServiceClientException(e);
        }
    }

    /**
     * Calls the PDF service healthcheck.
     * @return health status
     */
    public Health serviceHealthy() {
        try {
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON_UTF8));

            HttpEntity<?> entity = new HttpEntity<Object>("", httpHeaders);

            ResponseEntity<InternalHealth> exchange = restTemplate.exchange(
                healthEndpoint,
                HttpMethod.GET,
                entity,
                InternalHealth.class);

            InternalHealth body = exchange.getBody();

            return new Health.Builder(body.getStatus())
                .build();
        } catch (Exception ex) {
            LOGGER.error("Error on pdf service healthcheck", ex);
            return Health.down(ex)
                .build();
        }
    }

    private HttpEntity<String> requestEntityFor(
        byte[] template,
        Map<String, Object> placeholders) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf("application/vnd.uk.gov.hmcts.pdf-service.v2+json"));
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_PDF));

        GeneratePdfRequest request = new GeneratePdfRequest(new String(template), placeholders);
        try {
            return new HttpEntity<>(OBJECT_MAPPER.writeValueAsString(request), headers);
        } catch (JsonProcessingException e) {
            throw new PDFServiceClientException(e);
        }
    }
}
