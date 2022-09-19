package com.project.alphaspringbot.repository;

import com.project.alphaspringbot.model.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {
}
