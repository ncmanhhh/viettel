package com.datn.viettel.database;

import com.datn.viettel.common.Constants;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
//import org.springframework.boot.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration //Đánh dấu class câu hình Spring
@EnableTransactionManagement // Kích hoạt quản lý giao dịch trong Spring
@EnableJpaRepositories(basePackages = Constants.Database.Primary.PACKAGE_REPO) //Kích hoạt JPA Repositories và chỉ định các repo trong package này sẽ làm việc với db Primary

public class DataSourcePrimaryConfig {

    @Primary //Đánh dấu đây là cấu hình chính khi có nhiều cấu hình cùng loại
    @Bean (name = Constants.Database.Primary.BEAN_PRIMARY_DATASOURCE) //Định nghĩa một bean với tên "primaryDataSource"
    @ConfigurationProperties(prefix = Constants.Database.Primary.PROPERTY_PREFIX) //Liên kết các thuộc tính cấu hình từ application.properties với bean này
    public DataSource primaryDataSource() {
        return DataSourceBuilder.create().build(); //Tạo và trả về một DataSource dựa trên các thuộc tính cấu hình đã liên kết
    }


    //Hàm tạo EntityManagerFactory cho database Primary
    @Primary
    @Bean(name = Constants.Database.Primary.BEAN_ENTITY_MANAGER_FACTORY)
    public LocalContainerEntityManagerFactoryBean primaryEntityManagerFactory(
            @Qualifier(Constants.Database.Primary.BEAN_PRIMARY_DATASOURCE) DataSource dataSource,
            EntityManagerFactoryBuilder builder) {
        return builder
                .dataSource(dataSource)
                .packages(Constants.Database.Primary.PACKAGE_ENTITY)
                .persistenceUnit(Constants.Database.Primary.UNIT) // Đặt tên cho persistence unit để định danh cho JPA này
                .build();
    }


    //Hàm tạo TransactionManager cho database Primary
    @Primary
    @Bean(name = Constants.Database.Primary.BEAN_TRANSACTION_MANAGER)
    public PlatformTransactionManager primaryTransactionManager(
            @Qualifier(Constants.Database.Primary.BEAN_ENTITY_MANAGER_FACTORY) EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory); // Tạo và trả về một JpaTransactionManager sử dụng EntityManagerFactory đã cung cấp
    }
}
