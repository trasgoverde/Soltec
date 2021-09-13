package com.blocknitive.com.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.blocknitive.com.IntegrationTest;
import com.blocknitive.com.domain.Dismantling;
import com.blocknitive.com.repository.DismantlingRepository;
import com.blocknitive.com.repository.search.DismantlingSearchRepository;
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
 * Integration tests for the {@link DismantlingResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class DismantlingResourceIT {

    private static final Integer DEFAULT_GUARANTEE_DISMANTLING = 1;
    private static final Integer UPDATED_GUARANTEE_DISMANTLING = 2;

    private static final String ENTITY_API_URL = "/api/dismantlings";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/dismantlings";

    @Autowired
    private DismantlingRepository dismantlingRepository;

    /**
     * This repository is mocked in the com.blocknitive.com.repository.search test package.
     *
     * @see com.blocknitive.com.repository.search.DismantlingSearchRepositoryMockConfiguration
     */
    @Autowired
    private DismantlingSearchRepository mockDismantlingSearchRepository;

    @Autowired
    private MockMvc restDismantlingMockMvc;

    private Dismantling dismantling;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Dismantling createEntity() {
        Dismantling dismantling = new Dismantling().guaranteeDismantling(DEFAULT_GUARANTEE_DISMANTLING);
        return dismantling;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Dismantling createUpdatedEntity() {
        Dismantling dismantling = new Dismantling().guaranteeDismantling(UPDATED_GUARANTEE_DISMANTLING);
        return dismantling;
    }

    @BeforeEach
    public void initTest() {
        dismantlingRepository.deleteAll();
        dismantling = createEntity();
    }

    @Test
    void createDismantling() throws Exception {
        int databaseSizeBeforeCreate = dismantlingRepository.findAll().size();
        // Create the Dismantling
        restDismantlingMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(dismantling)))
            .andExpect(status().isCreated());

        // Validate the Dismantling in the database
        List<Dismantling> dismantlingList = dismantlingRepository.findAll();
        assertThat(dismantlingList).hasSize(databaseSizeBeforeCreate + 1);
        Dismantling testDismantling = dismantlingList.get(dismantlingList.size() - 1);
        assertThat(testDismantling.getGuaranteeDismantling()).isEqualTo(DEFAULT_GUARANTEE_DISMANTLING);

        // Validate the Dismantling in Elasticsearch
        verify(mockDismantlingSearchRepository, times(1)).save(testDismantling);
    }

    @Test
    void createDismantlingWithExistingId() throws Exception {
        // Create the Dismantling with an existing ID
        dismantling.setId("existing_id");

        int databaseSizeBeforeCreate = dismantlingRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restDismantlingMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(dismantling)))
            .andExpect(status().isBadRequest());

        // Validate the Dismantling in the database
        List<Dismantling> dismantlingList = dismantlingRepository.findAll();
        assertThat(dismantlingList).hasSize(databaseSizeBeforeCreate);

        // Validate the Dismantling in Elasticsearch
        verify(mockDismantlingSearchRepository, times(0)).save(dismantling);
    }

    @Test
    void getAllDismantlings() throws Exception {
        // Initialize the database
        dismantlingRepository.save(dismantling);

        // Get all the dismantlingList
        restDismantlingMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(dismantling.getId())))
            .andExpect(jsonPath("$.[*].guaranteeDismantling").value(hasItem(DEFAULT_GUARANTEE_DISMANTLING)));
    }

    @Test
    void getDismantling() throws Exception {
        // Initialize the database
        dismantlingRepository.save(dismantling);

        // Get the dismantling
        restDismantlingMockMvc
            .perform(get(ENTITY_API_URL_ID, dismantling.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(dismantling.getId()))
            .andExpect(jsonPath("$.guaranteeDismantling").value(DEFAULT_GUARANTEE_DISMANTLING));
    }

    @Test
    void getNonExistingDismantling() throws Exception {
        // Get the dismantling
        restDismantlingMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    void putNewDismantling() throws Exception {
        // Initialize the database
        dismantlingRepository.save(dismantling);

        int databaseSizeBeforeUpdate = dismantlingRepository.findAll().size();

        // Update the dismantling
        Dismantling updatedDismantling = dismantlingRepository.findById(dismantling.getId()).get();
        updatedDismantling.guaranteeDismantling(UPDATED_GUARANTEE_DISMANTLING);

        restDismantlingMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedDismantling.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedDismantling))
            )
            .andExpect(status().isOk());

        // Validate the Dismantling in the database
        List<Dismantling> dismantlingList = dismantlingRepository.findAll();
        assertThat(dismantlingList).hasSize(databaseSizeBeforeUpdate);
        Dismantling testDismantling = dismantlingList.get(dismantlingList.size() - 1);
        assertThat(testDismantling.getGuaranteeDismantling()).isEqualTo(UPDATED_GUARANTEE_DISMANTLING);

        // Validate the Dismantling in Elasticsearch
        verify(mockDismantlingSearchRepository).save(testDismantling);
    }

    @Test
    void putNonExistingDismantling() throws Exception {
        int databaseSizeBeforeUpdate = dismantlingRepository.findAll().size();
        dismantling.setId(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDismantlingMockMvc
            .perform(
                put(ENTITY_API_URL_ID, dismantling.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(dismantling))
            )
            .andExpect(status().isBadRequest());

        // Validate the Dismantling in the database
        List<Dismantling> dismantlingList = dismantlingRepository.findAll();
        assertThat(dismantlingList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Dismantling in Elasticsearch
        verify(mockDismantlingSearchRepository, times(0)).save(dismantling);
    }

    @Test
    void putWithIdMismatchDismantling() throws Exception {
        int databaseSizeBeforeUpdate = dismantlingRepository.findAll().size();
        dismantling.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDismantlingMockMvc
            .perform(
                put(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(dismantling))
            )
            .andExpect(status().isBadRequest());

        // Validate the Dismantling in the database
        List<Dismantling> dismantlingList = dismantlingRepository.findAll();
        assertThat(dismantlingList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Dismantling in Elasticsearch
        verify(mockDismantlingSearchRepository, times(0)).save(dismantling);
    }

    @Test
    void putWithMissingIdPathParamDismantling() throws Exception {
        int databaseSizeBeforeUpdate = dismantlingRepository.findAll().size();
        dismantling.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDismantlingMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(dismantling)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Dismantling in the database
        List<Dismantling> dismantlingList = dismantlingRepository.findAll();
        assertThat(dismantlingList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Dismantling in Elasticsearch
        verify(mockDismantlingSearchRepository, times(0)).save(dismantling);
    }

    @Test
    void partialUpdateDismantlingWithPatch() throws Exception {
        // Initialize the database
        dismantlingRepository.save(dismantling);

        int databaseSizeBeforeUpdate = dismantlingRepository.findAll().size();

        // Update the dismantling using partial update
        Dismantling partialUpdatedDismantling = new Dismantling();
        partialUpdatedDismantling.setId(dismantling.getId());

        partialUpdatedDismantling.guaranteeDismantling(UPDATED_GUARANTEE_DISMANTLING);

        restDismantlingMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDismantling.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedDismantling))
            )
            .andExpect(status().isOk());

        // Validate the Dismantling in the database
        List<Dismantling> dismantlingList = dismantlingRepository.findAll();
        assertThat(dismantlingList).hasSize(databaseSizeBeforeUpdate);
        Dismantling testDismantling = dismantlingList.get(dismantlingList.size() - 1);
        assertThat(testDismantling.getGuaranteeDismantling()).isEqualTo(UPDATED_GUARANTEE_DISMANTLING);
    }

    @Test
    void fullUpdateDismantlingWithPatch() throws Exception {
        // Initialize the database
        dismantlingRepository.save(dismantling);

        int databaseSizeBeforeUpdate = dismantlingRepository.findAll().size();

        // Update the dismantling using partial update
        Dismantling partialUpdatedDismantling = new Dismantling();
        partialUpdatedDismantling.setId(dismantling.getId());

        partialUpdatedDismantling.guaranteeDismantling(UPDATED_GUARANTEE_DISMANTLING);

        restDismantlingMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDismantling.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedDismantling))
            )
            .andExpect(status().isOk());

        // Validate the Dismantling in the database
        List<Dismantling> dismantlingList = dismantlingRepository.findAll();
        assertThat(dismantlingList).hasSize(databaseSizeBeforeUpdate);
        Dismantling testDismantling = dismantlingList.get(dismantlingList.size() - 1);
        assertThat(testDismantling.getGuaranteeDismantling()).isEqualTo(UPDATED_GUARANTEE_DISMANTLING);
    }

    @Test
    void patchNonExistingDismantling() throws Exception {
        int databaseSizeBeforeUpdate = dismantlingRepository.findAll().size();
        dismantling.setId(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDismantlingMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, dismantling.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(dismantling))
            )
            .andExpect(status().isBadRequest());

        // Validate the Dismantling in the database
        List<Dismantling> dismantlingList = dismantlingRepository.findAll();
        assertThat(dismantlingList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Dismantling in Elasticsearch
        verify(mockDismantlingSearchRepository, times(0)).save(dismantling);
    }

    @Test
    void patchWithIdMismatchDismantling() throws Exception {
        int databaseSizeBeforeUpdate = dismantlingRepository.findAll().size();
        dismantling.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDismantlingMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(dismantling))
            )
            .andExpect(status().isBadRequest());

        // Validate the Dismantling in the database
        List<Dismantling> dismantlingList = dismantlingRepository.findAll();
        assertThat(dismantlingList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Dismantling in Elasticsearch
        verify(mockDismantlingSearchRepository, times(0)).save(dismantling);
    }

    @Test
    void patchWithMissingIdPathParamDismantling() throws Exception {
        int databaseSizeBeforeUpdate = dismantlingRepository.findAll().size();
        dismantling.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDismantlingMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(dismantling))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Dismantling in the database
        List<Dismantling> dismantlingList = dismantlingRepository.findAll();
        assertThat(dismantlingList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Dismantling in Elasticsearch
        verify(mockDismantlingSearchRepository, times(0)).save(dismantling);
    }

    @Test
    void deleteDismantling() throws Exception {
        // Initialize the database
        dismantlingRepository.save(dismantling);

        int databaseSizeBeforeDelete = dismantlingRepository.findAll().size();

        // Delete the dismantling
        restDismantlingMockMvc
            .perform(delete(ENTITY_API_URL_ID, dismantling.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Dismantling> dismantlingList = dismantlingRepository.findAll();
        assertThat(dismantlingList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Dismantling in Elasticsearch
        verify(mockDismantlingSearchRepository, times(1)).deleteById(dismantling.getId());
    }

    @Test
    void searchDismantling() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        dismantlingRepository.save(dismantling);
        when(mockDismantlingSearchRepository.search(queryStringQuery("id:" + dismantling.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(dismantling), PageRequest.of(0, 1), 1));

        // Search the dismantling
        restDismantlingMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + dismantling.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(dismantling.getId())))
            .andExpect(jsonPath("$.[*].guaranteeDismantling").value(hasItem(DEFAULT_GUARANTEE_DISMANTLING)));
    }
}
