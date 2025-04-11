package uk.gov.hmcts.reform.pdf.service.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.boot.actuate.health.Status;

/**
 * Can't manage to serialise the spring health class.
 * Jackson-java-8 modules constructor parameters won't work either :(
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record InternalHealth(Status status) {
}

