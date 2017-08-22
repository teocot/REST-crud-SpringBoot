package com.teo.crud;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.guava.GuavaCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.google.common.cache.CacheBuilder;

/**
 * 
 * This application provides CRUD (create, read, update, delete)
 * RESTfull services for some customer data
 * Each customer has a 
 * - name
 * - address
 * - telephone number
 * 
 * In order to run the application, one can use maven and execute:
 * mvn clean
 * mvn spring-boot:run
 * 
 * This will start the application on localhost:8080
 * For further information on the URLs exposed, please read the readme file
 * 
 * @author teodor cotruta, 05/09/2016
 *
 * Spring Boot main application class. Serves as both the runtime application
 * entry point and the central Java configuration class.
 */
@SpringBootApplication
@EnableTransactionManagement
@EnableCaching
public class Application {

    /**
     * Entry point for the application.
     * 
     * @param args Command line arguments.
     * @throws Exception Thrown when an unexpected Exception is thrown from the
     *         application.
     */
    public static void main(String[] args) throws Exception {
        SpringApplication.run(Application.class, args);
    }

    /**
     * Create a CacheManager implementation class to be used by Spring where
     * <code>@Cacheable</code> annotations are applied.
     * 
     * @return a CacheManager instance with a list of 2 caches: customers and addresses
     */
    @Bean
    public CacheManager cacheManager() {

        SimpleCacheManager simpleCacheManager = new SimpleCacheManager();
        GuavaCache addressesCache = new GuavaCache("addresses", 
        		CacheBuilder
        		.newBuilder()
        		.build());
        GuavaCache customersCache = new GuavaCache("customers", 
        		CacheBuilder
        		.newBuilder()
                .expireAfterAccess(10, TimeUnit.MINUTES)
                .build());
        simpleCacheManager.setCaches(Arrays.asList(addressesCache, customersCache));
        return simpleCacheManager;
    }
}
