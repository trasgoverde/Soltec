package com.blocknitive.com.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.blocknitive.com.IntegrationTest;
import com.blocknitive.com.domain.Resources;
import com.blocknitive.com.repository.ResourcesRepository;
import com.blocknitive.com.repository.search.ResourcesSearchRepository;
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
 * Integration tests for the {@link ResourcesResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class ResourcesResourceIT {

    private static final Integer DEFAULT_WATER_CONSUMTION = 1;
    private static final Integer UPDATED_WATER_CONSUMTION = 2;

    private static final Integer DEFAULT_REFORESTRY_INDEX = 1;
    private static final Integer UPDATED_REFORESTRY_INDEX = 2;

    private static final String ENTITY_API_URL = "/api/resources";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/resources";

    @Autowired
    private ResourcesRepository resourcesRepository;

    /**
     * This repository is mocked in the com.blocknitive.com.repository.search test package.
     *
     * @see com.blocknitive.com.repository.search.ResourcesSearchRepositoryMockConfiguration
     */
    @Autowired
    private ResourcesSearchRepository mockResourcesSearchRepository;

    @Autowired
    private MockMvc restResourcesMockMvc;

    private Resources resources;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Resources createEntity() {
        Resources resources = new Resources().waterConsumtion(DEFAULT_WATER_CONSUMTION).reforestryIndex(DEFAULT_REFORESTRY_INDEX);
        return resources;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Resources createUpdatedEntity() {
        Resources resources = new Resources().waterConsumtion(UPDATED_WATER_CONSUMTION).reforestryIndex(UPDATED_REFORESTRY_INDEX);
        return resources;
    }

    @BeforeEach
    public void initTest() {
        resourcesRepository.deleteAll();
        resources = createEntity();
    }

    @Test
    void createResources() throws Exception {
        int databaseSizeBeforeCreate = resourcesRepository.findAll().size();
        // Create the Resources
        restResourcesMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(resources)))
            .andExpect(status().isCreated());

        // Validate the Resources in the database
        List<Resources> resourcesList = resourcesRepository.findAll();
        assertThat(resourcesList).hasSize(databaseSizeBeforeCreate + 1);
        Resources testResources = resourcesList.get(resourcesList.size() - 1);
        assertThat(testResources.getWaterConsumtion()).isEqualTo(DEFAULT_WATER_CONSUMTION);
        assertThat(testResources.getReforestryIndex()).isEqualTo(DEFAULT_REFORESTRY_INDEX);

        // Validate the Resources in Elasticsearch
        verify(mockResourcesSearchRepository, times(1)).save(testResources);
    }

    @Test
    void createResourcesWithExistingId() throws Exception {
        // Create the Resources with an existing ID
        resources.setId("existing_id");

        int databaseSizeBeforeCreate = resourcesRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restResourcesMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(resources)))
            .andExpect(status().isBadRequest());

        // Validate the Resources in the database
        List<Resources> resourcesList = resourcesRepository.findAll();
        assertThat(resourcesList).hasSize(databaseSizeBeforeCreate);

        // Validate the Resources in Elasticsearch
        verify(mockResourcesSearchRepository, times(0)).save(resources);
    }

    @Test
    void getAllResources() throws Exception {
        // Initialize the database
        resourcesRepository.save(resources);

        // Get all the resourcesList
        restResourcesMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(resources.getId())))
            .andExpect(jsonPath("$.[*].waterConsumtion").value(hasItem(DEFAULT_WATER_CONSUMTION)))
            .andExpect(jsonPath("$.[*].reforestryIndex").value(hasItem(DEFAULT_REFORESTRY_INDEX)));
    }

    @Test
    void getResources() throws Exception {
        // Initialize the database
        resourcesRepository.save(resources);

        // Get the resources
        restResourcesMockMvc
            .perform(get(ENTITY_API_URL_ID, resources.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(resources.getId()))
            .andExpect(jsonPath("$.waterConsumtion").value(DEFAULT_WATER_CONSUMTION))
            .andExpect(jsonPath("$.reforestryIndex").value(DEFAULT_REFORESTRY_INDEX));
    }

    @Test
    void getNonExistingResources() throws Exception {
        // Get the resources
        restResourcesMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    void putNewResources() throws Exception {
        // Initialize the database
        resourcesRepository.save(resources);

        int databaseSizeBeforeUpdate = resourcesRepository.findAll().size();

        // Update the resources
        Resources updatedResources = resourcesRepository.findById(resources.getId()).get();
        updatedResources.waterConsumtion(UPDATED_WATER_CONSUMTION).reforestryIndex(UPDATED_REFORESTRY_INDEX);

        restResourcesMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedResources.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedResources))
            )
            .andExpect(status().isOk());

        // Validate the Resources in the database
        List<Resources> resourcesList = resourcesRepository.findAll();
        assertThat(resourcesList).hasSize(databaseSizeBeforeUpdate);
        Resources testResources = resourcesList.get(resourcesList.size() - 1);
        assertThat(testResources.getWaterConsumtion()).isEqualTo(UPDATED_WATER_CONSUMTION);
        assertThat(testResources.getReforestryIndex()).isEqualTo(UPDATED_REFORESTRY_INDEX);

        // Validate the Resources in Elasticsearch
        verify(mockResourcesSearchRepository).save(testResources);
    }

    @Test
    void putNonExistingResources() throws Exception {
        int databaseSizeBeforeUpdate = resourcesRepository.findAll().size();
        resources.setId(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restResourcesMockMvc
            .perform(
                put(ENTITY_API_URL_ID, resources.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(resources))
            )
            .andExpect(status().isBadRequest());

        // Validate the Resources in the database
        List<Resources> resourcesList = resourcesRepository.findAll();
        assertThat(resourcesList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Resources in Elasticsearch
        verify(mockResourcesSearchRepository, times(0)).save(resources);
    }

    @Test
    void putWithIdMismatchResources() throws Exception {
        int databaseSizeBeforeUpdate = resourcesRepository.findAll().size();
        resources.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restResourcesMockMvc
            .perform(
                put(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(resources))
            )
            .andExpect(status().isBadRequest());

        // Validate the Resources in the database
        List<Resources> resourcesList = resourcesRepository.findAll();
        assertThat(resourcesList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Resources in Elasticsearch
        verify(mockResourcesSearchRepository, times(0)).save(resources);
    }

    @Test
    void putWithMissingIdPathParamResources() throws Exception {
        int databaseSizeBeforeUpdate = resourcesRepository.findAll().size();
        resources.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restResourcesMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(resources)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Resources in the database
        List<Resources> resourcesList = resourcesRepository.findAll();
        assertThat(resourcesList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Resources in Elasticsearch
        verify(mockResourcesSearchRepository, times(0)).save(resources);
    }

    @Test
    void partialUpdateResourcesWithPatch() throws Exception {
        // Initialize the database
        resourcesRepository.save(resources);

        int databaseSizeBeforeUpdate = resourcesRepository.findAll().size();

        // Update the resources using partial update
        Resources partialUpdatedResources = new Resources();
        partialUpdatedResources.setId(resources.getId());

        partialUpdatedResources.waterConsumtion(UPDATED_WATER_CONSUMTION);

        restResourcesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedResources.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedResources))
            )
            .andExpect(status().isOk());

        // Validate the Resources in the database
        List<Resources> resourcesList = resourcesRepository.findAll();
        assertThat(resourcesList).hasSize(databaseSizeBeforeUpdate);
        Resources testResources = resourcesList.get(resourcesList.size() - 1);
        assertThat(testResources.getWaterConsumtion()).isEqualTo(UPDATED_WATER_CONSUMTION);
        assertThat(testResources.getReforestryIndex()).isEqualTo(DEFAULT_REFORESTRY_INDEX);
    }

    @Test
    void fullUpdateResourcesWithPatch() throws Exception {
        // Initialize the database
        resourcesRepository.save(resources);

        int databaseSizeBeforeUpdate = resourcesRepository.findAll().size();

        // Update the resources using partial update
        Resources partialUpdatedResources = new Resources();
        partialUpdatedResources.setId(resources.getId());

        partialUpdatedResources.waterConsumtion(UPDATED_WATER_CONSUMTION).reforestryIndex(UPDATED_REFORESTRY_INDEX);

        restResourcesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedResources.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedResources))
            )
            .andExpect(status().isOk());

        // Validate the Resources in the database
        List<Resources> resourcesList = resourcesRepository.findAll();
        assertThat(resourcesList).hasSize(databaseSizeBeforeUpdate);
        Resources testResources = resourcesList.get(resourcesList.size() - 1);
        assertThat(testResources.getWaterConsumtion()).isEqualTo(UPDATED_WATER_CONSUMTION);
        assertThat(testResources.getReforestryIndex()).isEqualTo(UPDATED_REFORESTRY_INDEX);
    }

    @Test
    void patchNonExistingResources() throws Exception {
        int databaseSizeBeforeUpdate = resourcesRepository.findAll().size();
        resources.setId(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restResourcesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, resources.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(resources))
            )
            .andExpect(status().isBadRequest());

        // Validate the Resources in the database
        List<Resources> resourcesList = resourcesRepository.findAll();
        assertThat(resourcesList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Resources in Elasticsearch
        verify(mockResourcesSearchRepository, times(0)).save(resources);
    }

    @Test
    void patchWithIdMismatchResources() throws Exception {
        int databaseSizeBeforeUpdate = resourcesRepository.findAll().size();
        resources.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restResourcesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(resources))
            )
            .andExpect(status().isBadRequest());

        // Validate the Resources in the database
        List<Resources> resourcesList = resourcesRepository.findAll();
        assertThat(resourcesList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Resources in Elasticsearch
        verify(mockResourcesSearchRepository, times(0)).save(resources);
    }

    @Test
    void patchWithMissingIdPathParamResources() throws Exception {
        int databaseSizeBeforeUpdate = resourcesRepository.findAll().size();
        resources.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restResourcesMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(resources))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Resources in the database
        List<Resources> resourcesList = resourcesRepository.findAll();
        assertThat(resourcesList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Resources in Elasticsearch
        verify(mockResourcesSearchRepository, times(0)).save(resources);
    }

    @Test
    void deleteResources() throws Exception {
        // Initialize the database
        resourcesRepository.save(resources);

        int databaseSizeBeforeDelete = resourcesRepository.findAll().size();

        // Delete the resources
        restResourcesMockMvc
            .perform(delete(ENTITY_API_URL_ID, resources.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Resources> resourcesList = resourcesRepository.findAll();
        assertThat(resourcesList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Resources in Elasticsearch
        verify(mockResourcesSearchRepository, times(1)).deleteById(resources.getId());
    }

    @Test
    void searchResources() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        resourcesRepository.save(resources);
        when(mockResourcesSearchRepository.search(queryStringQuery("id:" + resources.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(resources), PageRequest.of(0, 1), 1));

        // Search the resources
        restResourcesMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + resources.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(resources.getId())))
            .andExpect(jsonPath("$.[*].waterConsumtion").value(hasItem(DEFAULT_WATER_CONSUMTION)))
            .andExpect(jsonPath("$.[*].reforestryIndex").value(hasItem(DEFAULT_REFORESTRY_INDEX)));
    }
}
