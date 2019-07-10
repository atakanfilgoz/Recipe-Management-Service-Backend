package com.benimsin.recipemanagementservice.controller;

import com.benimsin.recipemanagementservice.model.Photo;
import com.benimsin.recipemanagementservice.model.Recipe;
import com.benimsin.recipemanagementservice.model.User;
import com.benimsin.recipemanagementservice.repository.RecipeRepository;
import com.benimsin.recipemanagementservice.repository.UserRepository;
import com.benimsin.recipemanagementservice.service.CloudinaryService;
import com.benimsin.recipemanagementservice.service.FCMService;
import com.benimsin.recipemanagementservice.util.PushNotificationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
public class RecipeController {

    @Autowired
    RecipeRepository recipeRepo;

    @Autowired
    UserRepository userRepository;

    @Autowired
    FCMService fcmService;

    @Autowired
    private CloudinaryService cloudinaryService;

    @PostMapping("/addRecipe")
    public ResponseEntity<String> addRecipe(@RequestParam("name") String name,
                                            @RequestParam("details") String details,
                                            @RequestParam("tags") ArrayList<String> tags,
                                            @RequestPart("file") @Valid List<MultipartFile> files,
                                            Authentication authentication){
        Recipe tempRecipe = new Recipe();
        tempRecipe.setName(name);
        tempRecipe.setDetails(details);
        tempRecipe.setTags(tags);
        tempRecipe.setUserName(authentication.getName());
        if (files == null){
            return null;
        }
        ArrayList<Photo> photos = new ArrayList<>();
        for (MultipartFile mFile : files){
            Map <String,String> photoInfo = cloudinaryService.uploadFile(mFile);
            Photo photo = new Photo();
            photo.setPhotoLink(photoInfo.get("url"));
            photo.setPublicCloudinaryId(photoInfo.get("publicId"));
            photos.add(photo);
        }
        tempRecipe.setPhotos(photos);
        Recipe result = recipeRepo.save(tempRecipe);
        if (result == null){
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

    @GetMapping("/getMyRecipes")
    public ResponseEntity<List> getMyRecipes(Authentication authentication){
        String userName = authentication.getName();
        List<Recipe> recipeList = recipeRepo.findByUserName(userName);
        if (recipeList == null){
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(recipeList, HttpStatus.OK);
    }

    @GetMapping("/getRecipe/{id}")
    public Recipe getRecipeByTag(@PathVariable (value = "id") String _id){
        Recipe result = recipeRepo.findBy_id(_id);
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
    public ResponseEntity<String> updateRecipe(@PathVariable (value = "id") String _id, @RequestBody Recipe recipeRequest, Authentication authentication){
        if (!recipeRepo.existsBy_id(_id)){
            return new ResponseEntity<>("", HttpStatus.NOT_FOUND);
        }
        Recipe temp = recipeRepo.findBy_id(_id);
        if (!authentication.getName().equals(temp.getUserName())){
            return new ResponseEntity<>("", HttpStatus.UNAUTHORIZED);
        }
        temp.setName(recipeRequest.getName());
        temp.setDetails(recipeRequest.getDetails());
        temp.setUpdatedDate(new Date());
        temp.setPhotos(recipeRequest.getPhotos());
        temp.setTags(recipeRequest.getTags());
        recipeRepo.save(temp);
        return new ResponseEntity<>("", HttpStatus.OK);
    }

    @GetMapping("/searchRecipes/{keyword}")
    public List<Recipe> searchRecipe(@PathVariable (value = "keyword")  String keyword){
        ArrayList<Recipe> recipes = new ArrayList<>();
        ArrayList<String> ids = new ArrayList<>();
        boolean isDup = false;

        if (recipeRepo.findByTagsContaining(keyword) != null){
            recipes.addAll(recipeRepo.findByTagsContaining(keyword));
        }

        for (Recipe recipe : recipes){
            ids.add(recipe.getId());
        }

        if (recipeRepo.findByDetailsContaining(keyword) != null){
            List<Recipe> containing = recipeRepo.findByDetailsContaining(keyword);
            for (Recipe recipe : containing){
                for (String id : ids) {
                    if (id.equals(recipe.getId())){
                        isDup = true;
                    }
                }
                if (!isDup){
                    recipes.add(recipe);
                    isDup = false;
                }
            }
        }
        return recipes;
    }

    @DeleteMapping("/deleteRecipe/{id}")
    public ResponseEntity<String> deleteRecipeById(@PathVariable String id, Authentication authentication){
        Recipe temp = recipeRepo.findBy_id(id);
        if (!authentication.getName().equals(temp.getUserName())){
            return new ResponseEntity<>("", HttpStatus.UNAUTHORIZED);
        }
        long result = recipeRepo.deleteBy_id(id);
        for (Photo photo : temp.getPhotos()){
            if (photo != null){
                cloudinaryService.deleteFile(photo.getPublicCloudinaryId());
            }
        }
        if (temp == null){
            return new ResponseEntity<>("", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>("", HttpStatus.OK);
    }

    @DeleteMapping("/deleteAllRecipes")
    public void deleteAllRecipes(){
        recipeRepo.deleteAll();
    }

    @GetMapping("/liked/{id}")
    public void liked(@PathVariable String id){
        Recipe temp = recipeRepo.findBy_id(id);
        User user = userRepository.findByUsername(temp.getUserName());
        PushNotificationRequest pnRequest = new PushNotificationRequest();
        pnRequest.setMessage("Your recipe : " + temp.getName() + " is liked.");
        pnRequest.setTitle("Likes");
        pnRequest.setToken(user.getDeviceToken());
        pnRequest.setTopic("");
        try{
            fcmService.sendMessageToToken(pnRequest);
        }
        catch (Exception e){
            return;
        }
    }

}
