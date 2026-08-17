package org.images;

import jakarta.persistence.*;
import org.json.JSONObject;

@Entity
@Table(name = "image")
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    @Column(name = "image_name")
    private String imageName;
    
    @Column(name = "image_size", nullable = false)
    private int imageSize = 0;
    
    @Column(name = "upload_time")
    private String uploadTime;

    private String filePath;

    // Default constructor for JPA
    public Image() {
        this.imageSize = 0;
    }

    public Image(int id, String imageName, int imageSize, String uploadTime) {
        this.id = 0;
        this.imageName = imageName;
        this.imageSize = imageSize;
        this.uploadTime = uploadTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public int getImageSize() {
        return imageSize;
    }

    public void setImageSize(int imageSize) {
        this.imageSize = imageSize;
    }

    public String getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(String uploadTime) {
        this.uploadTime = uploadTime;
    }

    public void setFilePath(String filePath) { this.filePath = filePath; }

    public String getFilePath() { return filePath; }

    @Override
    public String toString() {
        JSONObject jo = new JSONObject();
        jo.put("id", id);
        jo.put("imageName", imageName);
        jo.put("filePath", filePath);
        jo.put("imageSize", imageSize);
        jo.put("uploadTime", uploadTime);

        return jo.toString();
    }
}
