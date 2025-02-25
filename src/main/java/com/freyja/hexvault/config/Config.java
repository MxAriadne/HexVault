/*
 * Config.java
 *
 * Pulls configuration values from the application.properties file for
 * use in the application. Currently used for the database connection.
 * Pulls the database URL, username, password, and driver class name.
 * Also directs JPA to the entities package.
 *
 */
package com.freyja.hexvault.config;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import javax.sql.DataSource;

@Configuration
public class Config {

    @Value("${spring.datasource.url}")
    private String url;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    @Value("${spring.datasource.driver-class-name}")
    private String driverClassName;

    @Bean
    public DataSource dataSource() {
        // Create and configure a DataSource bean
        DataSourceBuilder<?> dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.driverClassName(driverClassName);
        dataSourceBuilder.url(url);
        dataSourceBuilder.username(username);
        dataSourceBuilder.password(password);
        return dataSourceBuilder.build();
    }

    // Configure the EntityManagerFactory for JPA
    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        // Set data source built by dataSource()
        em.setDataSource(dataSource);
        // Direct JPA to entities package
        em.setPackagesToScan("com.freyja.hexvault.entities");

        // Set JPA vendor
        JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);

        // Explicitly set the EntityManagerFactory interface to avoid conflict between
        // the EntityManagerFactory interfaces used by Spring and Hibernate.
        em.setEntityManagerFactoryInterface(EntityManagerFactory.class);

        return em;
    }

}