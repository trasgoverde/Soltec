package com.blocknitive.com.repository.search;

import com.blocknitive.com.domain.Dismantling;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link Dismantling} entity.
 */
public interface DismantlingSearchRepository extends ElasticsearchRepository<Dismantling, String> {}
