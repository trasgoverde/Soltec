package com.blocknitive.com.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.blocknitive.com.IntegrationTest;
import com.blocknitive.com.domain.Logistics;
import com.blocknitive.com.repository.LogisticsRepository;
import com.blocknitive.com.repository.search.LogisticsSearchRepository;
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
 * Integration tests for the {@link LogisticsResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class LogisticsResourceIT {

    private static final Integer DEFAULT_CO_2_EMITIONS = 1;
    private static final Integer UPDATED_CO_2_EMITIONS = 2;

    private static final String ENTITY_API_URL = "/api/logistics";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/logistics";

    @Autowired
    private LogisticsRepository logisticsRepository;

    /**
     * This repository is mocked in the com.blocknitive.com.repository.search test package.
     *
     * @see com.blocknitive.com.repository.search.LogisticsSearchRepositoryMockConfiguration
     */
    @Autowired
    private LogisticsSearchRepository mockLogisticsSearchRepository;

    @Autowired
    private MockMvc restLogisticsMockMvc;

    private Logistics logistics;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Logistics createEntity() {
        Logistics logistics = new Logistics().co2Emitions(DEFAULT_CO_2_EMITIONS);
        return logistics;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Logistics createUpdatedEntity() {
        Logistics logistics = new Logistics().co2Emitions(UPDATED_CO_2_EMITIONS);
        return logistics;
    }

    @BeforeEach
    public void initTest() {
        logisticsRepository.deleteAll();
        logistics = createEntity();
    }

    @Test
    void createLogistics() throws Exception {
        int databaseSizeBeforeCreate = logisticsRepository.findAll().size();
        // Create the Logistics
        restLogisticsMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(logistics)))
            .andExpect(status().isCreated());

        // Validate the Logistics in the database
        List<Logistics> logisticsList = logisticsRepository.findAll();
        assertThat(logisticsList).hasSize(databaseSizeBeforeCreate + 1);
        Logistics testLogistics = logisticsList.get(logisticsList.size() - 1);
        assertThat(testLogistics.getCo2Emitions()).isEqualTo(DEFAULT_CO_2_EMITIONS);

        // Validate the Logistics in Elasticsearch
        verify(mockLogisticsSearchRepository, times(1)).save(testLogistics);
    }

    @Test
    void createLogisticsWithExistingId() throws Exception {
        // Create the Logistics with an existing ID
        logistics.setId("existing_id");

        int databaseSizeBeforeCreate = logisticsRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restLogisticsMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(logistics)))
            .andExpect(status().isBadRequest());

        // Validate the Logistics in the database
        List<Logistics> logisticsList = logisticsRepository.findAll();
        assertThat(logisticsList).hasSize(databaseSizeBeforeCreate);

        // Validate the Logistics in Elasticsearch
        verify(mockLogisticsSearchRepository, times(0)).save(logistics);
    }

    @Test
    void getAllLogistics() throws Exception {
        // Initialize the database
        logisticsRepository.save(logistics);

        // Get all the logisticsList
        restLogisticsMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(logistics.getId())))
            .andExpect(jsonPath("$.[*].co2Emitions").value(hasItem(DEFAULT_CO_2_EMITIONS)));
    }

    @Test
    void getLogistics() throws Exception {
        // Initialize the database
        logisticsRepository.save(logistics);

        // Get the logistics
        restLogisticsMockMvc
            .perform(get(ENTITY_API_URL_ID, logistics.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(logistics.getId()))
            .andExpect(jsonPath("$.co2Emitions").value(DEFAULT_CO_2_EMITIONS));
    }

    @Test
    void getNonExistingLogistics() throws Exception {
        // Get the logistics
        restLogisticsMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    void putNewLogistics() throws Exception {
        // Initialize the database
        logisticsRepository.save(logistics);

        int databaseSizeBeforeUpdate = logisticsRepository.findAll().size();

        // Update the logistics
        Logistics updatedLogistics = logisticsRepository.findById(logistics.getId()).get();
        updatedLogistics.co2Emitions(UPDATED_CO_2_EMITIONS);

        restLogisticsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedLogistics.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedLogistics))
            )
            .andExpect(status().isOk());

        // Validate the Logistics in the database
        List<Logistics> logisticsList = logisticsRepository.findAll();
        assertThat(logisticsList).hasSize(databaseSizeBeforeUpdate);
        Logistics testLogistics = logisticsList.get(logisticsList.size() - 1);
        assertThat(testLogistics.getCo2Emitions()).isEqualTo(UPDATED_CO_2_EMITIONS);

        // Validate the Logistics in Elasticsearch
        verify(mockLogisticsSearchRepository).save(testLogistics);
    }

    @Test
    void putNonExistingLogistics() throws Exception {
        int databaseSizeBeforeUpdate = logisticsRepository.findAll().size();
        logistics.setId(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLogisticsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, logistics.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(logistics))
            )
            .andExpect(status().isBadRequest());

        // Validate the Logistics in the database
        List<Logistics> logisticsList = logisticsRepository.findAll();
        assertThat(logisticsList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Logistics in Elasticsearch
        verify(mockLogisticsSearchRepository, times(0)).save(logistics);
    }

    @Test
    void putWithIdMismatchLogistics() throws Exception {
        int databaseSizeBeforeUpdate = logisticsRepository.findAll().size();
        logistics.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLogisticsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(logistics))
            )
            .andExpect(status().isBadRequest());

        // Validate the Logistics in the database
        List<Logistics> logisticsList = logisticsRepository.findAll();
        assertThat(logisticsList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Logistics in Elasticsearch
        verify(mockLogisticsSearchRepository, times(0)).save(logistics);
    }

    @Test
    void putWithMissingIdPathParamLogistics() throws Exception {
        int databaseSizeBeforeUpdate = logisticsRepository.findAll().size();
        logistics.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLogisticsMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(logistics)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Logistics in the database
        List<Logistics> logisticsList = logisticsRepository.findAll();
        assertThat(logisticsList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Logistics in Elasticsearch
        verify(mockLogisticsSearchRepository, times(0)).save(logistics);
    }

    @Test
    void partialUpdateLogisticsWithPatch() throws Exception {
        // Initialize the database
        logisticsRepository.save(logistics);

        int databaseSizeBeforeUpdate = logisticsRepository.findAll().size();

        // Update the logistics using partial update
        Logistics partialUpdatedLogistics = new Logistics();
        partialUpdatedLogistics.setId(logistics.getId());

        restLogisticsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedLogistics.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedLogistics))
            )
            .andExpect(status().isOk());

        // Validate the Logistics in the database
        List<Logistics> logisticsList = logisticsRepository.findAll();
        assertThat(logisticsList).hasSize(databaseSizeBeforeUpdate);
        Logistics testLogistics = logisticsList.get(logisticsList.size() - 1);
        assertThat(testLogistics.getCo2Emitions()).isEqualTo(DEFAULT_CO_2_EMITIONS);
    }

    @Test
    void fullUpdateLogisticsWithPatch() throws Exception {
        // Initialize the database
        logisticsRepository.save(logistics);

        int databaseSizeBeforeUpdate = logisticsRepository.findAll().size();

        // Update the logistics using partial update
        Logistics partialUpdatedLogistics = new Logistics();
        partialUpdatedLogistics.setId(logistics.getId());

        partialUpdatedLogistics.co2Emitions(UPDATED_CO_2_EMITIONS);

        restLogisticsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedLogistics.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedLogistics))
            )
            .andExpect(status().isOk());

        // Validate the Logistics in the database
        List<Logistics> logisticsList = logisticsRepository.findAll();
        assertThat(logisticsList).hasSize(databaseSizeBeforeUpdate);
        Logistics testLogistics = logisticsList.get(logisticsList.size() - 1);
        assertThat(testLogistics.getCo2Emitions()).isEqualTo(UPDATED_CO_2_EMITIONS);
    }

    @Test
    void patchNonExistingLogistics() throws Exception {
        int databaseSizeBeforeUpdate = logisticsRepository.findAll().size();
        logistics.setId(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLogisticsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, logistics.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(logistics))
            )
            .andExpect(status().isBadRequest());

        // Validate the Logistics in the database
        List<Logistics> logisticsList = logisticsRepository.findAll();
        assertThat(logisticsList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Logistics in Elasticsearch
        verify(mockLogisticsSearchRepository, times(0)).save(logistics);
    }

    @Test
    void patchWithIdMismatchLogistics() throws Exception {
        int databaseSizeBeforeUpdate = logisticsRepository.findAll().size();
        logistics.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLogisticsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(logistics))
            )
            .andExpect(status().isBadRequest());

        // Validate the Logistics in the database
        List<Logistics> logisticsList = logisticsRepository.findAll();
        assertThat(logisticsList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Logistics in Elasticsearch
        verify(mockLogisticsSearchRepository, times(0)).save(logistics);
    }

    @Test
    void patchWithMissingIdPathParamLogistics() throws Exception {
        int databaseSizeBeforeUpdate = logisticsRepository.findAll().size();
        logistics.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLogisticsMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(logistics))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Logistics in the database
        List<Logistics> logisticsList = logisticsRepository.findAll();
        assertThat(logisticsList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Logistics in Elasticsearch
        verify(mockLogisticsSearchRepository, times(0)).save(logistics);
    }

    @Test
    void deleteLogistics() throws Exception {
        // Initialize the database
        logisticsRepository.save(logistics);

        int databaseSizeBeforeDelete = logisticsRepository.findAll().size();

        // Delete the logistics
        restLogisticsMockMvc
            .perform(delete(ENTITY_API_URL_ID, logistics.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Logistics> logisticsList = logisticsRepository.findAll();
        assertThat(logisticsList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Logistics in Elasticsearch
        verify(mockLogisticsSearchRepository, times(1)).deleteById(logistics.getId());
    }

    @Test
    void searchLogistics() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        logisticsRepository.save(logistics);
        when(mockLogisticsSearchRepository.search(queryStringQuery("id:" + logistics.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(logistics), PageRequest.of(0, 1), 1));

        // Search the logistics
        restLogisticsMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + logistics.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(logistics.getId())))
            .andExpect(jsonPath("$.[*].co2Emitions").value(hasItem(DEFAULT_CO_2_EMITIONS)));
    }
}
