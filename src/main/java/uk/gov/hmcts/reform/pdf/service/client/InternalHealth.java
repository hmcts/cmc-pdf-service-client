package uk.gov.hmcts.reform.pdf.service.client;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.boot.actuate.health.Status;

import java.util.Map;

/**
 * Can't manage to serialise the spring health class.
 * Jackson-java-8 modules constructor parameters won't work either :(
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class InternalHealth {

    private final Status status;

    @JsonCreator
    public InternalHealth(@JsonProperty("status") Status status) {
        this.status = status;
    }

    public Status getStatus() {
        return status;
    }
}

