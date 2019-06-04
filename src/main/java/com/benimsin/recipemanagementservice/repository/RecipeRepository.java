package com.benimsin.recipemanagementservice.repository;

import com.benimsin.recipemanagementservice.model.Recipe;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RecipeRepository extends MongoRepository<Recipe,String> {
    Recipe findByTags(String tag);
    long deleteBy_id(String id);
    Recipe findBy_id(String id);
    //boolean existsBy_id(String id);
    //Recipe findByTagsContainingOOrDetails(String tag, String details);
}
