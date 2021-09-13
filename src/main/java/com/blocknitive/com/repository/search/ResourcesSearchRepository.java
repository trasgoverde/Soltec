package com.blocknitive.com.repository.search;

import com.blocknitive.com.domain.Resources;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link Resources} entity.
 */
public interface ResourcesSearchRepository extends ElasticsearchRepository<Resources, String> {}
