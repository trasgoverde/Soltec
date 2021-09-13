package com.blocknitive.com.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.blocknitive.com.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TerrainTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Terrain.class);
        Terrain terrain1 = new Terrain();
        terrain1.setId("id1");
        Terrain terrain2 = new Terrain();
        terrain2.setId(terrain1.getId());
        assertThat(terrain1).isEqualTo(terrain2);
        terrain2.setId("id2");
        assertThat(terrain1).isNotEqualTo(terrain2);
        terrain1.setId(null);
        assertThat(terrain1).isNotEqualTo(terrain2);
    }
}
