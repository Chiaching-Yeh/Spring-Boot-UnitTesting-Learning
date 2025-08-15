package com.self.demo.dao;

import com.self.demo.model.User;
import org.jdbi.v3.sqlobject.SqlObject;
import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.statement.SqlQuery;

import java.util.Optional;

@RegisterBeanMapper(User.class)
public interface UserInterface extends SqlObject {

    @SqlQuery("SELECT user_id, name, email, card_id FROM users WHERE user_id = :id")
    Optional<User> findById(String id);

}
