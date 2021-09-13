package com.blocknitive.com.repository.search;

import com.blocknitive.com.domain.Machinery;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link Machinery} entity.
 */
public interface MachinerySearchRepository extends ElasticsearchRepository<Machinery, String> {}
