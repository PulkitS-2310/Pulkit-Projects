package org;

import org.Users.UserRepository;
//import org.images.imageController;


import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


@EnableJpaRepositories(basePackages = {
        "org.Application",
        "org.images",
        "org.Search",
        "org.Users",
        "org.Chats",
        "org.websocket",
        "org.Activity",
        "org.Commenting",
        "org.Analytics",
        "org.Notifications"
})
@SpringBootApplication(exclude = {
        SecurityAutoConfiguration.class,
        SecurityFilterAutoConfiguration.class,
        UserDetailsServiceAutoConfiguration.class
})
@ComponentScan(basePackages = {
        "org.Application",
        "org.images",
        "org.Search",
        "org.Users",
        "org.Chats",
        "org.websocket",
        "org.Activity",
        "org.Commenting",
        "org.Analytics",
        "org.Notifications"
})
@EntityScan(basePackages = {
        "org.Application",
        "org.images",
        "org.Search",
        "org.Users",
        "org.Chats",
        "org.websocket",
        "org.Activity",
        "org.Commenting",
        "org.Analytics",
        "org.Notifications"
})
@Configuration
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
        //imageController image = new imageController();
    }

    @Bean
    CommandLineRunner initUser(UserRepository userRepository) {
        return args -> {

//    @Bean
//    CommandLineRunner initUser(UserRepository userRepository) {
//        return args ->
//        {

            /*
            HashCreator hashCreator = new HashCreator();

            //Adds 3 test users
            //hashes the passwords here because it usually does it during the post request and these dont do that
            User user1 = new User("John", "jdog357", "john@somemail.com", hashCreator.createSHAHash("password123"));
            User user2 = new User("George", "gman", "george@neopets.com", hashCreator.createSHAHash("123456"));
            User user3 = new User("Paul", "pmccartney", "pm@beatles.org", hashCreator.createSHAHash("idiedin1967"));
            userRepository.save(user1);
            userRepository.save(user2);
            userRepository.save(user3);
            */

        };

    }
}

