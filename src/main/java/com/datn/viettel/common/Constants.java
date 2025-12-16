package com.datn.viettel.common;

public class Constants {
    private Constants() {} // Không cho tạo đoi tượng từ lớp này. Nục đích để lưu các hằng số chung

    public static final class Database {
        private Database() {}

        public static final class Primary{
            private Primary () {}

            public static final String BEAN_PRIMARY_DATASOURCE = "primaryDataSource";
            public static final String PROPERTY_PREFIX = "spring.datasource";
            public static final String PACKAGE_REPO = "com.datn.viettel.repositories.core";
            public static final String BEAN_ENTITY_MANAGER_FACTORY = "entityManagerFactory";
            public static final String PACKAGE_ENTITY = "com.datn.viettel.entities.core";
            public static final String UNIT = "primary_datasource";
            public static final String BEAN_TRANSACTION_MANAGER = "transactionManager";
        }
    }
}
