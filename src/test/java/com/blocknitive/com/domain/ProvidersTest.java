package com.blocknitive.com.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.blocknitive.com.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ProvidersTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Providers.class);
        Providers providers1 = new Providers();
        providers1.setId("id1");
        Providers providers2 = new Providers();
        providers2.setId(providers1.getId());
        assertThat(providers1).isEqualTo(providers2);
        providers2.setId("id2");
        assertThat(providers1).isNotEqualTo(providers2);
        providers1.setId(null);
        assertThat(providers1).isNotEqualTo(providers2);
    }
}
