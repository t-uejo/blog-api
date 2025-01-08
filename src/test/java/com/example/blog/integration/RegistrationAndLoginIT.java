package com.example.blog.integration;

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

    private final static String TEST_USERNAME = "user99";
    private final static String TEST_PASSWORD = "password99";
    private final static String DUMMY_SESSION_ID = "session_id_99";
    private static final String SESSION_COOKIE_NAME = "SESSION";

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
        //ルートエンドポイント
        var xsrfToken = getRoot();

        //ユーザ登録
        register(xsrfToken);

        //ログイン失敗
        //CookieにXSRF-TOKENがない
        loginFailure_NoXSRFTokenInCookie(xsrfToken);
        //リクエストヘッダーにX-XSRF-TOKENがない
        loginFailure_NoXXSRFTokenInHeader(xsrfToken);
        //トークン値が異なる
        loginFailure_DifferentToken(xsrfToken);
        //ユーザが存在しない
        loginFailure_GivenUsernameDoneNotExistInDatabase(xsrfToken);
        //パスワード間違い
        loginFailure_GivenPasswordIsNotSameAsPasswordInDatabase(xsrfToken);

        //ログイン成功
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
     * @param xsrfToken XSRF-TOKEN
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
     * @param xsrfToken XSRF-TOKEN
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
                .cookie(SESSION_COOKIE_NAME, DUMMY_SESSION_ID)
                .header("X-XSRF-TOKEN", xsrfToken)
                .bodyValue(bodyJson)
                .exchange();

        //Assert
        responseSpec
                //ログインが成功しているかを確認
                .expectStatus().isOk()
                .expectCookie().value(SESSION_COOKIE_NAME, v ->
                        assertThat(v)
                                //SESSIONIDがレスポンスされていることを確認
                                .isNotBlank()
                                //リクエストのSESSIONIDと異なることを確認（セッション固定化攻撃対策確認用）
                                .isNotEqualTo(DUMMY_SESSION_ID)
                );
    }

    /**
     * ログイン失敗（CookieにXSRF-TOKENがない）
     * @param xsrfToken XSRF-TOKEN
     */
    private void loginFailure_NoXSRFTokenInCookie(String xsrfToken) {
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
                /*
                .cookie("XSRF-TOKEN", xsrfToken)
                */
                .cookie(SESSION_COOKIE_NAME, DUMMY_SESSION_ID)
                .header("X-XSRF-TOKEN", xsrfToken)
                .bodyValue(bodyJson)
                .exchange();

        //Assert
        responseSpec.expectStatus().isForbidden();
    }

    /**
     * ログイン失敗（リクエストヘッダーにX-XSRF-TOKENがない）
     * @param xsrfToken XSRF-TOKEN
     */
    private void loginFailure_NoXXSRFTokenInHeader(String xsrfToken) {
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
                .cookie(SESSION_COOKIE_NAME, DUMMY_SESSION_ID)
                /*
                .header("X-XSRF-TOKEN", xsrfToken)
                */
                .bodyValue(bodyJson)
                .exchange();

        //Assert
        responseSpec.expectStatus().isForbidden();
    }

    /**
     * ログイン失敗（Cookieとリクエストヘッダーのトークン値が異なる）
     * @param xsrfToken XSRF-TOKEN
     */
    private void loginFailure_DifferentToken(String xsrfToken) {
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
                .cookie(SESSION_COOKIE_NAME, DUMMY_SESSION_ID)
                .header("X-XSRF-TOKEN", xsrfToken + "_invalid")
                .bodyValue(bodyJson)
                .exchange();

        //Assert
        responseSpec.expectStatus().isForbidden();
    }

    /**
     * ログイン失敗（データベースにユーザが存在しない）
     * @param xsrfToken XSRF-TOKEN
     */
    private void loginFailure_GivenUsernameDoneNotExistInDatabase(String xsrfToken) {
        //Arrange
        String bodyJson = String.format(
                """
                {
                    "username": "%s",
                    "password": "%s"
                }
                """, TEST_USERNAME + "_invalid", TEST_PASSWORD);

        //Act
        var responseSpec = webTestClient
                .post().uri("login")
                .contentType(MediaType.APPLICATION_JSON)
                .cookie("XSRF-TOKEN", xsrfToken)
                .cookie(SESSION_COOKIE_NAME, DUMMY_SESSION_ID)
                .header("X-XSRF-TOKEN", xsrfToken)
                .bodyValue(bodyJson)
                .exchange();

        //Assert
        responseSpec.expectStatus().isUnauthorized();
    }

    /**
     * ログイン失敗（パスワード間違い）
     * @param xsrfToken XSRF-TOKEN
     */
    private void loginFailure_GivenPasswordIsNotSameAsPasswordInDatabase(String xsrfToken) {
        //Arrange
        String bodyJson = String.format(
                """
                {
                    "username": "%s",
                    "password": "%s"
                }
                """, TEST_USERNAME, TEST_PASSWORD + "_invalid");

        //Act
        var responseSpec = webTestClient
                .post().uri("login")
                .contentType(MediaType.APPLICATION_JSON)
                .cookie("XSRF-TOKEN", xsrfToken)
                .cookie(SESSION_COOKIE_NAME, DUMMY_SESSION_ID)
                .header("X-XSRF-TOKEN", xsrfToken)
                .bodyValue(bodyJson)
                .exchange();

        //Assert
        responseSpec.expectStatus().isUnauthorized();
    }
}
