package com.benimsin.recipemanagementservice.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.benimsin.recipemanagementservice.model.User;

public interface UserRepository extends MongoRepository<User, String> {

    User findByUsername(String username);
}
