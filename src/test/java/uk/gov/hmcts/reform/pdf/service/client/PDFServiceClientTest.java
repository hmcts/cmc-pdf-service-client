package uk.gov.hmcts.reform.pdf.service.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestOperations;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import static java.util.Collections.emptyMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class PDFServiceClientTest {

    private static final String ENDPOINT_BASE = "http://localhost";

    @Mock
    private RestOperations restClient;
    @Captor
    private ArgumentCaptor<HttpEntity> httpEntityArgument;

    private String sampleTemplate = "<html>Test</html>";

    private PDFServiceClient pdfServiceClient;

    @Before
    public void setup() {
        pdfServiceClient = PDFServiceClient.builder()
            .restOperations(restClient)
            .build(URI.create(ENDPOINT_BASE));
    }

    @Test
    public void sends_requests_to_then_new_url() {
        pdfServiceClient.generateFromHtml(sampleTemplate.getBytes(), emptyMap());

        ArgumentCaptor<URI> uriArgumentCaptor = ArgumentCaptor.forClass(URI.class);
        verify(restClient).postForObject(uriArgumentCaptor.capture(), any(), any());
        assertThat(uriArgumentCaptor.getValue().toString()).isEqualTo(ENDPOINT_BASE + "/pdfs");
    }

    @Test
    public void request_content_type_contains_versioned_mime_type() {
        pdfServiceClient.generateFromHtml(sampleTemplate.getBytes(), emptyMap());

        verify(restClient).postForObject(any(), httpEntityArgument.capture(), any());

        assertThat(httpEntityArgument.getValue().getHeaders().getContentType())
            .isEqualTo(PDFServiceClient.API_VERSION);
    }

    @Test
    public void request_accepts_pdf() {
        pdfServiceClient.generateFromHtml(sampleTemplate.getBytes(), emptyMap());

        verify(restClient).postForObject(any(), httpEntityArgument.capture(), any());

        assertThat(httpEntityArgument.getValue().getHeaders().getAccept()).contains(MediaType.APPLICATION_PDF);
    }

    @Test
    public void template_contents_are_sent_in_request_template_field() throws IOException {
        pdfServiceClient.generateFromHtml(sampleTemplate.getBytes(), emptyMap());

        verify(restClient).postForObject(any(), httpEntityArgument.capture(), any());

        GeneratePdfRequest generatePdfRequest = new ObjectMapper()
            .readValue(httpEntityArgument.getValue().getBody().toString(), GeneratePdfRequest.class);

        assertThat(generatePdfRequest.template).isEqualTo(sampleTemplate);
    }

    @Test
    public void values_are_passed_along() throws IOException {
        Map<String, Object> values = new HashMap<>();
        values.put("hello", "World!");
        values.put("Foo", "bar");

        pdfServiceClient.generateFromHtml(sampleTemplate.getBytes(), values);

        verify(restClient).postForObject(any(), httpEntityArgument.capture(), any());

        GeneratePdfRequest generatePdfRequest = new ObjectMapper()
            .readValue(httpEntityArgument.getValue().getBody().toString(), GeneratePdfRequest.class);

        assertThat(generatePdfRequest.values).containsAllEntriesOf(values).hasSameSizeAs(values);
    }

}
