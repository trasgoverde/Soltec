package com.blocknitive.com.repository;

import com.blocknitive.com.domain.Teams;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Spring Data MongoDB repository for the Teams entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TeamsRepository extends MongoRepository<Teams, String> {}
