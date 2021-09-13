package com.blocknitive.com.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.blocknitive.com.domain.HumanResources;
import com.blocknitive.com.repository.HumanResourcesRepository;
import com.blocknitive.com.repository.search.HumanResourcesSearchRepository;
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
 * REST controller for managing {@link com.blocknitive.com.domain.HumanResources}.
 */
@RestController
@RequestMapping("/api")
public class HumanResourcesResource {

    private final Logger log = LoggerFactory.getLogger(HumanResourcesResource.class);

    private static final String ENTITY_NAME = "humanResources";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final HumanResourcesRepository humanResourcesRepository;

    private final HumanResourcesSearchRepository humanResourcesSearchRepository;

    public HumanResourcesResource(
        HumanResourcesRepository humanResourcesRepository,
        HumanResourcesSearchRepository humanResourcesSearchRepository
    ) {
        this.humanResourcesRepository = humanResourcesRepository;
        this.humanResourcesSearchRepository = humanResourcesSearchRepository;
    }

    /**
     * {@code POST  /human-resources} : Create a new humanResources.
     *
     * @param humanResources the humanResources to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new humanResources, or with status {@code 400 (Bad Request)} if the humanResources has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/human-resources")
    public ResponseEntity<HumanResources> createHumanResources(@RequestBody HumanResources humanResources) throws URISyntaxException {
        log.debug("REST request to save HumanResources : {}", humanResources);
        if (humanResources.getId() != null) {
            throw new BadRequestAlertException("A new humanResources cannot already have an ID", ENTITY_NAME, "idexists");
        }
        HumanResources result = humanResourcesRepository.save(humanResources);
        humanResourcesSearchRepository.save(result);
        return ResponseEntity
            .created(new URI("/api/human-resources/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId()))
            .body(result);
    }

    /**
     * {@code PUT  /human-resources/:id} : Updates an existing humanResources.
     *
     * @param id the id of the humanResources to save.
     * @param humanResources the humanResources to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated humanResources,
     * or with status {@code 400 (Bad Request)} if the humanResources is not valid,
     * or with status {@code 500 (Internal Server Error)} if the humanResources couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/human-resources/{id}")
    public ResponseEntity<HumanResources> updateHumanResources(
        @PathVariable(value = "id", required = false) final String id,
        @RequestBody HumanResources humanResources
    ) throws URISyntaxException {
        log.debug("REST request to update HumanResources : {}, {}", id, humanResources);
        if (humanResources.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, humanResources.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!humanResourcesRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        HumanResources result = humanResourcesRepository.save(humanResources);
        humanResourcesSearchRepository.save(result);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, humanResources.getId()))
            .body(result);
    }

    /**
     * {@code PATCH  /human-resources/:id} : Partial updates given fields of an existing humanResources, field will ignore if it is null
     *
     * @param id the id of the humanResources to save.
     * @param humanResources the humanResources to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated humanResources,
     * or with status {@code 400 (Bad Request)} if the humanResources is not valid,
     * or with status {@code 404 (Not Found)} if the humanResources is not found,
     * or with status {@code 500 (Internal Server Error)} if the humanResources couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/human-resources/{id}", consumes = "application/merge-patch+json")
    public ResponseEntity<HumanResources> partialUpdateHumanResources(
        @PathVariable(value = "id", required = false) final String id,
        @RequestBody HumanResources humanResources
    ) throws URISyntaxException {
        log.debug("REST request to partial update HumanResources partially : {}, {}", id, humanResources);
        if (humanResources.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, humanResources.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!humanResourcesRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<HumanResources> result = humanResourcesRepository
            .findById(humanResources.getId())
            .map(
                existingHumanResources -> {
                    if (humanResources.getInvestmentsLocally() != null) {
                        existingHumanResources.setInvestmentsLocally(humanResources.getInvestmentsLocally());
                    }
                    if (humanResources.getLaborAccidentsindex() != null) {
                        existingHumanResources.setLaborAccidentsindex(humanResources.getLaborAccidentsindex());
                    }

                    return existingHumanResources;
                }
            )
            .map(humanResourcesRepository::save)
            .map(
                savedHumanResources -> {
                    humanResourcesSearchRepository.save(savedHumanResources);

                    return savedHumanResources;
                }
            );

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, humanResources.getId())
        );
    }

    /**
     * {@code GET  /human-resources} : get all the humanResources.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of humanResources in body.
     */
    @GetMapping("/human-resources")
    public ResponseEntity<List<HumanResources>> getAllHumanResources(Pageable pageable) {
        log.debug("REST request to get a page of HumanResources");
        Page<HumanResources> page = humanResourcesRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /human-resources/:id} : get the "id" humanResources.
     *
     * @param id the id of the humanResources to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the humanResources, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/human-resources/{id}")
    public ResponseEntity<HumanResources> getHumanResources(@PathVariable String id) {
        log.debug("REST request to get HumanResources : {}", id);
        Optional<HumanResources> humanResources = humanResourcesRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(humanResources);
    }

    /**
     * {@code DELETE  /human-resources/:id} : delete the "id" humanResources.
     *
     * @param id the id of the humanResources to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/human-resources/{id}")
    public ResponseEntity<Void> deleteHumanResources(@PathVariable String id) {
        log.debug("REST request to delete HumanResources : {}", id);
        humanResourcesRepository.deleteById(id);
        humanResourcesSearchRepository.deleteById(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id)).build();
    }

    /**
     * {@code SEARCH  /_search/human-resources?query=:query} : search for the humanResources corresponding
     * to the query.
     *
     * @param query the query of the humanResources search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/human-resources")
    public ResponseEntity<List<HumanResources>> searchHumanResources(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of HumanResources for query {}", query);
        Page<HumanResources> page = humanResourcesSearchRepository.search(queryStringQuery(query), pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
