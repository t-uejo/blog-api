package com.example.blog.web.controller.article;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ArticleRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void mockMvc(){
        assertThat(mockMvc).isNotNull();
    }

    @Test
    @DisplayName("/articles/{id}: 指定されたIDの記事が存在する場合、200 OKを返す")
    @Sql(statements = {
         """
         INSERT INTO articles (id, title, body, created_at, updated_at)
         VALUES (999, 'title_999', 'body_999', '2022-01-02 03:04:05', '2023-01-02 03:04:05');
         """
    })
    void getArticleById_200OK() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/articles/{id}", 999))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(999))
                .andExpect(jsonPath("$.title").value("title_999"))
                .andExpect(jsonPath("$.content").value("body_999"))
                .andExpect(jsonPath("$.createdAt").value("2022-01-02T03:04:05"))
                .andExpect(jsonPath("$.updatedAt").value("2023-01-02T03:04:05"));
    }

    @Test
    @DisplayName("/articles/{id}: 指定されたIDの記事が存在しない場合、404 NOT FOUNDを返す")
    void getArticleById_404() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/articles/{id}", -999))
                .andExpect(status().isNotFound());
    }
}