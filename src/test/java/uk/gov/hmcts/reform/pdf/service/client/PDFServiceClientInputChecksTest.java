package uk.gov.hmcts.reform.pdf.service.client;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.function.Supplier;

import static java.util.Collections.emptyMap;

@RunWith(MockitoJUnitRunner.class)
public class PDFServiceClientInputChecksTest {

    @Mock
    private Supplier<String> s2sAuthTokenSupplier;

    private URI testUri;

    private PDFServiceClient client;

    @Before
    public void beforeEachTest() throws URISyntaxException {
        testUri = new URI("http://this-can-be-anything/");
        client = new PDFServiceClient(s2sAuthTokenSupplier, testUri);
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
        new PDFServiceClient(s2sAuthTokenSupplier, null);
    }

    @Test(expected = NullPointerException.class)
    public void constructorShouldThrowNullPointerWhenGivenNullS2SAuthTokenSupplier() {
        new PDFServiceClient(null, testUri);
    }

    @Test(expected = NullPointerException.class)
    public void constructorShouldThrowNullPointerWhenGivenNullRestOperationsInstance() {
        new PDFServiceClient(null, s2sAuthTokenSupplier, testUri);
    }

}
