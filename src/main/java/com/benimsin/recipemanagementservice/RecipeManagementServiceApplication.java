package com.benimsin.recipemanagementservice;

import com.benimsin.recipemanagementservice.model.Recipe;
import com.benimsin.recipemanagementservice.model.User;
import com.benimsin.recipemanagementservice.repository.RecipeRepository;
import com.benimsin.recipemanagementservice.repository.UserRepository;
import com.cloudinary.Cloudinary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
@EnableScheduling
@ComponentScan(basePackages = { "com.benimsin.recipemanagementservice.*" })
public class RecipeManagementServiceApplication {
    @Value("${cloudinary.cloud_name}")
    private String cloudName;

    @Value("${cloudinary.api_key}")
    private String apiKey;

    @Value("${cloudinary.api_secret}")
    private String apiSecret;

    @Autowired
    RecipeRepository recipeRepository;

    @Autowired
    UserRepository userRepository;

    public static void main(String[] args) {
        SpringApplication.run(RecipeManagementServiceApplication.class, args);
    }

    @Bean
    public Cloudinary cloudinaryConfig() {
        Map config = new HashMap();
        config.put("cloud_name", cloudName);
        config.put("api_key", apiKey);
        config.put("api_secret", apiSecret);
        Cloudinary cloudinary = new Cloudinary(config);
        return cloudinary;
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Scheduled(cron = "0 0 12 * * ?")
    public void reportCurrentTime() {
        long timeDifference;
        for (User user : userRepository.findAll()){
            for (Recipe recipe : recipeRepository.findByUserName(user.getUsername())){
                if (recipe.getUpdatedDate() != null){
                    timeDifference = getDateDiff(new Date(),recipe.getUpdatedDate(),TimeUnit.DAYS);
                }
                else{
                    timeDifference = getDateDiff(new Date(),recipe.getCreatedDate(),TimeUnit.DAYS);
                }
                if (timeDifference < 0){
                    recipeRepository.deleteBy_id(recipe.getId());
                }
            }
        }

    }

    public static long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
        long diffInMillies = date2.getTime() - date1.getTime();
        return timeUnit.convert(diffInMillies,TimeUnit.MILLISECONDS);
    }

}
