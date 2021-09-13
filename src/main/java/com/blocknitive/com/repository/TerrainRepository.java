package com.blocknitive.com.repository;

import com.blocknitive.com.domain.Terrain;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Spring Data MongoDB repository for the Terrain entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TerrainRepository extends MongoRepository<Terrain, String> {}
