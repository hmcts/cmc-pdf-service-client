package uk.gov.hmcts.reform.pdf.service.client.exception;

public class PDFServiceClientException extends RuntimeException {

    public PDFServiceClientException(Exception cause) {
        super(cause);
    }

}