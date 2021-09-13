package com.blocknitive.com.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.blocknitive.com.IntegrationTest;
import com.blocknitive.com.domain.Terrain;
import com.blocknitive.com.repository.TerrainRepository;
import com.blocknitive.com.repository.search.TerrainSearchRepository;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Integration tests for the {@link TerrainResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class TerrainResourceIT {

    private static final Integer DEFAULT_ENERGY_FOR_COMMUNITY = 1;
    private static final Integer UPDATED_ENERGY_FOR_COMMUNITY = 2;

    private static final Integer DEFAULT_RE_INVERSION = 1;
    private static final Integer UPDATED_RE_INVERSION = 2;

    private static final String ENTITY_API_URL = "/api/terrains";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/terrains";

    @Autowired
    private TerrainRepository terrainRepository;

    /**
     * This repository is mocked in the com.blocknitive.com.repository.search test package.
     *
     * @see com.blocknitive.com.repository.search.TerrainSearchRepositoryMockConfiguration
     */
    @Autowired
    private TerrainSearchRepository mockTerrainSearchRepository;

    @Autowired
    private MockMvc restTerrainMockMvc;

    private Terrain terrain;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Terrain createEntity() {
        Terrain terrain = new Terrain().energyForCommunity(DEFAULT_ENERGY_FOR_COMMUNITY).reInversion(DEFAULT_RE_INVERSION);
        return terrain;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Terrain createUpdatedEntity() {
        Terrain terrain = new Terrain().energyForCommunity(UPDATED_ENERGY_FOR_COMMUNITY).reInversion(UPDATED_RE_INVERSION);
        return terrain;
    }

    @BeforeEach
    public void initTest() {
        terrainRepository.deleteAll();
        terrain = createEntity();
    }

    @Test
    void createTerrain() throws Exception {
        int databaseSizeBeforeCreate = terrainRepository.findAll().size();
        // Create the Terrain
        restTerrainMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(terrain)))
            .andExpect(status().isCreated());

        // Validate the Terrain in the database
        List<Terrain> terrainList = terrainRepository.findAll();
        assertThat(terrainList).hasSize(databaseSizeBeforeCreate + 1);
        Terrain testTerrain = terrainList.get(terrainList.size() - 1);
        assertThat(testTerrain.getEnergyForCommunity()).isEqualTo(DEFAULT_ENERGY_FOR_COMMUNITY);
        assertThat(testTerrain.getReInversion()).isEqualTo(DEFAULT_RE_INVERSION);

        // Validate the Terrain in Elasticsearch
        verify(mockTerrainSearchRepository, times(1)).save(testTerrain);
    }

    @Test
    void createTerrainWithExistingId() throws Exception {
        // Create the Terrain with an existing ID
        terrain.setId("existing_id");

        int databaseSizeBeforeCreate = terrainRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restTerrainMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(terrain)))
            .andExpect(status().isBadRequest());

        // Validate the Terrain in the database
        List<Terrain> terrainList = terrainRepository.findAll();
        assertThat(terrainList).hasSize(databaseSizeBeforeCreate);

        // Validate the Terrain in Elasticsearch
        verify(mockTerrainSearchRepository, times(0)).save(terrain);
    }

    @Test
    void getAllTerrains() throws Exception {
        // Initialize the database
        terrainRepository.save(terrain);

        // Get all the terrainList
        restTerrainMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(terrain.getId())))
            .andExpect(jsonPath("$.[*].energyForCommunity").value(hasItem(DEFAULT_ENERGY_FOR_COMMUNITY)))
            .andExpect(jsonPath("$.[*].reInversion").value(hasItem(DEFAULT_RE_INVERSION)));
    }

    @Test
    void getTerrain() throws Exception {
        // Initialize the database
        terrainRepository.save(terrain);

        // Get the terrain
        restTerrainMockMvc
            .perform(get(ENTITY_API_URL_ID, terrain.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(terrain.getId()))
            .andExpect(jsonPath("$.energyForCommunity").value(DEFAULT_ENERGY_FOR_COMMUNITY))
            .andExpect(jsonPath("$.reInversion").value(DEFAULT_RE_INVERSION));
    }

    @Test
    void getNonExistingTerrain() throws Exception {
        // Get the terrain
        restTerrainMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    void putNewTerrain() throws Exception {
        // Initialize the database
        terrainRepository.save(terrain);

        int databaseSizeBeforeUpdate = terrainRepository.findAll().size();

        // Update the terrain
        Terrain updatedTerrain = terrainRepository.findById(terrain.getId()).get();
        updatedTerrain.energyForCommunity(UPDATED_ENERGY_FOR_COMMUNITY).reInversion(UPDATED_RE_INVERSION);

        restTerrainMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedTerrain.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedTerrain))
            )
            .andExpect(status().isOk());

        // Validate the Terrain in the database
        List<Terrain> terrainList = terrainRepository.findAll();
        assertThat(terrainList).hasSize(databaseSizeBeforeUpdate);
        Terrain testTerrain = terrainList.get(terrainList.size() - 1);
        assertThat(testTerrain.getEnergyForCommunity()).isEqualTo(UPDATED_ENERGY_FOR_COMMUNITY);
        assertThat(testTerrain.getReInversion()).isEqualTo(UPDATED_RE_INVERSION);

        // Validate the Terrain in Elasticsearch
        verify(mockTerrainSearchRepository).save(testTerrain);
    }

    @Test
    void putNonExistingTerrain() throws Exception {
        int databaseSizeBeforeUpdate = terrainRepository.findAll().size();
        terrain.setId(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTerrainMockMvc
            .perform(
                put(ENTITY_API_URL_ID, terrain.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(terrain))
            )
            .andExpect(status().isBadRequest());

        // Validate the Terrain in the database
        List<Terrain> terrainList = terrainRepository.findAll();
        assertThat(terrainList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Terrain in Elasticsearch
        verify(mockTerrainSearchRepository, times(0)).save(terrain);
    }

    @Test
    void putWithIdMismatchTerrain() throws Exception {
        int databaseSizeBeforeUpdate = terrainRepository.findAll().size();
        terrain.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTerrainMockMvc
            .perform(
                put(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(terrain))
            )
            .andExpect(status().isBadRequest());

        // Validate the Terrain in the database
        List<Terrain> terrainList = terrainRepository.findAll();
        assertThat(terrainList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Terrain in Elasticsearch
        verify(mockTerrainSearchRepository, times(0)).save(terrain);
    }

    @Test
    void putWithMissingIdPathParamTerrain() throws Exception {
        int databaseSizeBeforeUpdate = terrainRepository.findAll().size();
        terrain.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTerrainMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(terrain)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Terrain in the database
        List<Terrain> terrainList = terrainRepository.findAll();
        assertThat(terrainList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Terrain in Elasticsearch
        verify(mockTerrainSearchRepository, times(0)).save(terrain);
    }

    @Test
    void partialUpdateTerrainWithPatch() throws Exception {
        // Initialize the database
        terrainRepository.save(terrain);

        int databaseSizeBeforeUpdate = terrainRepository.findAll().size();

        // Update the terrain using partial update
        Terrain partialUpdatedTerrain = new Terrain();
        partialUpdatedTerrain.setId(terrain.getId());

        partialUpdatedTerrain.reInversion(UPDATED_RE_INVERSION);

        restTerrainMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTerrain.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTerrain))
            )
            .andExpect(status().isOk());

        // Validate the Terrain in the database
        List<Terrain> terrainList = terrainRepository.findAll();
        assertThat(terrainList).hasSize(databaseSizeBeforeUpdate);
        Terrain testTerrain = terrainList.get(terrainList.size() - 1);
        assertThat(testTerrain.getEnergyForCommunity()).isEqualTo(DEFAULT_ENERGY_FOR_COMMUNITY);
        assertThat(testTerrain.getReInversion()).isEqualTo(UPDATED_RE_INVERSION);
    }

    @Test
    void fullUpdateTerrainWithPatch() throws Exception {
        // Initialize the database
        terrainRepository.save(terrain);

        int databaseSizeBeforeUpdate = terrainRepository.findAll().size();

        // Update the terrain using partial update
        Terrain partialUpdatedTerrain = new Terrain();
        partialUpdatedTerrain.setId(terrain.getId());

        partialUpdatedTerrain.energyForCommunity(UPDATED_ENERGY_FOR_COMMUNITY).reInversion(UPDATED_RE_INVERSION);

        restTerrainMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTerrain.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTerrain))
            )
            .andExpect(status().isOk());

        // Validate the Terrain in the database
        List<Terrain> terrainList = terrainRepository.findAll();
        assertThat(terrainList).hasSize(databaseSizeBeforeUpdate);
        Terrain testTerrain = terrainList.get(terrainList.size() - 1);
        assertThat(testTerrain.getEnergyForCommunity()).isEqualTo(UPDATED_ENERGY_FOR_COMMUNITY);
        assertThat(testTerrain.getReInversion()).isEqualTo(UPDATED_RE_INVERSION);
    }

    @Test
    void patchNonExistingTerrain() throws Exception {
        int databaseSizeBeforeUpdate = terrainRepository.findAll().size();
        terrain.setId(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTerrainMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, terrain.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(terrain))
            )
            .andExpect(status().isBadRequest());

        // Validate the Terrain in the database
        List<Terrain> terrainList = terrainRepository.findAll();
        assertThat(terrainList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Terrain in Elasticsearch
        verify(mockTerrainSearchRepository, times(0)).save(terrain);
    }

    @Test
    void patchWithIdMismatchTerrain() throws Exception {
        int databaseSizeBeforeUpdate = terrainRepository.findAll().size();
        terrain.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTerrainMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(terrain))
            )
            .andExpect(status().isBadRequest());

        // Validate the Terrain in the database
        List<Terrain> terrainList = terrainRepository.findAll();
        assertThat(terrainList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Terrain in Elasticsearch
        verify(mockTerrainSearchRepository, times(0)).save(terrain);
    }

    @Test
    void patchWithMissingIdPathParamTerrain() throws Exception {
        int databaseSizeBeforeUpdate = terrainRepository.findAll().size();
        terrain.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTerrainMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(terrain)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Terrain in the database
        List<Terrain> terrainList = terrainRepository.findAll();
        assertThat(terrainList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Terrain in Elasticsearch
        verify(mockTerrainSearchRepository, times(0)).save(terrain);
    }

    @Test
    void deleteTerrain() throws Exception {
        // Initialize the database
        terrainRepository.save(terrain);

        int databaseSizeBeforeDelete = terrainRepository.findAll().size();

        // Delete the terrain
        restTerrainMockMvc
            .perform(delete(ENTITY_API_URL_ID, terrain.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Terrain> terrainList = terrainRepository.findAll();
        assertThat(terrainList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Terrain in Elasticsearch
        verify(mockTerrainSearchRepository, times(1)).deleteById(terrain.getId());
    }

    @Test
    void searchTerrain() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        terrainRepository.save(terrain);
        when(mockTerrainSearchRepository.search(queryStringQuery("id:" + terrain.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(terrain), PageRequest.of(0, 1), 1));

        // Search the terrain
        restTerrainMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + terrain.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(terrain.getId())))
            .andExpect(jsonPath("$.[*].energyForCommunity").value(hasItem(DEFAULT_ENERGY_FOR_COMMUNITY)))
            .andExpect(jsonPath("$.[*].reInversion").value(hasItem(DEFAULT_RE_INVERSION)));
    }
}
