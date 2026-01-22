//package com.datn.viettel.database;
//
//import com.datn.viettel.common.Constants;
//import jakarta.persistence.EntityManagerFactory;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.boot.context.properties.ConfigurationProperties;
//import org.springframework.boot.jdbc.DataSourceBuilder;
//import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Primary;
//import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
//import org.springframework.orm.jpa.JpaTransactionManager;
//import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
//import org.springframework.transaction.PlatformTransactionManager;
//import org.springframework.transaction.annotation.EnableTransactionManagement;
//
//import javax.sql.DataSource;
//
//@Configuration
//@EnableTransactionManagement
//@EnableJpaRepositories(
//        basePackages = Constants.Database.Secondary.PACKAGE_REPO,
//        entityManagerFactoryRef = Constants.Database.Secondary.BEAN_ENTITY_MANAGER_FACTORY,
//        transactionManagerRef = Constants.Database.Secondary.BEAN_TRANSACTION_MANAGER
//)
//public class DataSourceSecondaryConfig {
//
//    @Bean(name = Constants.Database.Secondary.BEAN_SECONDARY_DATASOURCE)
//    @ConfigurationProperties(prefix = Constants.Database.Secondary.PROPERTY_PREFIX)
//    public DataSource dbSecondaryDataSource() {
//        return DataSourceBuilder.create().build();
//    }
//
//    @Bean(name = Constants.Database.Secondary.BEAN_ENTITY_MANAGER_FACTORY)
//    public LocalContainerEntityManagerFactoryBean dbSecondaryEntityManagerFactory(
//            @Qualifier(Constants.Database.Secondary.BEAN_SECONDARY_DATASOURCE) DataSource dataSource,
//            EntityManagerFactoryBuilder builder) {
//        return builder
//                .dataSource(dataSource)
//                .packages(Constants.Database.Secondary.PACKAGE_ENTITY)
//                .persistenceUnit(Constants.Database.Secondary.UNIT)
//                .build();
//    }
//
//    @Bean(name = Constants.Database.Secondary.BEAN_TRANSACTION_MANAGER)
//    public PlatformTransactionManager dbSecondaryTransactionManager(
//            @Qualifier(Constants.Database.Secondary.BEAN_ENTITY_MANAGER_FACTORY) EntityManagerFactory entityManagerFactory) {
//        return new JpaTransactionManager(entityManagerFactory);
//    }
//
//}
//
