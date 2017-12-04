package uk.gov.hmcts.reform.pdf.service.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestOperations;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class PDFServiceClientTest {

    private String endpointBase = "http://localhost";
    private RestOperations restClient = mock(RestOperations.class);
    private String sampleTemplate = "<html>Test</html>";
    private PDFServiceClient sut;

    @Before
    public void setup() {
        sut = new PDFServiceClient(restClient, URI.create(endpointBase));
    }

    @Test
    public void requests_new_endpoint() {
        sut.generateFromHtml(sampleTemplate.getBytes(), new HashMap<>());

        ArgumentCaptor<URI> uriArgumentCaptor = ArgumentCaptor.forClass(URI.class);
        verify(restClient).postForObject(uriArgumentCaptor.capture(), any(), any());
        assertThat(uriArgumentCaptor.getValue().toString()).isEqualTo(endpointBase + "/pdfs");
    }

    @Test
    public void request_content_type_contains_versioned_mime_type() {
        sut.generateFromHtml(sampleTemplate.getBytes(), new HashMap<>());

        ArgumentCaptor<HttpEntity> httpEntityArgumentCaptor = ArgumentCaptor.forClass(HttpEntity.class);
        verify(restClient).postForObject(any(), httpEntityArgumentCaptor.capture(), any());

        assertThat(httpEntityArgumentCaptor.getValue().getHeaders().getContentType().toString())
            .isEqualTo("application/vnd.uk.gov.hmcts.pdf-service.v2+json");
    }

    @Test
    public void request_accepts_pdf() {
        ArgumentCaptor<HttpEntity> httpEntityArgumentCaptor = ArgumentCaptor.forClass(HttpEntity.class);

        sut.generateFromHtml(sampleTemplate.getBytes(), new HashMap<>());

        verify(restClient).postForObject(any(), httpEntityArgumentCaptor.capture(), any());

        assertThat(httpEntityArgumentCaptor.getValue().getHeaders().getAccept()).contains(MediaType.APPLICATION_PDF);
    }

    @Test
    public void template_contents_are_sent_as_plain_string() throws IOException {
        ArgumentCaptor<HttpEntity> httpEntityArgumentCaptor = ArgumentCaptor.forClass(HttpEntity.class);

        sut.generateFromHtml(sampleTemplate.getBytes(), new HashMap<>());

        verify(restClient).postForObject(any(), httpEntityArgumentCaptor.capture(), any());

        GeneratePdfRequest generatePdfRequest = new ObjectMapper()
            .readValue(httpEntityArgumentCaptor.getValue().getBody().toString(), GeneratePdfRequest.class);

        assertThat(generatePdfRequest.template).isEqualTo(sampleTemplate);
    }
}
