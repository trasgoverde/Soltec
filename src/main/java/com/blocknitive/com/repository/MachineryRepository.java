package com.blocknitive.com.repository;

import com.blocknitive.com.domain.Machinery;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Spring Data MongoDB repository for the Machinery entity.
 */
@SuppressWarnings("unused")
@Repository
public interface MachineryRepository extends MongoRepository<Machinery, String> {}
