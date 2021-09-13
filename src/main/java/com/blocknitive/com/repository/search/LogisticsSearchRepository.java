package com.blocknitive.com.repository.search;

import com.blocknitive.com.domain.Logistics;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link Logistics} entity.
 */
public interface LogisticsSearchRepository extends ElasticsearchRepository<Logistics, String> {}
