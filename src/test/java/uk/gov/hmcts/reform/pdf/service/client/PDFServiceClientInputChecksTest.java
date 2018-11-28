package uk.gov.hmcts.reform.pdf.service.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.web.client.RestOperations;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;

import static java.util.Collections.emptyMap;

@RunWith(MockitoJUnitRunner.class)
public class PDFServiceClientInputChecksTest {

    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private RestOperations restOperations;

    private URI testUri;

    private PDFServiceClient client;

    @Before
    public void beforeEachTest() throws URISyntaxException {
        testUri = new URI("http://this-can-be-anything/");
        client = PDFServiceClient.builder().build(testUri);
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowIllegalArgumentExceptionWhenGivenNullTemplate() {
        client.generateFromHtml(null, emptyMap());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentExceptionWhenGivenEmptyTemplate() {
        client.generateFromHtml(new byte[] { }, emptyMap());
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowIllegalArgumentExceptionWhenGivenNullPlaceholders() {
        client.generateFromHtml("content".getBytes(Charset.defaultCharset()), null);
    }

    @Test(expected = NullPointerException.class)
    public void constructorShouldThrowNullPointerWhenGivenNullServiceURLString() {
        new PDFServiceClient(restOperations, objectMapper, null);
    }

    @Test(expected = NullPointerException.class)
    public void constructorShouldThrowNullPointerWhenGivenNullRestOperations() {
        new PDFServiceClient(null, objectMapper, testUri);
    }

    @Test(expected = NullPointerException.class)
    public void constructorShouldThrowNullPointerWhenGivenNullObjectMapper() {
        new PDFServiceClient(restOperations, null, testUri);
    }

}
