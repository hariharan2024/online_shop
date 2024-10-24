package com.example.PRODUCT_SERVICE.REPOSITY;

import com.example.PRODUCT_SERVICE.MODEL.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<Users, Long> {
        @Query("SELECT u FROM Users u WHERE LOWER(u.username) = LOWER(:username)")
        Users findByUsername(@Param("username") String username);
        }

