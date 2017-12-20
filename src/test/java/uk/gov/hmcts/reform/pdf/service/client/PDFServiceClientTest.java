package uk.gov.hmcts.reform.pdf.service.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestOperations;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class PDFServiceClientTest {

    private String endpointBase = "http://localhost";
    private RestOperations restClient = mock(RestOperations.class);
    @Mock
    private Supplier<String> s2sAuthTokenSupplier;
    private String sampleTemplate = "<html>Test</html>";
    private PDFServiceClient pdfServiceClient;

    @Before
    public void setup() {
        pdfServiceClient = new PDFServiceClient(restClient, s2sAuthTokenSupplier, URI.create(endpointBase));
    }

    @Test
    public void sends_requests_to_then_new_url() {
        pdfServiceClient.generateFromHtml(null, sampleTemplate.getBytes(), new HashMap<>());

        ArgumentCaptor<URI> uriArgumentCaptor = ArgumentCaptor.forClass(URI.class);
        verify(restClient).postForObject(uriArgumentCaptor.capture(), any(), any());
        assertThat(uriArgumentCaptor.getValue().toString()).isEqualTo(endpointBase + "/pdfs");
    }

    @Test
    public void request_content_type_contains_versioned_mime_type() {
        pdfServiceClient.generateFromHtml(null, sampleTemplate.getBytes(), new HashMap<>());

        ArgumentCaptor<HttpEntity> httpEntityArgumentCaptor = ArgumentCaptor.forClass(HttpEntity.class);
        verify(restClient).postForObject(any(), httpEntityArgumentCaptor.capture(), any());

        assertThat(httpEntityArgumentCaptor.getValue().getHeaders().getContentType())
            .isEqualTo(PDFServiceClient.API_VERSION);
    }

    @Test
    public void request_accepts_pdf() {
        ArgumentCaptor<HttpEntity> httpEntityArgumentCaptor = ArgumentCaptor.forClass(HttpEntity.class);

        pdfServiceClient.generateFromHtml(null, sampleTemplate.getBytes(), new HashMap<>());

        verify(restClient).postForObject(any(), httpEntityArgumentCaptor.capture(), any());

        assertThat(httpEntityArgumentCaptor.getValue().getHeaders().getAccept()).contains(MediaType.APPLICATION_PDF);
    }

    @Test
    public void template_contents_are_sent_in_request_template_field() throws IOException {
        ArgumentCaptor<HttpEntity> httpEntityArgumentCaptor = ArgumentCaptor.forClass(HttpEntity.class);

        pdfServiceClient.generateFromHtml(null, sampleTemplate.getBytes(), new HashMap<>());

        verify(restClient).postForObject(any(), httpEntityArgumentCaptor.capture(), any());

        GeneratePdfRequest generatePdfRequest = new ObjectMapper()
            .readValue(httpEntityArgumentCaptor.getValue().getBody().toString(), GeneratePdfRequest.class);

        assertThat(generatePdfRequest.template).isEqualTo(sampleTemplate);
    }

    @Test
    public void values_are_passed_along() throws IOException {
        Map<String, Object> values = new HashMap<>();
        values.put("hello", "World!");
        values.put("Foo", "bar");

        pdfServiceClient.generateFromHtml(null, sampleTemplate.getBytes(), values);

        ArgumentCaptor<HttpEntity> httpEntityArgumentCaptor = ArgumentCaptor.forClass(HttpEntity.class);
        verify(restClient).postForObject(any(), httpEntityArgumentCaptor.capture(), any());

        GeneratePdfRequest generatePdfRequest = new ObjectMapper()
            .readValue(httpEntityArgumentCaptor.getValue().getBody().toString(), GeneratePdfRequest.class);

        assertThat(generatePdfRequest.values).containsAllEntriesOf(values).hasSameSizeAs(values);
    }
}
