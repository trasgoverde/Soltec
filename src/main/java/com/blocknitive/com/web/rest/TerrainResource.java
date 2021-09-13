package com.blocknitive.com.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.blocknitive.com.domain.Terrain;
import com.blocknitive.com.repository.TerrainRepository;
import com.blocknitive.com.repository.search.TerrainSearchRepository;
import com.blocknitive.com.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.blocknitive.com.domain.Terrain}.
 */
@RestController
@RequestMapping("/api")
public class TerrainResource {

    private final Logger log = LoggerFactory.getLogger(TerrainResource.class);

    private static final String ENTITY_NAME = "terrain";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TerrainRepository terrainRepository;

    private final TerrainSearchRepository terrainSearchRepository;

    public TerrainResource(TerrainRepository terrainRepository, TerrainSearchRepository terrainSearchRepository) {
        this.terrainRepository = terrainRepository;
        this.terrainSearchRepository = terrainSearchRepository;
    }

    /**
     * {@code POST  /terrains} : Create a new terrain.
     *
     * @param terrain the terrain to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new terrain, or with status {@code 400 (Bad Request)} if the terrain has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/terrains")
    public ResponseEntity<Terrain> createTerrain(@RequestBody Terrain terrain) throws URISyntaxException {
        log.debug("REST request to save Terrain : {}", terrain);
        if (terrain.getId() != null) {
            throw new BadRequestAlertException("A new terrain cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Terrain result = terrainRepository.save(terrain);
        terrainSearchRepository.save(result);
        return ResponseEntity
            .created(new URI("/api/terrains/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId()))
            .body(result);
    }

    /**
     * {@code PUT  /terrains/:id} : Updates an existing terrain.
     *
     * @param id the id of the terrain to save.
     * @param terrain the terrain to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated terrain,
     * or with status {@code 400 (Bad Request)} if the terrain is not valid,
     * or with status {@code 500 (Internal Server Error)} if the terrain couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/terrains/{id}")
    public ResponseEntity<Terrain> updateTerrain(
        @PathVariable(value = "id", required = false) final String id,
        @RequestBody Terrain terrain
    ) throws URISyntaxException {
        log.debug("REST request to update Terrain : {}, {}", id, terrain);
        if (terrain.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, terrain.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!terrainRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Terrain result = terrainRepository.save(terrain);
        terrainSearchRepository.save(result);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, terrain.getId()))
            .body(result);
    }

    /**
     * {@code PATCH  /terrains/:id} : Partial updates given fields of an existing terrain, field will ignore if it is null
     *
     * @param id the id of the terrain to save.
     * @param terrain the terrain to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated terrain,
     * or with status {@code 400 (Bad Request)} if the terrain is not valid,
     * or with status {@code 404 (Not Found)} if the terrain is not found,
     * or with status {@code 500 (Internal Server Error)} if the terrain couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/terrains/{id}", consumes = "application/merge-patch+json")
    public ResponseEntity<Terrain> partialUpdateTerrain(
        @PathVariable(value = "id", required = false) final String id,
        @RequestBody Terrain terrain
    ) throws URISyntaxException {
        log.debug("REST request to partial update Terrain partially : {}, {}", id, terrain);
        if (terrain.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, terrain.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!terrainRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Terrain> result = terrainRepository
            .findById(terrain.getId())
            .map(
                existingTerrain -> {
                    if (terrain.getEnergyForCommunity() != null) {
                        existingTerrain.setEnergyForCommunity(terrain.getEnergyForCommunity());
                    }
                    if (terrain.getReInversion() != null) {
                        existingTerrain.setReInversion(terrain.getReInversion());
                    }

                    return existingTerrain;
                }
            )
            .map(terrainRepository::save)
            .map(
                savedTerrain -> {
                    terrainSearchRepository.save(savedTerrain);

                    return savedTerrain;
                }
            );

        return ResponseUtil.wrapOrNotFound(result, HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, terrain.getId()));
    }

    /**
     * {@code GET  /terrains} : get all the terrains.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of terrains in body.
     */
    @GetMapping("/terrains")
    public ResponseEntity<List<Terrain>> getAllTerrains(Pageable pageable) {
        log.debug("REST request to get a page of Terrains");
        Page<Terrain> page = terrainRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /terrains/:id} : get the "id" terrain.
     *
     * @param id the id of the terrain to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the terrain, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/terrains/{id}")
    public ResponseEntity<Terrain> getTerrain(@PathVariable String id) {
        log.debug("REST request to get Terrain : {}", id);
        Optional<Terrain> terrain = terrainRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(terrain);
    }

    /**
     * {@code DELETE  /terrains/:id} : delete the "id" terrain.
     *
     * @param id the id of the terrain to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/terrains/{id}")
    public ResponseEntity<Void> deleteTerrain(@PathVariable String id) {
        log.debug("REST request to delete Terrain : {}", id);
        terrainRepository.deleteById(id);
        terrainSearchRepository.deleteById(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id)).build();
    }

    /**
     * {@code SEARCH  /_search/terrains?query=:query} : search for the terrain corresponding
     * to the query.
     *
     * @param query the query of the terrain search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/terrains")
    public ResponseEntity<List<Terrain>> searchTerrains(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of Terrains for query {}", query);
        Page<Terrain> page = terrainSearchRepository.search(queryStringQuery(query), pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
