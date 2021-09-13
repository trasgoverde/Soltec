package com.blocknitive.com.repository.search;

import com.blocknitive.com.domain.Teams;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link Teams} entity.
 */
public interface TeamsSearchRepository extends ElasticsearchRepository<Teams, String> {}
