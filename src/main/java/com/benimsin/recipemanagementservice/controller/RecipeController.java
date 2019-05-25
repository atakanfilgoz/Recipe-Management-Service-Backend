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

    //update eklenecek. putMapping datei unutma

    @GetMapping("/getRecipe/{tag}")
    public ResponseEntity<String> getRecipeByTag(@PathVariable String tag){
        Recipe result = recipeRepo.findByTags(tag);
        if (result == null){
            return new ResponseEntity<>("", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>("", HttpStatus.OK);

    }

    @DeleteMapping("/deleteRecipe/{id}")
    public ResponseEntity<String> deleteRecipeById(@PathVariable String id){
        Recipe temp = recipeRepo.findBy_id(id);
        long result = recipeRepo.deleteBy_id(id);
        if (temp == null){
            return new ResponseEntity<>("", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>("", HttpStatus.OK);
    }

}
