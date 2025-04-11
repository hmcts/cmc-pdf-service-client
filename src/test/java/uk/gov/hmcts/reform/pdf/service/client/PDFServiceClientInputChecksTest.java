package uk.gov.hmcts.reform.pdf.service.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestOperations;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.Map;

import static java.util.Collections.emptyMap;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
class PDFServiceClientInputChecksTest {

    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private RestOperations restOperations;

    private URI testUri;

    private PDFServiceClient client;

    @BeforeEach
    void beforeEachTest() throws URISyntaxException {
        testUri = new URI("http://this-can-be-anything/");
        client = PDFServiceClient.builder().build(testUri);
    }

    @Test
    void shouldThrowNullPointerExceptionWhenGivenNullTemplate() {
        Map<String, Object> placeholders = emptyMap();
        assertThrows(NullPointerException.class, () ->
            client.generateFromHtml(null, placeholders)
        );
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenGivenEmptyTemplate() {
        Map<String, Object> placeholders = emptyMap();
        byte[] empty = new byte[] {};
        assertThrows(IllegalArgumentException.class, () ->
            client.generateFromHtml(empty, placeholders)
        );
    }

    @Test
    void shouldThrowNullPointerExceptionWhenGivenNullPlaceholders() {
        byte[] template = "content".getBytes(Charset.defaultCharset());
        assertThrows(NullPointerException.class, () ->
            client.generateFromHtml(template, null)
        );
    }

    @Test
    void constructorShouldThrowNullPointerWhenGivenNullServiceUrlString() {
        assertThrows(NullPointerException.class, () ->
            new PDFServiceClient(restOperations, objectMapper, null)
        );
    }

    @Test
    void constructorShouldThrowNullPointerWhenGivenNullRestOperations() {
        assertThrows(NullPointerException.class, () ->
            new PDFServiceClient(null, objectMapper, testUri)
        );
    }

    @Test
    void constructorShouldThrowNullPointerWhenGivenNullObjectMapper() {
        assertThrows(NullPointerException.class, () ->
            new PDFServiceClient(restOperations, null, testUri)
        );
    }
}
