package com.blocknitive.com.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.blocknitive.com.IntegrationTest;
import com.blocknitive.com.domain.Machinery;
import com.blocknitive.com.repository.MachineryRepository;
import com.blocknitive.com.repository.search.MachinerySearchRepository;
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
 * Integration tests for the {@link MachineryResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class MachineryResourceIT {

    private static final Integer DEFAULT_PAYMENT_CYCLE = 1;
    private static final Integer UPDATED_PAYMENT_CYCLE = 2;

    private static final String ENTITY_API_URL = "/api/machinery";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/machinery";

    @Autowired
    private MachineryRepository machineryRepository;

    /**
     * This repository is mocked in the com.blocknitive.com.repository.search test package.
     *
     * @see com.blocknitive.com.repository.search.MachinerySearchRepositoryMockConfiguration
     */
    @Autowired
    private MachinerySearchRepository mockMachinerySearchRepository;

    @Autowired
    private MockMvc restMachineryMockMvc;

    private Machinery machinery;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Machinery createEntity() {
        Machinery machinery = new Machinery().paymentCycle(DEFAULT_PAYMENT_CYCLE);
        return machinery;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Machinery createUpdatedEntity() {
        Machinery machinery = new Machinery().paymentCycle(UPDATED_PAYMENT_CYCLE);
        return machinery;
    }

    @BeforeEach
    public void initTest() {
        machineryRepository.deleteAll();
        machinery = createEntity();
    }

    @Test
    void createMachinery() throws Exception {
        int databaseSizeBeforeCreate = machineryRepository.findAll().size();
        // Create the Machinery
        restMachineryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(machinery)))
            .andExpect(status().isCreated());

        // Validate the Machinery in the database
        List<Machinery> machineryList = machineryRepository.findAll();
        assertThat(machineryList).hasSize(databaseSizeBeforeCreate + 1);
        Machinery testMachinery = machineryList.get(machineryList.size() - 1);
        assertThat(testMachinery.getPaymentCycle()).isEqualTo(DEFAULT_PAYMENT_CYCLE);

        // Validate the Machinery in Elasticsearch
        verify(mockMachinerySearchRepository, times(1)).save(testMachinery);
    }

    @Test
    void createMachineryWithExistingId() throws Exception {
        // Create the Machinery with an existing ID
        machinery.setId("existing_id");

        int databaseSizeBeforeCreate = machineryRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restMachineryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(machinery)))
            .andExpect(status().isBadRequest());

        // Validate the Machinery in the database
        List<Machinery> machineryList = machineryRepository.findAll();
        assertThat(machineryList).hasSize(databaseSizeBeforeCreate);

        // Validate the Machinery in Elasticsearch
        verify(mockMachinerySearchRepository, times(0)).save(machinery);
    }

    @Test
    void getAllMachinery() throws Exception {
        // Initialize the database
        machineryRepository.save(machinery);

        // Get all the machineryList
        restMachineryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(machinery.getId())))
            .andExpect(jsonPath("$.[*].paymentCycle").value(hasItem(DEFAULT_PAYMENT_CYCLE)));
    }

    @Test
    void getMachinery() throws Exception {
        // Initialize the database
        machineryRepository.save(machinery);

        // Get the machinery
        restMachineryMockMvc
            .perform(get(ENTITY_API_URL_ID, machinery.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(machinery.getId()))
            .andExpect(jsonPath("$.paymentCycle").value(DEFAULT_PAYMENT_CYCLE));
    }

    @Test
    void getNonExistingMachinery() throws Exception {
        // Get the machinery
        restMachineryMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    void putNewMachinery() throws Exception {
        // Initialize the database
        machineryRepository.save(machinery);

        int databaseSizeBeforeUpdate = machineryRepository.findAll().size();

        // Update the machinery
        Machinery updatedMachinery = machineryRepository.findById(machinery.getId()).get();
        updatedMachinery.paymentCycle(UPDATED_PAYMENT_CYCLE);

        restMachineryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedMachinery.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedMachinery))
            )
            .andExpect(status().isOk());

        // Validate the Machinery in the database
        List<Machinery> machineryList = machineryRepository.findAll();
        assertThat(machineryList).hasSize(databaseSizeBeforeUpdate);
        Machinery testMachinery = machineryList.get(machineryList.size() - 1);
        assertThat(testMachinery.getPaymentCycle()).isEqualTo(UPDATED_PAYMENT_CYCLE);

        // Validate the Machinery in Elasticsearch
        verify(mockMachinerySearchRepository).save(testMachinery);
    }

    @Test
    void putNonExistingMachinery() throws Exception {
        int databaseSizeBeforeUpdate = machineryRepository.findAll().size();
        machinery.setId(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMachineryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, machinery.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(machinery))
            )
            .andExpect(status().isBadRequest());

        // Validate the Machinery in the database
        List<Machinery> machineryList = machineryRepository.findAll();
        assertThat(machineryList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Machinery in Elasticsearch
        verify(mockMachinerySearchRepository, times(0)).save(machinery);
    }

    @Test
    void putWithIdMismatchMachinery() throws Exception {
        int databaseSizeBeforeUpdate = machineryRepository.findAll().size();
        machinery.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMachineryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(machinery))
            )
            .andExpect(status().isBadRequest());

        // Validate the Machinery in the database
        List<Machinery> machineryList = machineryRepository.findAll();
        assertThat(machineryList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Machinery in Elasticsearch
        verify(mockMachinerySearchRepository, times(0)).save(machinery);
    }

    @Test
    void putWithMissingIdPathParamMachinery() throws Exception {
        int databaseSizeBeforeUpdate = machineryRepository.findAll().size();
        machinery.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMachineryMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(machinery)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Machinery in the database
        List<Machinery> machineryList = machineryRepository.findAll();
        assertThat(machineryList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Machinery in Elasticsearch
        verify(mockMachinerySearchRepository, times(0)).save(machinery);
    }

    @Test
    void partialUpdateMachineryWithPatch() throws Exception {
        // Initialize the database
        machineryRepository.save(machinery);

        int databaseSizeBeforeUpdate = machineryRepository.findAll().size();

        // Update the machinery using partial update
        Machinery partialUpdatedMachinery = new Machinery();
        partialUpdatedMachinery.setId(machinery.getId());

        partialUpdatedMachinery.paymentCycle(UPDATED_PAYMENT_CYCLE);

        restMachineryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedMachinery.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedMachinery))
            )
            .andExpect(status().isOk());

        // Validate the Machinery in the database
        List<Machinery> machineryList = machineryRepository.findAll();
        assertThat(machineryList).hasSize(databaseSizeBeforeUpdate);
        Machinery testMachinery = machineryList.get(machineryList.size() - 1);
        assertThat(testMachinery.getPaymentCycle()).isEqualTo(UPDATED_PAYMENT_CYCLE);
    }

    @Test
    void fullUpdateMachineryWithPatch() throws Exception {
        // Initialize the database
        machineryRepository.save(machinery);

        int databaseSizeBeforeUpdate = machineryRepository.findAll().size();

        // Update the machinery using partial update
        Machinery partialUpdatedMachinery = new Machinery();
        partialUpdatedMachinery.setId(machinery.getId());

        partialUpdatedMachinery.paymentCycle(UPDATED_PAYMENT_CYCLE);

        restMachineryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedMachinery.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedMachinery))
            )
            .andExpect(status().isOk());

        // Validate the Machinery in the database
        List<Machinery> machineryList = machineryRepository.findAll();
        assertThat(machineryList).hasSize(databaseSizeBeforeUpdate);
        Machinery testMachinery = machineryList.get(machineryList.size() - 1);
        assertThat(testMachinery.getPaymentCycle()).isEqualTo(UPDATED_PAYMENT_CYCLE);
    }

    @Test
    void patchNonExistingMachinery() throws Exception {
        int databaseSizeBeforeUpdate = machineryRepository.findAll().size();
        machinery.setId(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMachineryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, machinery.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(machinery))
            )
            .andExpect(status().isBadRequest());

        // Validate the Machinery in the database
        List<Machinery> machineryList = machineryRepository.findAll();
        assertThat(machineryList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Machinery in Elasticsearch
        verify(mockMachinerySearchRepository, times(0)).save(machinery);
    }

    @Test
    void patchWithIdMismatchMachinery() throws Exception {
        int databaseSizeBeforeUpdate = machineryRepository.findAll().size();
        machinery.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMachineryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(machinery))
            )
            .andExpect(status().isBadRequest());

        // Validate the Machinery in the database
        List<Machinery> machineryList = machineryRepository.findAll();
        assertThat(machineryList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Machinery in Elasticsearch
        verify(mockMachinerySearchRepository, times(0)).save(machinery);
    }

    @Test
    void patchWithMissingIdPathParamMachinery() throws Exception {
        int databaseSizeBeforeUpdate = machineryRepository.findAll().size();
        machinery.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMachineryMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(machinery))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Machinery in the database
        List<Machinery> machineryList = machineryRepository.findAll();
        assertThat(machineryList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Machinery in Elasticsearch
        verify(mockMachinerySearchRepository, times(0)).save(machinery);
    }

    @Test
    void deleteMachinery() throws Exception {
        // Initialize the database
        machineryRepository.save(machinery);

        int databaseSizeBeforeDelete = machineryRepository.findAll().size();

        // Delete the machinery
        restMachineryMockMvc
            .perform(delete(ENTITY_API_URL_ID, machinery.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Machinery> machineryList = machineryRepository.findAll();
        assertThat(machineryList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Machinery in Elasticsearch
        verify(mockMachinerySearchRepository, times(1)).deleteById(machinery.getId());
    }

    @Test
    void searchMachinery() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        machineryRepository.save(machinery);
        when(mockMachinerySearchRepository.search(queryStringQuery("id:" + machinery.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(machinery), PageRequest.of(0, 1), 1));

        // Search the machinery
        restMachineryMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + machinery.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(machinery.getId())))
            .andExpect(jsonPath("$.[*].paymentCycle").value(hasItem(DEFAULT_PAYMENT_CYCLE)));
    }
}
