package uk.gov.hmcts.reform.pdf.service.client;

import java.util.Map;

public record GeneratePdfRequest(String template, Map<String, Object> values) {
}
