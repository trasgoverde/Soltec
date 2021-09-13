package com.blocknitive.com.repository;

import com.blocknitive.com.domain.Logistics;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Spring Data MongoDB repository for the Logistics entity.
 */
@SuppressWarnings("unused")
@Repository
public interface LogisticsRepository extends MongoRepository<Logistics, String> {}