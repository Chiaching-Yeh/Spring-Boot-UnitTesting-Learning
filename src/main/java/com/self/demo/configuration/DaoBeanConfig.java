package com.self.demo.configuration;

import com.self.demo.dao.UserInterface;
import lombok.extern.slf4j.Slf4j;
import org.jdbi.v3.core.Jdbi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class DaoBeanConfig {

    @Bean
    public UserInterface userDao(Jdbi jdbi) {
        return jdbi.onDemand(UserInterface.class);
    }

}
