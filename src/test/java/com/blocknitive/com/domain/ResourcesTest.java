package com.blocknitive.com.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.blocknitive.com.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ResourcesTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Resources.class);
        Resources resources1 = new Resources();
        resources1.setId("id1");
        Resources resources2 = new Resources();
        resources2.setId(resources1.getId());
        assertThat(resources1).isEqualTo(resources2);
        resources2.setId("id2");
        assertThat(resources1).isNotEqualTo(resources2);
        resources1.setId(null);
        assertThat(resources1).isNotEqualTo(resources2);
    }
}
