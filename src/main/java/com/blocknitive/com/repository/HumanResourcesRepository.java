package com.blocknitive.com.repository;

import com.blocknitive.com.domain.HumanResources;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Spring Data MongoDB repository for the HumanResources entity.
 */
@SuppressWarnings("unused")
@Repository
public interface HumanResourcesRepository extends MongoRepository<HumanResources, String> {}
