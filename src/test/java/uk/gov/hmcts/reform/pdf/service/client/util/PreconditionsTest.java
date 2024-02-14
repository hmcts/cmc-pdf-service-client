package uk.gov.hmcts.reform.pdf.service.client.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PreconditionsTest {

    @Test
    void notEmptyShouldThrowNullPointerExceptionWhenGivenEmptyArray() {
        assertThrows(NullPointerException.class, () ->
            Preconditions.requireNonEmpty(null)
        );
    }

    @Test
    void notEmptyShouldThrowIllegalArgumentExceptionWhenGivenEmptyArray() {
        assertThrows(IllegalArgumentException.class, () ->
            Preconditions.requireNonEmpty(new byte[] {})
        );
    }

    @Test
    void notEmptyShouldNotThrowWhenGivenNonEmptyArray() {
        Throwable throwable = catchThrowable(() -> Preconditions.requireNonEmpty(new byte[] { 12, 34, 56, 78 }));
        assertThat(throwable).isNull();
    }
}
