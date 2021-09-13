package com.blocknitive.com.repository;

import com.blocknitive.com.domain.Providers;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Spring Data MongoDB repository for the Providers entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ProvidersRepository extends MongoRepository<Providers, String> {}
