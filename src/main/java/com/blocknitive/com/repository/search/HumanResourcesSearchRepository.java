package com.blocknitive.com.repository.search;

import com.blocknitive.com.domain.HumanResources;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link HumanResources} entity.
 */
public interface HumanResourcesSearchRepository extends ElasticsearchRepository<HumanResources, String> {}
