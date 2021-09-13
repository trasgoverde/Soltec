package com.blocknitive.com.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.blocknitive.com.domain.Resources;
import com.blocknitive.com.repository.ResourcesRepository;
import com.blocknitive.com.repository.search.ResourcesSearchRepository;
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
 * REST controller for managing {@link com.blocknitive.com.domain.Resources}.
 */
@RestController
@RequestMapping("/api")
public class ResourcesResource {

    private final Logger log = LoggerFactory.getLogger(ResourcesResource.class);

    private static final String ENTITY_NAME = "resources";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ResourcesRepository resourcesRepository;

    private final ResourcesSearchRepository resourcesSearchRepository;

    public ResourcesResource(ResourcesRepository resourcesRepository, ResourcesSearchRepository resourcesSearchRepository) {
        this.resourcesRepository = resourcesRepository;
        this.resourcesSearchRepository = resourcesSearchRepository;
    }

    /**
     * {@code POST  /resources} : Create a new resources.
     *
     * @param resources the resources to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new resources, or with status {@code 400 (Bad Request)} if the resources has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/resources")
    public ResponseEntity<Resources> createResources(@RequestBody Resources resources) throws URISyntaxException {
        log.debug("REST request to save Resources : {}", resources);
        if (resources.getId() != null) {
            throw new BadRequestAlertException("A new resources cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Resources result = resourcesRepository.save(resources);
        resourcesSearchRepository.save(result);
        return ResponseEntity
            .created(new URI("/api/resources/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId()))
            .body(result);
    }

    /**
     * {@code PUT  /resources/:id} : Updates an existing resources.
     *
     * @param id the id of the resources to save.
     * @param resources the resources to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated resources,
     * or with status {@code 400 (Bad Request)} if the resources is not valid,
     * or with status {@code 500 (Internal Server Error)} if the resources couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/resources/{id}")
    public ResponseEntity<Resources> updateResources(
        @PathVariable(value = "id", required = false) final String id,
        @RequestBody Resources resources
    ) throws URISyntaxException {
        log.debug("REST request to update Resources : {}, {}", id, resources);
        if (resources.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, resources.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!resourcesRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Resources result = resourcesRepository.save(resources);
        resourcesSearchRepository.save(result);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, resources.getId()))
            .body(result);
    }

    /**
     * {@code PATCH  /resources/:id} : Partial updates given fields of an existing resources, field will ignore if it is null
     *
     * @param id the id of the resources to save.
     * @param resources the resources to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated resources,
     * or with status {@code 400 (Bad Request)} if the resources is not valid,
     * or with status {@code 404 (Not Found)} if the resources is not found,
     * or with status {@code 500 (Internal Server Error)} if the resources couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/resources/{id}", consumes = "application/merge-patch+json")
    public ResponseEntity<Resources> partialUpdateResources(
        @PathVariable(value = "id", required = false) final String id,
        @RequestBody Resources resources
    ) throws URISyntaxException {
        log.debug("REST request to partial update Resources partially : {}, {}", id, resources);
        if (resources.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, resources.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!resourcesRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Resources> result = resourcesRepository
            .findById(resources.getId())
            .map(
                existingResources -> {
                    if (resources.getWaterConsumtion() != null) {
                        existingResources.setWaterConsumtion(resources.getWaterConsumtion());
                    }
                    if (resources.getReforestryIndex() != null) {
                        existingResources.setReforestryIndex(resources.getReforestryIndex());
                    }

                    return existingResources;
                }
            )
            .map(resourcesRepository::save)
            .map(
                savedResources -> {
                    resourcesSearchRepository.save(savedResources);

                    return savedResources;
                }
            );

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, resources.getId())
        );
    }

    /**
     * {@code GET  /resources} : get all the resources.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of resources in body.
     */
    @GetMapping("/resources")
    public ResponseEntity<List<Resources>> getAllResources(Pageable pageable) {
        log.debug("REST request to get a page of Resources");
        Page<Resources> page = resourcesRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /resources/:id} : get the "id" resources.
     *
     * @param id the id of the resources to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the resources, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/resources/{id}")
    public ResponseEntity<Resources> getResources(@PathVariable String id) {
        log.debug("REST request to get Resources : {}", id);
        Optional<Resources> resources = resourcesRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(resources);
    }

    /**
     * {@code DELETE  /resources/:id} : delete the "id" resources.
     *
     * @param id the id of the resources to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/resources/{id}")
    public ResponseEntity<Void> deleteResources(@PathVariable String id) {
        log.debug("REST request to delete Resources : {}", id);
        resourcesRepository.deleteById(id);
        resourcesSearchRepository.deleteById(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id)).build();
    }

    /**
     * {@code SEARCH  /_search/resources?query=:query} : search for the resources corresponding
     * to the query.
     *
     * @param query the query of the resources search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/resources")
    public ResponseEntity<List<Resources>> searchResources(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of Resources for query {}", query);
        Page<Resources> page = resourcesSearchRepository.search(queryStringQuery(query), pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
