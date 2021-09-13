package com.blocknitive.com.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.blocknitive.com.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TeamsTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Teams.class);
        Teams teams1 = new Teams();
        teams1.setId("id1");
        Teams teams2 = new Teams();
        teams2.setId(teams1.getId());
        assertThat(teams1).isEqualTo(teams2);
        teams2.setId("id2");
        assertThat(teams1).isNotEqualTo(teams2);
        teams1.setId(null);
        assertThat(teams1).isNotEqualTo(teams2);
    }
}
