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
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;
import static uk.gov.hmcts.reform.pdf.service.client.util.Preconditions.requireNonEmpty;

public class PDFServiceClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(PDFServiceClient.class);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static final MediaType API_VERSION = MediaType.valueOf("application/vnd.uk.gov.hmcts.pdf-service.v2+json");

    private final RestOperations restOperations;
    private final Supplier<String> s2sAuthTokenSupplier;

    private final URI htmlEndpoint;
    private final URI healthEndpoint;

    public PDFServiceClient(
        Supplier<String> s2sAuthTokenSupplier,
        URI pdfServiceBaseUrl
    ) {
        this(new RestTemplate(), s2sAuthTokenSupplier, pdfServiceBaseUrl);
    }

    public PDFServiceClient(
        RestOperations restOperations,
        Supplier<String> s2sAuthTokenSupplier,
        URI pdfServiceBaseUrl
    ) {
        requireNonNull(restOperations);
        requireNonNull(s2sAuthTokenSupplier);
        requireNonNull(pdfServiceBaseUrl);

        this.restOperations = restOperations;
        this.s2sAuthTokenSupplier = s2sAuthTokenSupplier;

        htmlEndpoint = pdfServiceBaseUrl.resolve("/pdfs");
        healthEndpoint = pdfServiceBaseUrl.resolve("/health");
    }

    public byte[] generateFromHtml(String serviceAuthToken,
                                   byte[] template,
                                   Map<String, Object> placeholders) {
        requireNonEmpty(template);
        requireNonNull(placeholders);

        try {
            return restOperations.postForObject(
                htmlEndpoint,
                createRequestEntityFor(serviceAuthToken, template, placeholders),
                byte[].class);
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
            httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON_UTF8));

            HttpEntity<?> entity = new HttpEntity<Object>("", httpHeaders);

            ResponseEntity<InternalHealth> exchange = restOperations.exchange(
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

    private HttpEntity<String> createRequestEntityFor(
        String serviceAuthToken,
        byte[] template,
        Map<String, Object> placeholders) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(API_VERSION);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_PDF));
        headers.add("ServiceAuthorization", serviceAuthToken);

        GeneratePdfRequest request = new GeneratePdfRequest(new String(template), placeholders);
        try {
            return new HttpEntity<>(OBJECT_MAPPER.writeValueAsString(request), headers);
        } catch (JsonProcessingException e) {
            throw new PDFServiceClientException("Failed to convert PDF request into JSON", e);
        }
    }
}
