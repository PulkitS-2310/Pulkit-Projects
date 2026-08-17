package org.Search;

import jakarta.persistence.*;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.Set;
import java.util.HashSet;

@Entity
public class Hashtag implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;
    private String tag;



    public Hashtag() {
        // Leave this blank
    }

    public Hashtag (String newTag)
    {
        if (newTag.startsWith("#"))
        {
            tag = newTag.substring(1);
        } else {
            tag = newTag;
        }
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    @Override
    public String toString() {
        return "#" + tag;
    }
}
