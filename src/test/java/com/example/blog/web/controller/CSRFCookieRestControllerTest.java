package com.example.blog.web.controller;

import org.hamcrest.core.StringContains;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class CSRFCookieRestControllerTest {

    @Nested
    class Return204Test {

        @Autowired
        private MockMvc mockMvc;

        @Test
        @DisplayName("GET /csrf-cookie: 204を返し、Set-Cookie ヘッダに XSRF-TOKEN が含まれている")
        void return204() throws Exception {
            mockMvc.perform(get("/csrf-cookie"))
                    .andExpect(status().isNoContent())
                    .andExpect(header().string("Set-Cookie", StringContains.containsString("XSRF-TOKEN")));
        }
    }

    @Nested
    class Return500Test {

        @Autowired
        private MockMvc mockMvc;

        @SuppressWarnings("removal")
        @MockBean
        private CSRFCookieRestController mockCSRFCookieRestController;

        @Test
        @DisplayName("GET /csrf-cookie: サーバでエラーが発生した場合は 500 を返す")
        void return500() throws Exception {
            doThrow(new RuntimeException("Exception"))
                    .when(mockCSRFCookieRestController).getCsrfCookie();

            mockMvc.perform(get("/csrf-cookie"))
                    .andExpect(status().isInternalServerError())
                    .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                    .andExpect(jsonPath("$.type").isEmpty())
                    .andExpect(jsonPath("$.title").value("Internal Server Error"))
                    .andExpect(jsonPath("$.detail").isEmpty())
                    .andExpect(jsonPath("$.instance").isEmpty())
            ;
        }
    }
}