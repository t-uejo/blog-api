package com.example.blog.repository.user;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Optional;

@Mapper
public interface UserRepository {
    @Select("""
            SELECT
                  username
                , password
                , enabled
            FROM users
            WHERE username = #{username}
            """
    )
    Optional<UserRecord> selectByUsername(@Param("username") String username);
}
