package org.images;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Integer> {
    Image findById(int id);
    void deleteById(int id);
    void deleteAll();
}
