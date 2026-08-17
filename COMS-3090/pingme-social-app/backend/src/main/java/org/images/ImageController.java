package org.images;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/images")
@CrossOrigin(origins = "*")
public class ImageController {


    private static String directory = "/home/reinrj/images/";

    @Autowired
    private ImageRepository imageRepository;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadImage(@RequestParam("image") MultipartFile file) {
        try {
            // Create a new image entity
            Image newImage = new Image();
            TimeFormatter time = new TimeFormatter();
            newImage.setImageName(file.getOriginalFilename());
            newImage.setImageSize((int) file.getSize());
            newImage.setUploadTime(time.getFormatData());

            // Save the image metadata
            //Image savedImage = imageRepository.save(newImage);

            try {
                File destinationFile = new File(directory + File.separator + file.getOriginalFilename());
                file.transferTo(destinationFile);  // save file to disk


                newImage.setFilePath(destinationFile.getAbsolutePath());
                Image savedImage = imageRepository.save(newImage);

                return ResponseEntity.ok().body("File uploaded successfully: ID: " + Integer.toString(savedImage.getId()) + ", " + destinationFile.getAbsolutePath());
            } catch (IOException e) {
                return ResponseEntity.badRequest().body("Failed to upload file: " + e.getMessage());
            }

            
            //return ResponseEntity.ok().body(savedImage);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to upload image: " + e.getMessage());
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<Image>> getAllImages() {
        return ResponseEntity.ok(imageRepository.findAll());
    }


    @GetMapping(value = "/images/{id}", produces = MediaType.IMAGE_JPEG_VALUE)
    byte[] getImageById1(@PathVariable int id) throws IOException {
        Image image = imageRepository.findById(id);
        File imageFile = new File(image.getFilePath());
        return Files.readAllBytes(imageFile.toPath());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Image> getImageById(@PathVariable int id) {
        Image img = imageRepository.findById(id);
        return img != null ? ResponseEntity.ok(img) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteImage(@PathVariable int id) {
        try {
            imageRepository.deleteById(id);
            return ResponseEntity.ok("Image successfully deleted");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to delete image: " + e.getMessage());
        }
    }
/*
    @DeleteMapping("/all")
    public ResponseEntity<String> deleteAllImage() {
        try {
            imageRepository.deleteAll();
            return ResponseEntity.ok("All images are succesfully deleted");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Failed to delete all images: " + e.getMessage());
        }
    }
 */

}
