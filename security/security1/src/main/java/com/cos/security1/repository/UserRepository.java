package com.cos.security1.repository;

import com.cos.security1.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

// CRUD 함수를 JpaRepository가 들고 있음
// @Repository라는 어노테이션 없어도 IOC 된다. 이유는 JpaRepository 상속했기 때문에...
public interface UserRepository extends JpaRepository<User, Integer> {

    // findBy 규칙 -> Username 문법
    // select * from user where username = 1?
    Optional<User> findByUsername(String username);    // Jpa Query method 검색하면 자세히 공부 할 수 있음
}
