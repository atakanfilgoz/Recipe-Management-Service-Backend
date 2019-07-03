package com.benimsin.recipemanagementservice.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

@Document(collection = "photos")
public class Photo implements Serializable {

    @Id
    private String id;
    private String photoLink;
    private String publicCloudinaryId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPhotoLink() {
        return photoLink;
    }

    public void setPhotoLink(String photoLink) {
        this.photoLink = photoLink;
    }

    public String getPublicCloudinaryId() {
        return publicCloudinaryId;
    }

    public void setPublicCloudinaryId(String publicCloudinaryId) {
        this.publicCloudinaryId = publicCloudinaryId;
    }
}
