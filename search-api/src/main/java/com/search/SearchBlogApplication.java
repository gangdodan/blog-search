package com.search;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableCaching
@EnableAspectJAutoProxy
@SpringBootApplication
//@ComponentScan(basePackages = {"com.search"})
@EnableScheduling
public class SearchBlogApplication {
    public static void main(String[] args) {
        SpringApplication.run(SearchBlogApplication.class, args);
    }
}