package com.benimsin.recipemanagementservice.controller;

import com.benimsin.recipemanagementservice.model.Recipe;
import com.benimsin.recipemanagementservice.repository.RecipeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
public class RecipeController {

    @Autowired
    RecipeRepository recipeRepo;

    @PostMapping("/addRecipe")
    public ResponseEntity<String> addRecipe(@RequestBody Recipe recipe){
        Recipe tempRecipe = recipeRepo.save(recipe);
        if (tempRecipe == null){
            return new ResponseEntity<>("", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>("", HttpStatus.OK);
    }

    @GetMapping("/getRecipes")
    public ResponseEntity<List> getRecipes(){
        List<Recipe> recipeList = recipeRepo.findAll();
        if (recipeList == null){
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(recipeList, HttpStatus.OK);
    }

    @GetMapping("/getRecipe/{tag}")
    public Recipe getRecipeByTag(@PathVariable String tag){
        Recipe result = recipeRepo.findByTags(tag);
        if (result == null){
            return null;
        }
        return result;
        /*if (result == null){
            return new ResponseEntity<>("", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>("", HttpStatus.OK);*/
    }

    @PutMapping("/updateRecipe/{id}")
    public ResponseEntity<String> updateRecipe(@PathVariable (value = "id") String _id, @RequestBody Recipe recipeRequest){
        if (!recipeRepo.existsBy_id(_id)){
            return new ResponseEntity<>("", HttpStatus.NOT_FOUND);
        }
        Recipe temp = recipeRepo.findBy_id(_id);
        temp.setName(recipeRequest.getName());
        temp.setDetails(recipeRequest.getDetails());
        temp.setUpdatedDate(new Date());
        temp.setPhotos(recipeRequest.getPhotos());
        temp.setTags(recipeRequest.getTags());
        recipeRepo.save(temp);
        return new ResponseEntity<>("", HttpStatus.OK);
    }

}
