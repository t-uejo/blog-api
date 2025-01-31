package com.example.blog.service.article;

import com.example.blog.repository.article.ArticleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ArticleServiceMockTest {

    @Mock
    private ArticleRepository articleRepository;

    @InjectMocks
    private ArticleService sut;

    @Test
    public void sut() {
        assertThat(sut).isNotNull();
    }

    @Test
    public void mockPractice(){
        when(articleRepository.selectById(999)).thenReturn(
                Optional.of(new ArticleEntity(999, null, null, null, null))
        );

        assertThat(articleRepository.selectById(999))
                .isPresent()
                .hasValueSatisfying(articleEntity ->
                        assertThat(articleEntity.id()).isEqualTo(999))
        ;
        assertThat(articleRepository.selectById(111)).isEmpty();
    }
}