package com.example.fixedlengthprocessor.config;

import jakarta.persistence.EntityManagerFactory;
import org.modelmapper.ModelMapper;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.sql.init.dependency.DependsOnDatabaseInitialization;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@EnableJpaRepositories("com.example.fixedlengthprocessor.repository")
@EntityScan("com.example.fixedlengthprocessor.domain")
@EnableTransactionManagement
public class JpaConfig {

    @Bean
    @DependsOnDatabaseInitialization
    public JpaTransactionManager transactionManager(DataSource dataSource) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory(dataSource));
        return transactionManager;
    }

    @Bean
    public EntityManagerFactory entityManagerFactory(DataSource dataSource) {

        HibernateJpaVendorAdapter jpaVendorAdapter = new HibernateJpaVendorAdapter();
        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setPackagesToScan("com.example.fixedlengthprocessor.domain");
        factory.setJpaVendorAdapter(jpaVendorAdapter);
        factory.setDataSource(dataSource);
        factory.afterPropertiesSet();

        return factory.getNativeEntityManagerFactory();
    }

    @Bean
    public ModelMapper eocModelMapper() {

        ModelMapper dpaModelMapper = new ModelMapper();
        dpaModelMapper.getConfiguration()
                .setFieldMatchingEnabled(true)
                .setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE);

        return dpaModelMapper;
    }

}
