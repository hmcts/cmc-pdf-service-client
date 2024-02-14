package uk.gov.hmcts.reform.pdf.service.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.http.HttpEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestOperations;

import java.net.URI;

import static java.util.Collections.emptyMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
class PDFServiceClientBuilderTest {

    private static final byte[] TEST_TEMPLATE = "<html><body>Hello</body></html>".getBytes();

    @Spy
    private ObjectMapper objectMapper = new ObjectMapper();
    @Mock
    private RestOperations restOperations;

    private URI baseUri = URI.create("https://some-host");

    @Test
    void itShouldBePossibleToBuildClientInstanceWithDefaults() {
        PDFServiceClient client = PDFServiceClient.builder().build(baseUri);
        assertThat(client).isNotNull();
    }

    @Test
    void itShouldUseProvidedRestOperations() {
        PDFServiceClient client = PDFServiceClient.builder()
            .restOperations(restOperations)
            .build(baseUri);

        client.generateFromHtml(TEST_TEMPLATE, emptyMap());

        verify(restOperations).postForObject(any(URI.class), any(HttpEntity.class), eq(byte[].class));
    }

    @Test
    void itShouldUseProvidedObjectMapper() throws Exception {
        PDFServiceClient client = PDFServiceClient.builder()
            .objectMapper(objectMapper)
            .restOperations(restOperations)
            .build(baseUri);

        client.generateFromHtml(TEST_TEMPLATE, emptyMap());

        verify(objectMapper).writeValueAsString(any());
    }

}
