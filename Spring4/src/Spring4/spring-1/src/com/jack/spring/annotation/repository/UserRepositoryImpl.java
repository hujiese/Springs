package com.jack.spring.annotation.repository;

import org.springframework.stereotype.Repository;

//@Repository("userRepository")
@Repository
public class UserRepositoryImpl implements UserRepository {

    @Override
    public void save() {
        System.out.println("UserRepositoryImpl save ...");
    }
}
