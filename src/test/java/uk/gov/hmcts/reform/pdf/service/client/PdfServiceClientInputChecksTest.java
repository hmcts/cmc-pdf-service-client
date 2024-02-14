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

import static java.util.Collections.emptyMap;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
class PdfServiceClientInputChecksTest {

    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private RestOperations restOperations;

    private URI testUri;

    private PdfServiceClient client;

    @BeforeEach
    void beforeEachTest() throws URISyntaxException {
        testUri = new URI("http://this-can-be-anything/");
        client = PdfServiceClient.builder().build(testUri);
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenGivenNullTemplate() {
        assertThrows(NullPointerException.class, () ->
            client.generateFromHtml(null, emptyMap())
        );
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenGivenEmptyTemplate() {
        assertThrows(IllegalArgumentException.class, () ->
            client.generateFromHtml(new byte[] { }, emptyMap())
        );
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenGivenNullPlaceholders() {
        assertThrows(NullPointerException.class, () ->
            client.generateFromHtml("content".getBytes(Charset.defaultCharset()), null)
        );
    }

    @Test
    void constructorShouldThrowNullPointerWhenGivenNullServiceUrlString() {
        assertThrows(NullPointerException.class, () ->
            new PdfServiceClient(restOperations, objectMapper, null)
        );
    }

    @Test
    void constructorShouldThrowNullPointerWhenGivenNullRestOperations() {
        assertThrows(NullPointerException.class, () ->
            new PdfServiceClient(null, objectMapper, testUri)
        );
    }

    @Test
    void constructorShouldThrowNullPointerWhenGivenNullObjectMapper() {
        assertThrows(NullPointerException.class, () ->
            new PdfServiceClient(restOperations, null, testUri)
        );
    }
}
