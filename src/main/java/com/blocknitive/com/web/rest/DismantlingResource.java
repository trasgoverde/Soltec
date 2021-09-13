package com.blocknitive.com.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.blocknitive.com.domain.Dismantling;
import com.blocknitive.com.repository.DismantlingRepository;
import com.blocknitive.com.repository.search.DismantlingSearchRepository;
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
 * REST controller for managing {@link com.blocknitive.com.domain.Dismantling}.
 */
@RestController
@RequestMapping("/api")
public class DismantlingResource {

    private final Logger log = LoggerFactory.getLogger(DismantlingResource.class);

    private static final String ENTITY_NAME = "dismantling";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final DismantlingRepository dismantlingRepository;

    private final DismantlingSearchRepository dismantlingSearchRepository;

    public DismantlingResource(DismantlingRepository dismantlingRepository, DismantlingSearchRepository dismantlingSearchRepository) {
        this.dismantlingRepository = dismantlingRepository;
        this.dismantlingSearchRepository = dismantlingSearchRepository;
    }

    /**
     * {@code POST  /dismantlings} : Create a new dismantling.
     *
     * @param dismantling the dismantling to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new dismantling, or with status {@code 400 (Bad Request)} if the dismantling has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/dismantlings")
    public ResponseEntity<Dismantling> createDismantling(@RequestBody Dismantling dismantling) throws URISyntaxException {
        log.debug("REST request to save Dismantling : {}", dismantling);
        if (dismantling.getId() != null) {
            throw new BadRequestAlertException("A new dismantling cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Dismantling result = dismantlingRepository.save(dismantling);
        dismantlingSearchRepository.save(result);
        return ResponseEntity
            .created(new URI("/api/dismantlings/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId()))
            .body(result);
    }

    /**
     * {@code PUT  /dismantlings/:id} : Updates an existing dismantling.
     *
     * @param id the id of the dismantling to save.
     * @param dismantling the dismantling to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated dismantling,
     * or with status {@code 400 (Bad Request)} if the dismantling is not valid,
     * or with status {@code 500 (Internal Server Error)} if the dismantling couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/dismantlings/{id}")
    public ResponseEntity<Dismantling> updateDismantling(
        @PathVariable(value = "id", required = false) final String id,
        @RequestBody Dismantling dismantling
    ) throws URISyntaxException {
        log.debug("REST request to update Dismantling : {}, {}", id, dismantling);
        if (dismantling.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, dismantling.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!dismantlingRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Dismantling result = dismantlingRepository.save(dismantling);
        dismantlingSearchRepository.save(result);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, dismantling.getId()))
            .body(result);
    }

    /**
     * {@code PATCH  /dismantlings/:id} : Partial updates given fields of an existing dismantling, field will ignore if it is null
     *
     * @param id the id of the dismantling to save.
     * @param dismantling the dismantling to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated dismantling,
     * or with status {@code 400 (Bad Request)} if the dismantling is not valid,
     * or with status {@code 404 (Not Found)} if the dismantling is not found,
     * or with status {@code 500 (Internal Server Error)} if the dismantling couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/dismantlings/{id}", consumes = "application/merge-patch+json")
    public ResponseEntity<Dismantling> partialUpdateDismantling(
        @PathVariable(value = "id", required = false) final String id,
        @RequestBody Dismantling dismantling
    ) throws URISyntaxException {
        log.debug("REST request to partial update Dismantling partially : {}, {}", id, dismantling);
        if (dismantling.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, dismantling.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!dismantlingRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Dismantling> result = dismantlingRepository
            .findById(dismantling.getId())
            .map(
                existingDismantling -> {
                    if (dismantling.getGuaranteeDismantling() != null) {
                        existingDismantling.setGuaranteeDismantling(dismantling.getGuaranteeDismantling());
                    }

                    return existingDismantling;
                }
            )
            .map(dismantlingRepository::save)
            .map(
                savedDismantling -> {
                    dismantlingSearchRepository.save(savedDismantling);

                    return savedDismantling;
                }
            );

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, dismantling.getId())
        );
    }

    /**
     * {@code GET  /dismantlings} : get all the dismantlings.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of dismantlings in body.
     */
    @GetMapping("/dismantlings")
    public ResponseEntity<List<Dismantling>> getAllDismantlings(Pageable pageable) {
        log.debug("REST request to get a page of Dismantlings");
        Page<Dismantling> page = dismantlingRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /dismantlings/:id} : get the "id" dismantling.
     *
     * @param id the id of the dismantling to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the dismantling, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/dismantlings/{id}")
    public ResponseEntity<Dismantling> getDismantling(@PathVariable String id) {
        log.debug("REST request to get Dismantling : {}", id);
        Optional<Dismantling> dismantling = dismantlingRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(dismantling);
    }

    /**
     * {@code DELETE  /dismantlings/:id} : delete the "id" dismantling.
     *
     * @param id the id of the dismantling to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/dismantlings/{id}")
    public ResponseEntity<Void> deleteDismantling(@PathVariable String id) {
        log.debug("REST request to delete Dismantling : {}", id);
        dismantlingRepository.deleteById(id);
        dismantlingSearchRepository.deleteById(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id)).build();
    }

    /**
     * {@code SEARCH  /_search/dismantlings?query=:query} : search for the dismantling corresponding
     * to the query.
     *
     * @param query the query of the dismantling search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/dismantlings")
    public ResponseEntity<List<Dismantling>> searchDismantlings(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of Dismantlings for query {}", query);
        Page<Dismantling> page = dismantlingSearchRepository.search(queryStringQuery(query), pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
