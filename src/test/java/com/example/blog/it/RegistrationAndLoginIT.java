package com.example.blog.it;

import com.example.blog.service.user.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RegistrationAndLoginIT {

    private final static String TEST_USERNAME = "user1";
    private final static String TEST_PASSWORD = "password1";
    private final static String DUMMY_SESSION_ID = "session_id_1";

    @Autowired
    private WebTestClient webTestClient;
    @Autowired
    private UserService userService;

    @BeforeEach
    public void beforeEach(){
        userService.delete(TEST_USERNAME);
    }

    @AfterEach
    public void afterEach(){
        userService.delete(TEST_USERNAME);
    }

    @Test
    public void integrationTest(){
        var xsrfToken = getRoot();
        register(xsrfToken);
        loginSuccess(xsrfToken);
    }

    /**
     * ルートエンドポイントへリクエスト
     * @return XSRF-TOKENのValue値
     */
    private String getRoot() {
        var responseSpec = webTestClient.get().uri("/").exchange();
        var response = responseSpec.returnResult(String.class);
        //CookieのXSRF-TOKENを取得
        var xsrfTokenOpt = Optional.ofNullable(response.getResponseCookies().getFirst("XSRF-TOKEN"));

        // response statusがNo Content(204)であることを確認
        responseSpec.expectStatus().isNoContent();
        assertThat(xsrfTokenOpt)
                //Optional<null>でないことを確認
                .isPresent()
                //XSRF-TOKENのvalue属性がNULLまたは空文字でないことを確認
                .hasValueSatisfying(xsrfTokenCookie ->
                        assertThat(xsrfTokenCookie.getValue()).isNotBlank()
                );

        return xsrfTokenOpt.get().getValue();
    }

    /**
     * ユーザ登録
     * @param xsrfToken
     */
    private void register(String xsrfToken) {
        //Arrange
        String bodyJson = String.format(
                """
                {
                    "username": "%s",
                    "password": "%s"
                }
                """, TEST_USERNAME, TEST_PASSWORD);

        //Act
        var responseSpec = webTestClient
                .post().uri("users")
                .contentType(MediaType.APPLICATION_JSON)
                .cookie("XSRF-TOKEN", xsrfToken)
                .header("X-XSRF-TOKEN", xsrfToken)
                .bodyValue(bodyJson)
                .exchange();

        //Assert
        responseSpec.expectStatus().isCreated();
    }

    /**
     * ログイン成功
     * @param xsrfToken
     */
    private void loginSuccess(String xsrfToken) {
        //Arrange
        String bodyJson = String.format(
                """
                {
                    "username": "%s",
                    "password": "%s"
                }
                """, TEST_USERNAME, TEST_PASSWORD);

        //Act
        var responseSpec = webTestClient
                .post().uri("login")
                .contentType(MediaType.APPLICATION_JSON)
                .cookie("XSRF-TOKEN", xsrfToken)
                .cookie("JSESSIONID", DUMMY_SESSION_ID)
                .header("X-XSRF-TOKEN", xsrfToken)
                .bodyValue(bodyJson)
                .exchange();

        //Assert
        responseSpec
                //ログインが成功しているかを確認
                .expectStatus().isOk()
                .expectCookie().value("JSESSIONID", v ->
                        assertThat(v)
                                //JSESSIONIDがレスポンスされていることを確認
                                .isNotBlank()
                                //リクエストのJSESSIONIDと異なることを確認（セッション固定化攻撃対策確認用）
                                .isNotEqualTo(DUMMY_SESSION_ID)
                );
    }
}
