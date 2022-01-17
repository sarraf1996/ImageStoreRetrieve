package com.example.imagestoreretrieve;

public class UploadedImages {
    String imageName;
    String imageUrl;

    public UploadedImages() {
    }

    public UploadedImages(String imageName2, String imageUrl2) {
        this.imageName = imageName2;
        this.imageUrl = imageUrl2;
    }

    public String getImageName() {
        return this.imageName;
    }

    public void setImageName(String imageName2) {
        this.imageName = imageName2;
    }

    public String getImageUrl() {
        return this.imageUrl;
    }

    public void setImageUrl(String imageUrl2) {
        this.imageUrl = imageUrl2;
    }
}
