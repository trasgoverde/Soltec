package com.blocknitive.com.repository.search;

import com.blocknitive.com.domain.Terrain;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link Terrain} entity.
 */
public interface TerrainSearchRepository extends ElasticsearchRepository<Terrain, String> {}
