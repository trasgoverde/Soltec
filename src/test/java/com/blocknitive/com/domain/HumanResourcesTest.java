package com.blocknitive.com.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.blocknitive.com.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class HumanResourcesTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(HumanResources.class);
        HumanResources humanResources1 = new HumanResources();
        humanResources1.setId("id1");
        HumanResources humanResources2 = new HumanResources();
        humanResources2.setId(humanResources1.getId());
        assertThat(humanResources1).isEqualTo(humanResources2);
        humanResources2.setId("id2");
        assertThat(humanResources1).isNotEqualTo(humanResources2);
        humanResources1.setId(null);
        assertThat(humanResources1).isNotEqualTo(humanResources2);
    }
}
