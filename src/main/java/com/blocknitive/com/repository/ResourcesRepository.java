package com.blocknitive.com.repository;

import com.blocknitive.com.domain.Resources;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Spring Data MongoDB repository for the Resources entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ResourcesRepository extends MongoRepository<Resources, String> {}
