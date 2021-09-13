package com.blocknitive.com.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.blocknitive.com.IntegrationTest;
import com.blocknitive.com.domain.HumanResources;
import com.blocknitive.com.repository.HumanResourcesRepository;
import com.blocknitive.com.repository.search.HumanResourcesSearchRepository;
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
 * Integration tests for the {@link HumanResourcesResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class HumanResourcesResourceIT {

    private static final Integer DEFAULT_INVESTMENTS_LOCALLY = 1;
    private static final Integer UPDATED_INVESTMENTS_LOCALLY = 2;

    private static final Integer DEFAULT_LABOR_ACCIDENTSINDEX = 1;
    private static final Integer UPDATED_LABOR_ACCIDENTSINDEX = 2;

    private static final String ENTITY_API_URL = "/api/human-resources";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/human-resources";

    @Autowired
    private HumanResourcesRepository humanResourcesRepository;

    /**
     * This repository is mocked in the com.blocknitive.com.repository.search test package.
     *
     * @see com.blocknitive.com.repository.search.HumanResourcesSearchRepositoryMockConfiguration
     */
    @Autowired
    private HumanResourcesSearchRepository mockHumanResourcesSearchRepository;

    @Autowired
    private MockMvc restHumanResourcesMockMvc;

    private HumanResources humanResources;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static HumanResources createEntity() {
        HumanResources humanResources = new HumanResources()
            .investmentsLocally(DEFAULT_INVESTMENTS_LOCALLY)
            .laborAccidentsindex(DEFAULT_LABOR_ACCIDENTSINDEX);
        return humanResources;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static HumanResources createUpdatedEntity() {
        HumanResources humanResources = new HumanResources()
            .investmentsLocally(UPDATED_INVESTMENTS_LOCALLY)
            .laborAccidentsindex(UPDATED_LABOR_ACCIDENTSINDEX);
        return humanResources;
    }

    @BeforeEach
    public void initTest() {
        humanResourcesRepository.deleteAll();
        humanResources = createEntity();
    }

    @Test
    void createHumanResources() throws Exception {
        int databaseSizeBeforeCreate = humanResourcesRepository.findAll().size();
        // Create the HumanResources
        restHumanResourcesMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(humanResources))
            )
            .andExpect(status().isCreated());

        // Validate the HumanResources in the database
        List<HumanResources> humanResourcesList = humanResourcesRepository.findAll();
        assertThat(humanResourcesList).hasSize(databaseSizeBeforeCreate + 1);
        HumanResources testHumanResources = humanResourcesList.get(humanResourcesList.size() - 1);
        assertThat(testHumanResources.getInvestmentsLocally()).isEqualTo(DEFAULT_INVESTMENTS_LOCALLY);
        assertThat(testHumanResources.getLaborAccidentsindex()).isEqualTo(DEFAULT_LABOR_ACCIDENTSINDEX);

        // Validate the HumanResources in Elasticsearch
        verify(mockHumanResourcesSearchRepository, times(1)).save(testHumanResources);
    }

    @Test
    void createHumanResourcesWithExistingId() throws Exception {
        // Create the HumanResources with an existing ID
        humanResources.setId("existing_id");

        int databaseSizeBeforeCreate = humanResourcesRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restHumanResourcesMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(humanResources))
            )
            .andExpect(status().isBadRequest());

        // Validate the HumanResources in the database
        List<HumanResources> humanResourcesList = humanResourcesRepository.findAll();
        assertThat(humanResourcesList).hasSize(databaseSizeBeforeCreate);

        // Validate the HumanResources in Elasticsearch
        verify(mockHumanResourcesSearchRepository, times(0)).save(humanResources);
    }

    @Test
    void getAllHumanResources() throws Exception {
        // Initialize the database
        humanResourcesRepository.save(humanResources);

        // Get all the humanResourcesList
        restHumanResourcesMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(humanResources.getId())))
            .andExpect(jsonPath("$.[*].investmentsLocally").value(hasItem(DEFAULT_INVESTMENTS_LOCALLY)))
            .andExpect(jsonPath("$.[*].laborAccidentsindex").value(hasItem(DEFAULT_LABOR_ACCIDENTSINDEX)));
    }

    @Test
    void getHumanResources() throws Exception {
        // Initialize the database
        humanResourcesRepository.save(humanResources);

        // Get the humanResources
        restHumanResourcesMockMvc
            .perform(get(ENTITY_API_URL_ID, humanResources.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(humanResources.getId()))
            .andExpect(jsonPath("$.investmentsLocally").value(DEFAULT_INVESTMENTS_LOCALLY))
            .andExpect(jsonPath("$.laborAccidentsindex").value(DEFAULT_LABOR_ACCIDENTSINDEX));
    }

    @Test
    void getNonExistingHumanResources() throws Exception {
        // Get the humanResources
        restHumanResourcesMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    void putNewHumanResources() throws Exception {
        // Initialize the database
        humanResourcesRepository.save(humanResources);

        int databaseSizeBeforeUpdate = humanResourcesRepository.findAll().size();

        // Update the humanResources
        HumanResources updatedHumanResources = humanResourcesRepository.findById(humanResources.getId()).get();
        updatedHumanResources.investmentsLocally(UPDATED_INVESTMENTS_LOCALLY).laborAccidentsindex(UPDATED_LABOR_ACCIDENTSINDEX);

        restHumanResourcesMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedHumanResources.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedHumanResources))
            )
            .andExpect(status().isOk());

        // Validate the HumanResources in the database
        List<HumanResources> humanResourcesList = humanResourcesRepository.findAll();
        assertThat(humanResourcesList).hasSize(databaseSizeBeforeUpdate);
        HumanResources testHumanResources = humanResourcesList.get(humanResourcesList.size() - 1);
        assertThat(testHumanResources.getInvestmentsLocally()).isEqualTo(UPDATED_INVESTMENTS_LOCALLY);
        assertThat(testHumanResources.getLaborAccidentsindex()).isEqualTo(UPDATED_LABOR_ACCIDENTSINDEX);

        // Validate the HumanResources in Elasticsearch
        verify(mockHumanResourcesSearchRepository).save(testHumanResources);
    }

    @Test
    void putNonExistingHumanResources() throws Exception {
        int databaseSizeBeforeUpdate = humanResourcesRepository.findAll().size();
        humanResources.setId(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restHumanResourcesMockMvc
            .perform(
                put(ENTITY_API_URL_ID, humanResources.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(humanResources))
            )
            .andExpect(status().isBadRequest());

        // Validate the HumanResources in the database
        List<HumanResources> humanResourcesList = humanResourcesRepository.findAll();
        assertThat(humanResourcesList).hasSize(databaseSizeBeforeUpdate);

        // Validate the HumanResources in Elasticsearch
        verify(mockHumanResourcesSearchRepository, times(0)).save(humanResources);
    }

    @Test
    void putWithIdMismatchHumanResources() throws Exception {
        int databaseSizeBeforeUpdate = humanResourcesRepository.findAll().size();
        humanResources.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restHumanResourcesMockMvc
            .perform(
                put(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(humanResources))
            )
            .andExpect(status().isBadRequest());

        // Validate the HumanResources in the database
        List<HumanResources> humanResourcesList = humanResourcesRepository.findAll();
        assertThat(humanResourcesList).hasSize(databaseSizeBeforeUpdate);

        // Validate the HumanResources in Elasticsearch
        verify(mockHumanResourcesSearchRepository, times(0)).save(humanResources);
    }

    @Test
    void putWithMissingIdPathParamHumanResources() throws Exception {
        int databaseSizeBeforeUpdate = humanResourcesRepository.findAll().size();
        humanResources.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restHumanResourcesMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(humanResources)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the HumanResources in the database
        List<HumanResources> humanResourcesList = humanResourcesRepository.findAll();
        assertThat(humanResourcesList).hasSize(databaseSizeBeforeUpdate);

        // Validate the HumanResources in Elasticsearch
        verify(mockHumanResourcesSearchRepository, times(0)).save(humanResources);
    }

    @Test
    void partialUpdateHumanResourcesWithPatch() throws Exception {
        // Initialize the database
        humanResourcesRepository.save(humanResources);

        int databaseSizeBeforeUpdate = humanResourcesRepository.findAll().size();

        // Update the humanResources using partial update
        HumanResources partialUpdatedHumanResources = new HumanResources();
        partialUpdatedHumanResources.setId(humanResources.getId());

        restHumanResourcesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedHumanResources.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedHumanResources))
            )
            .andExpect(status().isOk());

        // Validate the HumanResources in the database
        List<HumanResources> humanResourcesList = humanResourcesRepository.findAll();
        assertThat(humanResourcesList).hasSize(databaseSizeBeforeUpdate);
        HumanResources testHumanResources = humanResourcesList.get(humanResourcesList.size() - 1);
        assertThat(testHumanResources.getInvestmentsLocally()).isEqualTo(DEFAULT_INVESTMENTS_LOCALLY);
        assertThat(testHumanResources.getLaborAccidentsindex()).isEqualTo(DEFAULT_LABOR_ACCIDENTSINDEX);
    }

    @Test
    void fullUpdateHumanResourcesWithPatch() throws Exception {
        // Initialize the database
        humanResourcesRepository.save(humanResources);

        int databaseSizeBeforeUpdate = humanResourcesRepository.findAll().size();

        // Update the humanResources using partial update
        HumanResources partialUpdatedHumanResources = new HumanResources();
        partialUpdatedHumanResources.setId(humanResources.getId());

        partialUpdatedHumanResources.investmentsLocally(UPDATED_INVESTMENTS_LOCALLY).laborAccidentsindex(UPDATED_LABOR_ACCIDENTSINDEX);

        restHumanResourcesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedHumanResources.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedHumanResources))
            )
            .andExpect(status().isOk());

        // Validate the HumanResources in the database
        List<HumanResources> humanResourcesList = humanResourcesRepository.findAll();
        assertThat(humanResourcesList).hasSize(databaseSizeBeforeUpdate);
        HumanResources testHumanResources = humanResourcesList.get(humanResourcesList.size() - 1);
        assertThat(testHumanResources.getInvestmentsLocally()).isEqualTo(UPDATED_INVESTMENTS_LOCALLY);
        assertThat(testHumanResources.getLaborAccidentsindex()).isEqualTo(UPDATED_LABOR_ACCIDENTSINDEX);
    }

    @Test
    void patchNonExistingHumanResources() throws Exception {
        int databaseSizeBeforeUpdate = humanResourcesRepository.findAll().size();
        humanResources.setId(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restHumanResourcesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, humanResources.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(humanResources))
            )
            .andExpect(status().isBadRequest());

        // Validate the HumanResources in the database
        List<HumanResources> humanResourcesList = humanResourcesRepository.findAll();
        assertThat(humanResourcesList).hasSize(databaseSizeBeforeUpdate);

        // Validate the HumanResources in Elasticsearch
        verify(mockHumanResourcesSearchRepository, times(0)).save(humanResources);
    }

    @Test
    void patchWithIdMismatchHumanResources() throws Exception {
        int databaseSizeBeforeUpdate = humanResourcesRepository.findAll().size();
        humanResources.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restHumanResourcesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(humanResources))
            )
            .andExpect(status().isBadRequest());

        // Validate the HumanResources in the database
        List<HumanResources> humanResourcesList = humanResourcesRepository.findAll();
        assertThat(humanResourcesList).hasSize(databaseSizeBeforeUpdate);

        // Validate the HumanResources in Elasticsearch
        verify(mockHumanResourcesSearchRepository, times(0)).save(humanResources);
    }

    @Test
    void patchWithMissingIdPathParamHumanResources() throws Exception {
        int databaseSizeBeforeUpdate = humanResourcesRepository.findAll().size();
        humanResources.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restHumanResourcesMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(humanResources))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the HumanResources in the database
        List<HumanResources> humanResourcesList = humanResourcesRepository.findAll();
        assertThat(humanResourcesList).hasSize(databaseSizeBeforeUpdate);

        // Validate the HumanResources in Elasticsearch
        verify(mockHumanResourcesSearchRepository, times(0)).save(humanResources);
    }

    @Test
    void deleteHumanResources() throws Exception {
        // Initialize the database
        humanResourcesRepository.save(humanResources);

        int databaseSizeBeforeDelete = humanResourcesRepository.findAll().size();

        // Delete the humanResources
        restHumanResourcesMockMvc
            .perform(delete(ENTITY_API_URL_ID, humanResources.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<HumanResources> humanResourcesList = humanResourcesRepository.findAll();
        assertThat(humanResourcesList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the HumanResources in Elasticsearch
        verify(mockHumanResourcesSearchRepository, times(1)).deleteById(humanResources.getId());
    }

    @Test
    void searchHumanResources() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        humanResourcesRepository.save(humanResources);
        when(mockHumanResourcesSearchRepository.search(queryStringQuery("id:" + humanResources.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(humanResources), PageRequest.of(0, 1), 1));

        // Search the humanResources
        restHumanResourcesMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + humanResources.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(humanResources.getId())))
            .andExpect(jsonPath("$.[*].investmentsLocally").value(hasItem(DEFAULT_INVESTMENTS_LOCALLY)))
            .andExpect(jsonPath("$.[*].laborAccidentsindex").value(hasItem(DEFAULT_LABOR_ACCIDENTSINDEX)));
    }
}
