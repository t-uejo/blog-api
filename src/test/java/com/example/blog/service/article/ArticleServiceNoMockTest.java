package com.example.blog.service.article;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class ArticleServiceNoMockTest {

    @Autowired
    ArticleService sut;

    @Test
    void sut() {
        assertThat(sut).isNotNull();
    }

    @Test
    @DisplayName("findById: 指定されたIDの記事が存在するとき、ArticleEntityを返す")
    @Sql(statements = {
            """
            INSERT INTO articles (id, title, body, created_at, updated_at)
            VALUES (999, 'title_999', 'body_999', '2024-01-01 00:00:00', '2024-01-01 00:00:00');
            """
    })
    void findById_returnArticleEntity(){
        var actual = sut.findById(999);

        assertThat(actual)
                .isPresent()
                .hasValueSatisfying(article -> {
                    assertThat(article.id()).isEqualTo(999);
                    assertThat(article.title()).isEqualTo("title_999");
                    assertThat(article.content()).isEqualTo("body_999");
                    assertThat(article.createdAt()).isEqualTo("2024-01-01T00:00:00");
                    assertThat(article.updatedAt()).isEqualTo("2024-01-01T00:00:00");
                })
        ;
    }

    @Test
    @DisplayName("findById: 指定されたIDの記事が存在しないとき、Optional.emptyを返す")
    void findById_returnEmpty(){
        var actual = sut.findById(-999);

        assertThat(actual).isEmpty();
    }
}