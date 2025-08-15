package com.self.demo.configuration;

import lombok.extern.slf4j.Slf4j;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;

import javax.sql.DataSource;

@Slf4j
@Configuration
public class DataSourceConfig {

    // ① 定義一個名稱為 "jdbi" 的 Spring Bean 這個方法會在 Spring 容器啟動時被呼叫
    @Bean("jdbi")
    public Jdbi jdbi(@Qualifier("datasource") DataSource dataSource) {

        // ② 建立一個 TransactionAwareDataSourceProxy
        //    - 包裝傳進來的 DataSource（此時 Spring 會去找名稱是 "datasource" 的 Bean）
        //    - 作用是讓 Jdbi 使用的 Connection 能夠跟 Spring 的 @Transactional 共用同一個交易
        TransactionAwareDataSourceProxy proxy = new TransactionAwareDataSourceProxy(dataSource);

        // ③ 用代理過的 DataSource 建立一個 Jdbi 實例
        //    - Jdbi.create(...) 會回傳一個可以操作 DB 的工具物件
        Jdbi jdbi = Jdbi.create(proxy);

        // ④ 安裝 SqlObjectPlugin 外掛
        //    - 這個外掛讓你能使用 DAO interface + onDemand 模式
        //    - 沒裝的話，Jdbi 只支援手動 createQuery / createUpdate，不支援 @SqlQuery 註解
        jdbi.installPlugin(new SqlObjectPlugin());

        // ⑤ 回傳 Jdbi Bean，讓 Spring 容器管理
        return jdbi;
    }


    // ⑥ 定義一個名稱為 "datasource" 的 Spring Bean（也是 @Primary 預設資料來源）
    //    - @ConfigurationProperties(prefix = "spring.datasource") 會自動綁定 application.yml / application.properties 裡的 spring.datasource.* 設定
    @Bean(name = "datasource")
    @ConfigurationProperties(prefix = "spring.datasource")
    @Primary
    public DataSource driverManagerDataSource() {

        // ⑦ 建立一個 DataSource（資料庫連線池或基本連線物件）
        //    - DataSourceBuilder.create() 會依你的設定自動建立 DriverManagerDataSource、HikariDataSource 等類型
        //    - 這是連線資料庫的核心物件
        return DataSourceBuilder.create().build();
    }


}
