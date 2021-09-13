package com.blocknitive.com.repository.search;

import com.blocknitive.com.domain.Providers;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link Providers} entity.
 */
public interface ProvidersSearchRepository extends ElasticsearchRepository<Providers, String> {}
