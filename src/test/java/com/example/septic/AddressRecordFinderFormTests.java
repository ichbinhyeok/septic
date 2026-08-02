package com.example.septic;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.septic.web.AddressRecordFinderForm;
import org.junit.jupiter.api.Test;

class AddressRecordFinderFormTests {
    @Test
    void rejectsAnAddressWithoutStateOrZip() {
        assertThat(new AddressRecordFinderForm("401 Church St, Nashville").isUsable()).isFalse();
    }

    @Test
    void acceptsACompleteAddressWithStateOrZip() {
        assertThat(new AddressRecordFinderForm("401 Church St, Nashville, TN").isUsable()).isTrue();
        assertThat(new AddressRecordFinderForm("401 Church St, Nashville, 37219").isUsable()).isTrue();
    }
}
