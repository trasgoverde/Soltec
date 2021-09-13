package com.blocknitive.com.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.blocknitive.com.domain.Logistics;
import com.blocknitive.com.repository.LogisticsRepository;
import com.blocknitive.com.repository.search.LogisticsSearchRepository;
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
 * REST controller for managing {@link com.blocknitive.com.domain.Logistics}.
 */
@RestController
@RequestMapping("/api")
public class LogisticsResource {

    private final Logger log = LoggerFactory.getLogger(LogisticsResource.class);

    private static final String ENTITY_NAME = "logistics";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final LogisticsRepository logisticsRepository;

    private final LogisticsSearchRepository logisticsSearchRepository;

    public LogisticsResource(LogisticsRepository logisticsRepository, LogisticsSearchRepository logisticsSearchRepository) {
        this.logisticsRepository = logisticsRepository;
        this.logisticsSearchRepository = logisticsSearchRepository;
    }

    /**
     * {@code POST  /logistics} : Create a new logistics.
     *
     * @param logistics the logistics to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new logistics, or with status {@code 400 (Bad Request)} if the logistics has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/logistics")
    public ResponseEntity<Logistics> createLogistics(@RequestBody Logistics logistics) throws URISyntaxException {
        log.debug("REST request to save Logistics : {}", logistics);
        if (logistics.getId() != null) {
            throw new BadRequestAlertException("A new logistics cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Logistics result = logisticsRepository.save(logistics);
        logisticsSearchRepository.save(result);
        return ResponseEntity
            .created(new URI("/api/logistics/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId()))
            .body(result);
    }

    /**
     * {@code PUT  /logistics/:id} : Updates an existing logistics.
     *
     * @param id the id of the logistics to save.
     * @param logistics the logistics to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated logistics,
     * or with status {@code 400 (Bad Request)} if the logistics is not valid,
     * or with status {@code 500 (Internal Server Error)} if the logistics couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/logistics/{id}")
    public ResponseEntity<Logistics> updateLogistics(
        @PathVariable(value = "id", required = false) final String id,
        @RequestBody Logistics logistics
    ) throws URISyntaxException {
        log.debug("REST request to update Logistics : {}, {}", id, logistics);
        if (logistics.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, logistics.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!logisticsRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Logistics result = logisticsRepository.save(logistics);
        logisticsSearchRepository.save(result);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, logistics.getId()))
            .body(result);
    }

    /**
     * {@code PATCH  /logistics/:id} : Partial updates given fields of an existing logistics, field will ignore if it is null
     *
     * @param id the id of the logistics to save.
     * @param logistics the logistics to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated logistics,
     * or with status {@code 400 (Bad Request)} if the logistics is not valid,
     * or with status {@code 404 (Not Found)} if the logistics is not found,
     * or with status {@code 500 (Internal Server Error)} if the logistics couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/logistics/{id}", consumes = "application/merge-patch+json")
    public ResponseEntity<Logistics> partialUpdateLogistics(
        @PathVariable(value = "id", required = false) final String id,
        @RequestBody Logistics logistics
    ) throws URISyntaxException {
        log.debug("REST request to partial update Logistics partially : {}, {}", id, logistics);
        if (logistics.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, logistics.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!logisticsRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Logistics> result = logisticsRepository
            .findById(logistics.getId())
            .map(
                existingLogistics -> {
                    if (logistics.getCo2Emitions() != null) {
                        existingLogistics.setCo2Emitions(logistics.getCo2Emitions());
                    }

                    return existingLogistics;
                }
            )
            .map(logisticsRepository::save)
            .map(
                savedLogistics -> {
                    logisticsSearchRepository.save(savedLogistics);

                    return savedLogistics;
                }
            );

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, logistics.getId())
        );
    }

    /**
     * {@code GET  /logistics} : get all the logistics.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of logistics in body.
     */
    @GetMapping("/logistics")
    public ResponseEntity<List<Logistics>> getAllLogistics(Pageable pageable) {
        log.debug("REST request to get a page of Logistics");
        Page<Logistics> page = logisticsRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /logistics/:id} : get the "id" logistics.
     *
     * @param id the id of the logistics to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the logistics, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/logistics/{id}")
    public ResponseEntity<Logistics> getLogistics(@PathVariable String id) {
        log.debug("REST request to get Logistics : {}", id);
        Optional<Logistics> logistics = logisticsRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(logistics);
    }

    /**
     * {@code DELETE  /logistics/:id} : delete the "id" logistics.
     *
     * @param id the id of the logistics to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/logistics/{id}")
    public ResponseEntity<Void> deleteLogistics(@PathVariable String id) {
        log.debug("REST request to delete Logistics : {}", id);
        logisticsRepository.deleteById(id);
        logisticsSearchRepository.deleteById(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id)).build();
    }

    /**
     * {@code SEARCH  /_search/logistics?query=:query} : search for the logistics corresponding
     * to the query.
     *
     * @param query the query of the logistics search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/logistics")
    public ResponseEntity<List<Logistics>> searchLogistics(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of Logistics for query {}", query);
        Page<Logistics> page = logisticsSearchRepository.search(queryStringQuery(query), pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
