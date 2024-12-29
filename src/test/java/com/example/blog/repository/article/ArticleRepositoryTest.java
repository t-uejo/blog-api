package com.example.blog.repository.article;

import com.example.blog.config.MybatisDefaultDatasourceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import static org.assertj.core.api.Assertions.assertThat;

@MybatisDefaultDatasourceTest
class ArticleRepositoryTest {

    @Autowired
    private ArticleRepository sut;

    @Test
    void sut() {
        assertThat(sut).isNotNull();
    }

    @Test
    @DisplayName("selectById: 指定されたIDの記事が存在するとき、ArticleEntityを返す")
    @Sql(statements = {
            """
            INSERT INTO articles (id, title, body, created_at, updated_at)
            VALUES (999, 'title_999', 'body_999', '2024-01-01 00:00:00', '2024-01-01 00:00:00');
            """
    })
    void selectById_returnArticleEntity(){
        var actual = sut.selectById(999);

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
    @DisplayName("selectById: 指定されたIDの記事が存在しないとき、Optional.emptyを返す")
    void selectById_returnEmpty(){
        var actual = sut.selectById(-999);

        assertThat(actual).isEmpty();
    }
}