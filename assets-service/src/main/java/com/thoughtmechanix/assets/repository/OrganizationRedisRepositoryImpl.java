package com.thoughtmechanix.assets.repository;

import com.thoughtmechanix.assets.model.Organization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;

/**
 * This class uses the RedisTemplate Spring bean you declared earlier in Application.class 
 * to interact with the Redis server and carry out actions against the Redis server.
 * This class contains all the CRUD (Create, Read, Update, Delete) logic used for storing 
 * and retrieving data from Redis. 
   There are two key things to note from this class:
   1. All data in Redis is stored and retrieved by a key. Because you’re storing data
	  retrieved from the organization service, organization ID is the natural choice
	  for the key being used to store an organization record.
   2. The Redis server can contain multiple hashes and data structures within it. 
      In every operation against the Redis server, you need to tell Redis the name 
      of the data structure you’re performing the operation against. 
      In code below, the data structure name you’re using is stored in the HASH_NAME constant 
      and is called “organization.”

 *
 */
@Repository
public class OrganizationRedisRepositoryImpl implements OrganizationRedisRepository {
    private static final String HASH_NAME ="organization";

    private RedisTemplate<String, Organization> redisTemplate;
    private HashOperations hashOperations;

    public OrganizationRedisRepositoryImpl(){
        super();
    }

    @Autowired
    private OrganizationRedisRepositoryImpl(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @PostConstruct
    private void init() {
        hashOperations = redisTemplate.opsForHash();
    }


    @Override
    public void saveOrganization(Organization org) {
        hashOperations.put(HASH_NAME, org.getId(), org);
    }

    @Override
    public void updateOrganization(Organization org) {
        hashOperations.put(HASH_NAME, org.getId(), org);
    }

    @Override
    public void deleteOrganization(String organizationId) {
        hashOperations.delete(HASH_NAME, organizationId);
    }

    @Override
    public Organization findOrganization(String organizationId) {
       return (Organization) hashOperations.get(HASH_NAME, organizationId);
    }
}
