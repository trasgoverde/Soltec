package com.blocknitive.com.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.blocknitive.com.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class LogisticsTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Logistics.class);
        Logistics logistics1 = new Logistics();
        logistics1.setId("id1");
        Logistics logistics2 = new Logistics();
        logistics2.setId(logistics1.getId());
        assertThat(logistics1).isEqualTo(logistics2);
        logistics2.setId("id2");
        assertThat(logistics1).isNotEqualTo(logistics2);
        logistics1.setId(null);
        assertThat(logistics1).isNotEqualTo(logistics2);
    }
}
