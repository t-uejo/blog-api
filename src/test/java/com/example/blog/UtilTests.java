package com.example.blog;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class UtilTests {
    @Test
    void bcrypt(){
        var encoder = new BCryptPasswordEncoder();
        System.out.println(encoder.encode("password"));
        System.out.println(encoder.encode("password"));
        System.out.println(encoder.encode("password"));
    }
}
