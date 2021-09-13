package com.blocknitive.com.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.blocknitive.com.domain.Machinery;
import com.blocknitive.com.repository.MachineryRepository;
import com.blocknitive.com.repository.search.MachinerySearchRepository;
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
 * REST controller for managing {@link com.blocknitive.com.domain.Machinery}.
 */
@RestController
@RequestMapping("/api")
public class MachineryResource {

    private final Logger log = LoggerFactory.getLogger(MachineryResource.class);

    private static final String ENTITY_NAME = "machinery";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final MachineryRepository machineryRepository;

    private final MachinerySearchRepository machinerySearchRepository;

    public MachineryResource(MachineryRepository machineryRepository, MachinerySearchRepository machinerySearchRepository) {
        this.machineryRepository = machineryRepository;
        this.machinerySearchRepository = machinerySearchRepository;
    }

    /**
     * {@code POST  /machinery} : Create a new machinery.
     *
     * @param machinery the machinery to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new machinery, or with status {@code 400 (Bad Request)} if the machinery has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/machinery")
    public ResponseEntity<Machinery> createMachinery(@RequestBody Machinery machinery) throws URISyntaxException {
        log.debug("REST request to save Machinery : {}", machinery);
        if (machinery.getId() != null) {
            throw new BadRequestAlertException("A new machinery cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Machinery result = machineryRepository.save(machinery);
        machinerySearchRepository.save(result);
        return ResponseEntity
            .created(new URI("/api/machinery/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId()))
            .body(result);
    }

    /**
     * {@code PUT  /machinery/:id} : Updates an existing machinery.
     *
     * @param id the id of the machinery to save.
     * @param machinery the machinery to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated machinery,
     * or with status {@code 400 (Bad Request)} if the machinery is not valid,
     * or with status {@code 500 (Internal Server Error)} if the machinery couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/machinery/{id}")
    public ResponseEntity<Machinery> updateMachinery(
        @PathVariable(value = "id", required = false) final String id,
        @RequestBody Machinery machinery
    ) throws URISyntaxException {
        log.debug("REST request to update Machinery : {}, {}", id, machinery);
        if (machinery.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, machinery.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!machineryRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Machinery result = machineryRepository.save(machinery);
        machinerySearchRepository.save(result);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, machinery.getId()))
            .body(result);
    }

    /**
     * {@code PATCH  /machinery/:id} : Partial updates given fields of an existing machinery, field will ignore if it is null
     *
     * @param id the id of the machinery to save.
     * @param machinery the machinery to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated machinery,
     * or with status {@code 400 (Bad Request)} if the machinery is not valid,
     * or with status {@code 404 (Not Found)} if the machinery is not found,
     * or with status {@code 500 (Internal Server Error)} if the machinery couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/machinery/{id}", consumes = "application/merge-patch+json")
    public ResponseEntity<Machinery> partialUpdateMachinery(
        @PathVariable(value = "id", required = false) final String id,
        @RequestBody Machinery machinery
    ) throws URISyntaxException {
        log.debug("REST request to partial update Machinery partially : {}, {}", id, machinery);
        if (machinery.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, machinery.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!machineryRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Machinery> result = machineryRepository
            .findById(machinery.getId())
            .map(
                existingMachinery -> {
                    if (machinery.getPaymentCycle() != null) {
                        existingMachinery.setPaymentCycle(machinery.getPaymentCycle());
                    }

                    return existingMachinery;
                }
            )
            .map(machineryRepository::save)
            .map(
                savedMachinery -> {
                    machinerySearchRepository.save(savedMachinery);

                    return savedMachinery;
                }
            );

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, machinery.getId())
        );
    }

    /**
     * {@code GET  /machinery} : get all the machinery.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of machinery in body.
     */
    @GetMapping("/machinery")
    public ResponseEntity<List<Machinery>> getAllMachinery(Pageable pageable) {
        log.debug("REST request to get a page of Machinery");
        Page<Machinery> page = machineryRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /machinery/:id} : get the "id" machinery.
     *
     * @param id the id of the machinery to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the machinery, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/machinery/{id}")
    public ResponseEntity<Machinery> getMachinery(@PathVariable String id) {
        log.debug("REST request to get Machinery : {}", id);
        Optional<Machinery> machinery = machineryRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(machinery);
    }

    /**
     * {@code DELETE  /machinery/:id} : delete the "id" machinery.
     *
     * @param id the id of the machinery to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/machinery/{id}")
    public ResponseEntity<Void> deleteMachinery(@PathVariable String id) {
        log.debug("REST request to delete Machinery : {}", id);
        machineryRepository.deleteById(id);
        machinerySearchRepository.deleteById(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id)).build();
    }

    /**
     * {@code SEARCH  /_search/machinery?query=:query} : search for the machinery corresponding
     * to the query.
     *
     * @param query the query of the machinery search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/machinery")
    public ResponseEntity<List<Machinery>> searchMachinery(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of Machinery for query {}", query);
        Page<Machinery> page = machinerySearchRepository.search(queryStringQuery(query), pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
