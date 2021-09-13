package com.blocknitive.com.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.blocknitive.com.IntegrationTest;
import com.blocknitive.com.domain.Teams;
import com.blocknitive.com.repository.TeamsRepository;
import com.blocknitive.com.repository.search.TeamsSearchRepository;
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
 * Integration tests for the {@link TeamsResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class TeamsResourceIT {

    private static final Integer DEFAULT_ORIGIN_MATERIALS = 1;
    private static final Integer UPDATED_ORIGIN_MATERIALS = 2;

    private static final Integer DEFAULT_ORIGIN_STEAL = 1;
    private static final Integer UPDATED_ORIGIN_STEAL = 2;

    private static final Integer DEFAULT_ORIGIN_ALUMINIUM = 1;
    private static final Integer UPDATED_ORIGIN_ALUMINIUM = 2;

    private static final Boolean DEFAULT_SUSTAINABLE_PROVIDERS = false;
    private static final Boolean UPDATED_SUSTAINABLE_PROVIDERS = true;

    private static final String ENTITY_API_URL = "/api/teams";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/teams";

    @Autowired
    private TeamsRepository teamsRepository;

    /**
     * This repository is mocked in the com.blocknitive.com.repository.search test package.
     *
     * @see com.blocknitive.com.repository.search.TeamsSearchRepositoryMockConfiguration
     */
    @Autowired
    private TeamsSearchRepository mockTeamsSearchRepository;

    @Autowired
    private MockMvc restTeamsMockMvc;

    private Teams teams;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Teams createEntity() {
        Teams teams = new Teams()
            .originMaterials(DEFAULT_ORIGIN_MATERIALS)
            .originSteal(DEFAULT_ORIGIN_STEAL)
            .originAluminium(DEFAULT_ORIGIN_ALUMINIUM)
            .sustainableProviders(DEFAULT_SUSTAINABLE_PROVIDERS);
        return teams;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Teams createUpdatedEntity() {
        Teams teams = new Teams()
            .originMaterials(UPDATED_ORIGIN_MATERIALS)
            .originSteal(UPDATED_ORIGIN_STEAL)
            .originAluminium(UPDATED_ORIGIN_ALUMINIUM)
            .sustainableProviders(UPDATED_SUSTAINABLE_PROVIDERS);
        return teams;
    }

    @BeforeEach
    public void initTest() {
        teamsRepository.deleteAll();
        teams = createEntity();
    }

    @Test
    void createTeams() throws Exception {
        int databaseSizeBeforeCreate = teamsRepository.findAll().size();
        // Create the Teams
        restTeamsMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(teams)))
            .andExpect(status().isCreated());

        // Validate the Teams in the database
        List<Teams> teamsList = teamsRepository.findAll();
        assertThat(teamsList).hasSize(databaseSizeBeforeCreate + 1);
        Teams testTeams = teamsList.get(teamsList.size() - 1);
        assertThat(testTeams.getOriginMaterials()).isEqualTo(DEFAULT_ORIGIN_MATERIALS);
        assertThat(testTeams.getOriginSteal()).isEqualTo(DEFAULT_ORIGIN_STEAL);
        assertThat(testTeams.getOriginAluminium()).isEqualTo(DEFAULT_ORIGIN_ALUMINIUM);
        assertThat(testTeams.getSustainableProviders()).isEqualTo(DEFAULT_SUSTAINABLE_PROVIDERS);

        // Validate the Teams in Elasticsearch
        verify(mockTeamsSearchRepository, times(1)).save(testTeams);
    }

    @Test
    void createTeamsWithExistingId() throws Exception {
        // Create the Teams with an existing ID
        teams.setId("existing_id");

        int databaseSizeBeforeCreate = teamsRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restTeamsMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(teams)))
            .andExpect(status().isBadRequest());

        // Validate the Teams in the database
        List<Teams> teamsList = teamsRepository.findAll();
        assertThat(teamsList).hasSize(databaseSizeBeforeCreate);

        // Validate the Teams in Elasticsearch
        verify(mockTeamsSearchRepository, times(0)).save(teams);
    }

    @Test
    void getAllTeams() throws Exception {
        // Initialize the database
        teamsRepository.save(teams);

        // Get all the teamsList
        restTeamsMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(teams.getId())))
            .andExpect(jsonPath("$.[*].originMaterials").value(hasItem(DEFAULT_ORIGIN_MATERIALS)))
            .andExpect(jsonPath("$.[*].originSteal").value(hasItem(DEFAULT_ORIGIN_STEAL)))
            .andExpect(jsonPath("$.[*].originAluminium").value(hasItem(DEFAULT_ORIGIN_ALUMINIUM)))
            .andExpect(jsonPath("$.[*].sustainableProviders").value(hasItem(DEFAULT_SUSTAINABLE_PROVIDERS.booleanValue())));
    }

    @Test
    void getTeams() throws Exception {
        // Initialize the database
        teamsRepository.save(teams);

        // Get the teams
        restTeamsMockMvc
            .perform(get(ENTITY_API_URL_ID, teams.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(teams.getId()))
            .andExpect(jsonPath("$.originMaterials").value(DEFAULT_ORIGIN_MATERIALS))
            .andExpect(jsonPath("$.originSteal").value(DEFAULT_ORIGIN_STEAL))
            .andExpect(jsonPath("$.originAluminium").value(DEFAULT_ORIGIN_ALUMINIUM))
            .andExpect(jsonPath("$.sustainableProviders").value(DEFAULT_SUSTAINABLE_PROVIDERS.booleanValue()));
    }

    @Test
    void getNonExistingTeams() throws Exception {
        // Get the teams
        restTeamsMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    void putNewTeams() throws Exception {
        // Initialize the database
        teamsRepository.save(teams);

        int databaseSizeBeforeUpdate = teamsRepository.findAll().size();

        // Update the teams
        Teams updatedTeams = teamsRepository.findById(teams.getId()).get();
        updatedTeams
            .originMaterials(UPDATED_ORIGIN_MATERIALS)
            .originSteal(UPDATED_ORIGIN_STEAL)
            .originAluminium(UPDATED_ORIGIN_ALUMINIUM)
            .sustainableProviders(UPDATED_SUSTAINABLE_PROVIDERS);

        restTeamsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedTeams.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedTeams))
            )
            .andExpect(status().isOk());

        // Validate the Teams in the database
        List<Teams> teamsList = teamsRepository.findAll();
        assertThat(teamsList).hasSize(databaseSizeBeforeUpdate);
        Teams testTeams = teamsList.get(teamsList.size() - 1);
        assertThat(testTeams.getOriginMaterials()).isEqualTo(UPDATED_ORIGIN_MATERIALS);
        assertThat(testTeams.getOriginSteal()).isEqualTo(UPDATED_ORIGIN_STEAL);
        assertThat(testTeams.getOriginAluminium()).isEqualTo(UPDATED_ORIGIN_ALUMINIUM);
        assertThat(testTeams.getSustainableProviders()).isEqualTo(UPDATED_SUSTAINABLE_PROVIDERS);

        // Validate the Teams in Elasticsearch
        verify(mockTeamsSearchRepository).save(testTeams);
    }

    @Test
    void putNonExistingTeams() throws Exception {
        int databaseSizeBeforeUpdate = teamsRepository.findAll().size();
        teams.setId(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTeamsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, teams.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(teams))
            )
            .andExpect(status().isBadRequest());

        // Validate the Teams in the database
        List<Teams> teamsList = teamsRepository.findAll();
        assertThat(teamsList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Teams in Elasticsearch
        verify(mockTeamsSearchRepository, times(0)).save(teams);
    }

    @Test
    void putWithIdMismatchTeams() throws Exception {
        int databaseSizeBeforeUpdate = teamsRepository.findAll().size();
        teams.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTeamsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(teams))
            )
            .andExpect(status().isBadRequest());

        // Validate the Teams in the database
        List<Teams> teamsList = teamsRepository.findAll();
        assertThat(teamsList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Teams in Elasticsearch
        verify(mockTeamsSearchRepository, times(0)).save(teams);
    }

    @Test
    void putWithMissingIdPathParamTeams() throws Exception {
        int databaseSizeBeforeUpdate = teamsRepository.findAll().size();
        teams.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTeamsMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(teams)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Teams in the database
        List<Teams> teamsList = teamsRepository.findAll();
        assertThat(teamsList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Teams in Elasticsearch
        verify(mockTeamsSearchRepository, times(0)).save(teams);
    }

    @Test
    void partialUpdateTeamsWithPatch() throws Exception {
        // Initialize the database
        teamsRepository.save(teams);

        int databaseSizeBeforeUpdate = teamsRepository.findAll().size();

        // Update the teams using partial update
        Teams partialUpdatedTeams = new Teams();
        partialUpdatedTeams.setId(teams.getId());

        partialUpdatedTeams.originMaterials(UPDATED_ORIGIN_MATERIALS).originAluminium(UPDATED_ORIGIN_ALUMINIUM);

        restTeamsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTeams.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTeams))
            )
            .andExpect(status().isOk());

        // Validate the Teams in the database
        List<Teams> teamsList = teamsRepository.findAll();
        assertThat(teamsList).hasSize(databaseSizeBeforeUpdate);
        Teams testTeams = teamsList.get(teamsList.size() - 1);
        assertThat(testTeams.getOriginMaterials()).isEqualTo(UPDATED_ORIGIN_MATERIALS);
        assertThat(testTeams.getOriginSteal()).isEqualTo(DEFAULT_ORIGIN_STEAL);
        assertThat(testTeams.getOriginAluminium()).isEqualTo(UPDATED_ORIGIN_ALUMINIUM);
        assertThat(testTeams.getSustainableProviders()).isEqualTo(DEFAULT_SUSTAINABLE_PROVIDERS);
    }

    @Test
    void fullUpdateTeamsWithPatch() throws Exception {
        // Initialize the database
        teamsRepository.save(teams);

        int databaseSizeBeforeUpdate = teamsRepository.findAll().size();

        // Update the teams using partial update
        Teams partialUpdatedTeams = new Teams();
        partialUpdatedTeams.setId(teams.getId());

        partialUpdatedTeams
            .originMaterials(UPDATED_ORIGIN_MATERIALS)
            .originSteal(UPDATED_ORIGIN_STEAL)
            .originAluminium(UPDATED_ORIGIN_ALUMINIUM)
            .sustainableProviders(UPDATED_SUSTAINABLE_PROVIDERS);

        restTeamsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTeams.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTeams))
            )
            .andExpect(status().isOk());

        // Validate the Teams in the database
        List<Teams> teamsList = teamsRepository.findAll();
        assertThat(teamsList).hasSize(databaseSizeBeforeUpdate);
        Teams testTeams = teamsList.get(teamsList.size() - 1);
        assertThat(testTeams.getOriginMaterials()).isEqualTo(UPDATED_ORIGIN_MATERIALS);
        assertThat(testTeams.getOriginSteal()).isEqualTo(UPDATED_ORIGIN_STEAL);
        assertThat(testTeams.getOriginAluminium()).isEqualTo(UPDATED_ORIGIN_ALUMINIUM);
        assertThat(testTeams.getSustainableProviders()).isEqualTo(UPDATED_SUSTAINABLE_PROVIDERS);
    }

    @Test
    void patchNonExistingTeams() throws Exception {
        int databaseSizeBeforeUpdate = teamsRepository.findAll().size();
        teams.setId(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTeamsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, teams.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(teams))
            )
            .andExpect(status().isBadRequest());

        // Validate the Teams in the database
        List<Teams> teamsList = teamsRepository.findAll();
        assertThat(teamsList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Teams in Elasticsearch
        verify(mockTeamsSearchRepository, times(0)).save(teams);
    }

    @Test
    void patchWithIdMismatchTeams() throws Exception {
        int databaseSizeBeforeUpdate = teamsRepository.findAll().size();
        teams.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTeamsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(teams))
            )
            .andExpect(status().isBadRequest());

        // Validate the Teams in the database
        List<Teams> teamsList = teamsRepository.findAll();
        assertThat(teamsList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Teams in Elasticsearch
        verify(mockTeamsSearchRepository, times(0)).save(teams);
    }

    @Test
    void patchWithMissingIdPathParamTeams() throws Exception {
        int databaseSizeBeforeUpdate = teamsRepository.findAll().size();
        teams.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTeamsMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(teams)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Teams in the database
        List<Teams> teamsList = teamsRepository.findAll();
        assertThat(teamsList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Teams in Elasticsearch
        verify(mockTeamsSearchRepository, times(0)).save(teams);
    }

    @Test
    void deleteTeams() throws Exception {
        // Initialize the database
        teamsRepository.save(teams);

        int databaseSizeBeforeDelete = teamsRepository.findAll().size();

        // Delete the teams
        restTeamsMockMvc
            .perform(delete(ENTITY_API_URL_ID, teams.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Teams> teamsList = teamsRepository.findAll();
        assertThat(teamsList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Teams in Elasticsearch
        verify(mockTeamsSearchRepository, times(1)).deleteById(teams.getId());
    }

    @Test
    void searchTeams() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        teamsRepository.save(teams);
        when(mockTeamsSearchRepository.search(queryStringQuery("id:" + teams.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(teams), PageRequest.of(0, 1), 1));

        // Search the teams
        restTeamsMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + teams.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(teams.getId())))
            .andExpect(jsonPath("$.[*].originMaterials").value(hasItem(DEFAULT_ORIGIN_MATERIALS)))
            .andExpect(jsonPath("$.[*].originSteal").value(hasItem(DEFAULT_ORIGIN_STEAL)))
            .andExpect(jsonPath("$.[*].originAluminium").value(hasItem(DEFAULT_ORIGIN_ALUMINIUM)))
            .andExpect(jsonPath("$.[*].sustainableProviders").value(hasItem(DEFAULT_SUSTAINABLE_PROVIDERS.booleanValue())));
    }
}
