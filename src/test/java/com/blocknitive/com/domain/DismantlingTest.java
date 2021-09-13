package com.blocknitive.com.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.blocknitive.com.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class DismantlingTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Dismantling.class);
        Dismantling dismantling1 = new Dismantling();
        dismantling1.setId("id1");
        Dismantling dismantling2 = new Dismantling();
        dismantling2.setId(dismantling1.getId());
        assertThat(dismantling1).isEqualTo(dismantling2);
        dismantling2.setId("id2");
        assertThat(dismantling1).isNotEqualTo(dismantling2);
        dismantling1.setId(null);
        assertThat(dismantling1).isNotEqualTo(dismantling2);
    }
}
