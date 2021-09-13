package com.blocknitive.com.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.blocknitive.com.domain.Providers;
import com.blocknitive.com.repository.ProvidersRepository;
import com.blocknitive.com.repository.search.ProvidersSearchRepository;
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
 * REST controller for managing {@link com.blocknitive.com.domain.Providers}.
 */
@RestController
@RequestMapping("/api")
public class ProvidersResource {

    private final Logger log = LoggerFactory.getLogger(ProvidersResource.class);

    private static final String ENTITY_NAME = "providers";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ProvidersRepository providersRepository;

    private final ProvidersSearchRepository providersSearchRepository;

    public ProvidersResource(ProvidersRepository providersRepository, ProvidersSearchRepository providersSearchRepository) {
        this.providersRepository = providersRepository;
        this.providersSearchRepository = providersSearchRepository;
    }

    /**
     * {@code POST  /providers} : Create a new providers.
     *
     * @param providers the providers to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new providers, or with status {@code 400 (Bad Request)} if the providers has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/providers")
    public ResponseEntity<Providers> createProviders(@RequestBody Providers providers) throws URISyntaxException {
        log.debug("REST request to save Providers : {}", providers);
        if (providers.getId() != null) {
            throw new BadRequestAlertException("A new providers cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Providers result = providersRepository.save(providers);
        providersSearchRepository.save(result);
        return ResponseEntity
            .created(new URI("/api/providers/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId()))
            .body(result);
    }

    /**
     * {@code PUT  /providers/:id} : Updates an existing providers.
     *
     * @param id the id of the providers to save.
     * @param providers the providers to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated providers,
     * or with status {@code 400 (Bad Request)} if the providers is not valid,
     * or with status {@code 500 (Internal Server Error)} if the providers couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/providers/{id}")
    public ResponseEntity<Providers> updateProviders(
        @PathVariable(value = "id", required = false) final String id,
        @RequestBody Providers providers
    ) throws URISyntaxException {
        log.debug("REST request to update Providers : {}, {}", id, providers);
        if (providers.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, providers.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!providersRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Providers result = providersRepository.save(providers);
        providersSearchRepository.save(result);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, providers.getId()))
            .body(result);
    }

    /**
     * {@code PATCH  /providers/:id} : Partial updates given fields of an existing providers, field will ignore if it is null
     *
     * @param id the id of the providers to save.
     * @param providers the providers to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated providers,
     * or with status {@code 400 (Bad Request)} if the providers is not valid,
     * or with status {@code 404 (Not Found)} if the providers is not found,
     * or with status {@code 500 (Internal Server Error)} if the providers couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/providers/{id}", consumes = "application/merge-patch+json")
    public ResponseEntity<Providers> partialUpdateProviders(
        @PathVariable(value = "id", required = false) final String id,
        @RequestBody Providers providers
    ) throws URISyntaxException {
        log.debug("REST request to partial update Providers partially : {}, {}", id, providers);
        if (providers.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, providers.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!providersRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Providers> result = providersRepository
            .findById(providers.getId())
            .map(
                existingProviders -> {
                    if (providers.getAgreementParis() != null) {
                        existingProviders.setAgreementParis(providers.getAgreementParis());
                    }
                    if (providers.getCertifiedSustianable() != null) {
                        existingProviders.setCertifiedSustianable(providers.getCertifiedSustianable());
                    }

                    return existingProviders;
                }
            )
            .map(providersRepository::save)
            .map(
                savedProviders -> {
                    providersSearchRepository.save(savedProviders);

                    return savedProviders;
                }
            );

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, providers.getId())
        );
    }

    /**
     * {@code GET  /providers} : get all the providers.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of providers in body.
     */
    @GetMapping("/providers")
    public ResponseEntity<List<Providers>> getAllProviders(Pageable pageable) {
        log.debug("REST request to get a page of Providers");
        Page<Providers> page = providersRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /providers/:id} : get the "id" providers.
     *
     * @param id the id of the providers to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the providers, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/providers/{id}")
    public ResponseEntity<Providers> getProviders(@PathVariable String id) {
        log.debug("REST request to get Providers : {}", id);
        Optional<Providers> providers = providersRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(providers);
    }

    /**
     * {@code DELETE  /providers/:id} : delete the "id" providers.
     *
     * @param id the id of the providers to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/providers/{id}")
    public ResponseEntity<Void> deleteProviders(@PathVariable String id) {
        log.debug("REST request to delete Providers : {}", id);
        providersRepository.deleteById(id);
        providersSearchRepository.deleteById(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id)).build();
    }

    /**
     * {@code SEARCH  /_search/providers?query=:query} : search for the providers corresponding
     * to the query.
     *
     * @param query the query of the providers search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/providers")
    public ResponseEntity<List<Providers>> searchProviders(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of Providers for query {}", query);
        Page<Providers> page = providersSearchRepository.search(queryStringQuery(query), pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
