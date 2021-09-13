package com.blocknitive.com.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.blocknitive.com.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class MachineryTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Machinery.class);
        Machinery machinery1 = new Machinery();
        machinery1.setId("id1");
        Machinery machinery2 = new Machinery();
        machinery2.setId(machinery1.getId());
        assertThat(machinery1).isEqualTo(machinery2);
        machinery2.setId("id2");
        assertThat(machinery1).isNotEqualTo(machinery2);
        machinery1.setId(null);
        assertThat(machinery1).isNotEqualTo(machinery2);
    }
}
