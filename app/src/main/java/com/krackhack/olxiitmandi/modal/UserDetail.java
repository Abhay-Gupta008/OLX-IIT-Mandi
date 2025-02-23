package com.krackhack.olxiitmandi.modal;

public class UserDetail {
    private String userId;
    private String name;
    private String email;
    private String profileImage;

    // Empty constructor (required for Firebase)
    public UserDetail() {
    }

    // Constructor
    public UserDetail(String userId, String name, String email, String profileImage) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.profileImage = profileImage;
    }

    // Getters and Setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }
}
