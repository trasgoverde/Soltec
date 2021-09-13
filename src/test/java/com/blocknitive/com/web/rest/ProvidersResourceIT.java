package com.blocknitive.com.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.blocknitive.com.IntegrationTest;
import com.blocknitive.com.domain.Providers;
import com.blocknitive.com.repository.ProvidersRepository;
import com.blocknitive.com.repository.search.ProvidersSearchRepository;
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
 * Integration tests for the {@link ProvidersResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class ProvidersResourceIT {

    private static final Integer DEFAULT_AGREEMENT_PARIS = 1;
    private static final Integer UPDATED_AGREEMENT_PARIS = 2;

    private static final Boolean DEFAULT_CERTIFIED_SUSTIANABLE = false;
    private static final Boolean UPDATED_CERTIFIED_SUSTIANABLE = true;

    private static final String ENTITY_API_URL = "/api/providers";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/providers";

    @Autowired
    private ProvidersRepository providersRepository;

    /**
     * This repository is mocked in the com.blocknitive.com.repository.search test package.
     *
     * @see com.blocknitive.com.repository.search.ProvidersSearchRepositoryMockConfiguration
     */
    @Autowired
    private ProvidersSearchRepository mockProvidersSearchRepository;

    @Autowired
    private MockMvc restProvidersMockMvc;

    private Providers providers;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Providers createEntity() {
        Providers providers = new Providers().agreementParis(DEFAULT_AGREEMENT_PARIS).certifiedSustianable(DEFAULT_CERTIFIED_SUSTIANABLE);
        return providers;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Providers createUpdatedEntity() {
        Providers providers = new Providers().agreementParis(UPDATED_AGREEMENT_PARIS).certifiedSustianable(UPDATED_CERTIFIED_SUSTIANABLE);
        return providers;
    }

    @BeforeEach
    public void initTest() {
        providersRepository.deleteAll();
        providers = createEntity();
    }

    @Test
    void createProviders() throws Exception {
        int databaseSizeBeforeCreate = providersRepository.findAll().size();
        // Create the Providers
        restProvidersMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(providers)))
            .andExpect(status().isCreated());

        // Validate the Providers in the database
        List<Providers> providersList = providersRepository.findAll();
        assertThat(providersList).hasSize(databaseSizeBeforeCreate + 1);
        Providers testProviders = providersList.get(providersList.size() - 1);
        assertThat(testProviders.getAgreementParis()).isEqualTo(DEFAULT_AGREEMENT_PARIS);
        assertThat(testProviders.getCertifiedSustianable()).isEqualTo(DEFAULT_CERTIFIED_SUSTIANABLE);

        // Validate the Providers in Elasticsearch
        verify(mockProvidersSearchRepository, times(1)).save(testProviders);
    }

    @Test
    void createProvidersWithExistingId() throws Exception {
        // Create the Providers with an existing ID
        providers.setId("existing_id");

        int databaseSizeBeforeCreate = providersRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restProvidersMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(providers)))
            .andExpect(status().isBadRequest());

        // Validate the Providers in the database
        List<Providers> providersList = providersRepository.findAll();
        assertThat(providersList).hasSize(databaseSizeBeforeCreate);

        // Validate the Providers in Elasticsearch
        verify(mockProvidersSearchRepository, times(0)).save(providers);
    }

    @Test
    void getAllProviders() throws Exception {
        // Initialize the database
        providersRepository.save(providers);

        // Get all the providersList
        restProvidersMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(providers.getId())))
            .andExpect(jsonPath("$.[*].agreementParis").value(hasItem(DEFAULT_AGREEMENT_PARIS)))
            .andExpect(jsonPath("$.[*].certifiedSustianable").value(hasItem(DEFAULT_CERTIFIED_SUSTIANABLE.booleanValue())));
    }

    @Test
    void getProviders() throws Exception {
        // Initialize the database
        providersRepository.save(providers);

        // Get the providers
        restProvidersMockMvc
            .perform(get(ENTITY_API_URL_ID, providers.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(providers.getId()))
            .andExpect(jsonPath("$.agreementParis").value(DEFAULT_AGREEMENT_PARIS))
            .andExpect(jsonPath("$.certifiedSustianable").value(DEFAULT_CERTIFIED_SUSTIANABLE.booleanValue()));
    }

    @Test
    void getNonExistingProviders() throws Exception {
        // Get the providers
        restProvidersMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    void putNewProviders() throws Exception {
        // Initialize the database
        providersRepository.save(providers);

        int databaseSizeBeforeUpdate = providersRepository.findAll().size();

        // Update the providers
        Providers updatedProviders = providersRepository.findById(providers.getId()).get();
        updatedProviders.agreementParis(UPDATED_AGREEMENT_PARIS).certifiedSustianable(UPDATED_CERTIFIED_SUSTIANABLE);

        restProvidersMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedProviders.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedProviders))
            )
            .andExpect(status().isOk());

        // Validate the Providers in the database
        List<Providers> providersList = providersRepository.findAll();
        assertThat(providersList).hasSize(databaseSizeBeforeUpdate);
        Providers testProviders = providersList.get(providersList.size() - 1);
        assertThat(testProviders.getAgreementParis()).isEqualTo(UPDATED_AGREEMENT_PARIS);
        assertThat(testProviders.getCertifiedSustianable()).isEqualTo(UPDATED_CERTIFIED_SUSTIANABLE);

        // Validate the Providers in Elasticsearch
        verify(mockProvidersSearchRepository).save(testProviders);
    }

    @Test
    void putNonExistingProviders() throws Exception {
        int databaseSizeBeforeUpdate = providersRepository.findAll().size();
        providers.setId(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProvidersMockMvc
            .perform(
                put(ENTITY_API_URL_ID, providers.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(providers))
            )
            .andExpect(status().isBadRequest());

        // Validate the Providers in the database
        List<Providers> providersList = providersRepository.findAll();
        assertThat(providersList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Providers in Elasticsearch
        verify(mockProvidersSearchRepository, times(0)).save(providers);
    }

    @Test
    void putWithIdMismatchProviders() throws Exception {
        int databaseSizeBeforeUpdate = providersRepository.findAll().size();
        providers.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProvidersMockMvc
            .perform(
                put(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(providers))
            )
            .andExpect(status().isBadRequest());

        // Validate the Providers in the database
        List<Providers> providersList = providersRepository.findAll();
        assertThat(providersList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Providers in Elasticsearch
        verify(mockProvidersSearchRepository, times(0)).save(providers);
    }

    @Test
    void putWithMissingIdPathParamProviders() throws Exception {
        int databaseSizeBeforeUpdate = providersRepository.findAll().size();
        providers.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProvidersMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(providers)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Providers in the database
        List<Providers> providersList = providersRepository.findAll();
        assertThat(providersList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Providers in Elasticsearch
        verify(mockProvidersSearchRepository, times(0)).save(providers);
    }

    @Test
    void partialUpdateProvidersWithPatch() throws Exception {
        // Initialize the database
        providersRepository.save(providers);

        int databaseSizeBeforeUpdate = providersRepository.findAll().size();

        // Update the providers using partial update
        Providers partialUpdatedProviders = new Providers();
        partialUpdatedProviders.setId(providers.getId());

        partialUpdatedProviders.certifiedSustianable(UPDATED_CERTIFIED_SUSTIANABLE);

        restProvidersMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProviders.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedProviders))
            )
            .andExpect(status().isOk());

        // Validate the Providers in the database
        List<Providers> providersList = providersRepository.findAll();
        assertThat(providersList).hasSize(databaseSizeBeforeUpdate);
        Providers testProviders = providersList.get(providersList.size() - 1);
        assertThat(testProviders.getAgreementParis()).isEqualTo(DEFAULT_AGREEMENT_PARIS);
        assertThat(testProviders.getCertifiedSustianable()).isEqualTo(UPDATED_CERTIFIED_SUSTIANABLE);
    }

    @Test
    void fullUpdateProvidersWithPatch() throws Exception {
        // Initialize the database
        providersRepository.save(providers);

        int databaseSizeBeforeUpdate = providersRepository.findAll().size();

        // Update the providers using partial update
        Providers partialUpdatedProviders = new Providers();
        partialUpdatedProviders.setId(providers.getId());

        partialUpdatedProviders.agreementParis(UPDATED_AGREEMENT_PARIS).certifiedSustianable(UPDATED_CERTIFIED_SUSTIANABLE);

        restProvidersMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProviders.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedProviders))
            )
            .andExpect(status().isOk());

        // Validate the Providers in the database
        List<Providers> providersList = providersRepository.findAll();
        assertThat(providersList).hasSize(databaseSizeBeforeUpdate);
        Providers testProviders = providersList.get(providersList.size() - 1);
        assertThat(testProviders.getAgreementParis()).isEqualTo(UPDATED_AGREEMENT_PARIS);
        assertThat(testProviders.getCertifiedSustianable()).isEqualTo(UPDATED_CERTIFIED_SUSTIANABLE);
    }

    @Test
    void patchNonExistingProviders() throws Exception {
        int databaseSizeBeforeUpdate = providersRepository.findAll().size();
        providers.setId(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProvidersMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, providers.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(providers))
            )
            .andExpect(status().isBadRequest());

        // Validate the Providers in the database
        List<Providers> providersList = providersRepository.findAll();
        assertThat(providersList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Providers in Elasticsearch
        verify(mockProvidersSearchRepository, times(0)).save(providers);
    }

    @Test
    void patchWithIdMismatchProviders() throws Exception {
        int databaseSizeBeforeUpdate = providersRepository.findAll().size();
        providers.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProvidersMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(providers))
            )
            .andExpect(status().isBadRequest());

        // Validate the Providers in the database
        List<Providers> providersList = providersRepository.findAll();
        assertThat(providersList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Providers in Elasticsearch
        verify(mockProvidersSearchRepository, times(0)).save(providers);
    }

    @Test
    void patchWithMissingIdPathParamProviders() throws Exception {
        int databaseSizeBeforeUpdate = providersRepository.findAll().size();
        providers.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProvidersMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(providers))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Providers in the database
        List<Providers> providersList = providersRepository.findAll();
        assertThat(providersList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Providers in Elasticsearch
        verify(mockProvidersSearchRepository, times(0)).save(providers);
    }

    @Test
    void deleteProviders() throws Exception {
        // Initialize the database
        providersRepository.save(providers);

        int databaseSizeBeforeDelete = providersRepository.findAll().size();

        // Delete the providers
        restProvidersMockMvc
            .perform(delete(ENTITY_API_URL_ID, providers.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Providers> providersList = providersRepository.findAll();
        assertThat(providersList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Providers in Elasticsearch
        verify(mockProvidersSearchRepository, times(1)).deleteById(providers.getId());
    }

    @Test
    void searchProviders() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        providersRepository.save(providers);
        when(mockProvidersSearchRepository.search(queryStringQuery("id:" + providers.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(providers), PageRequest.of(0, 1), 1));

        // Search the providers
        restProvidersMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + providers.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(providers.getId())))
            .andExpect(jsonPath("$.[*].agreementParis").value(hasItem(DEFAULT_AGREEMENT_PARIS)))
            .andExpect(jsonPath("$.[*].certifiedSustianable").value(hasItem(DEFAULT_CERTIFIED_SUSTIANABLE.booleanValue())));
    }
}
