package com.objects.marketbridge.errors;


import com.objects.marketbridge.common.RestDocsSupportWebAppContext;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@WebMvcTest(ErrorController.class)
@ActiveProfiles("test")
public class CommonErrorControllerTest extends RestDocsSupportWebAppContext {

    @MockBean
    ErrorController errorController;

    @DisplayName("지원하지 않는 HTTP Method 호출 한 경우")
    @Test
    void error1() throws Exception {
        // given
        // when

        //then
        mockMvc.perform(post("/errors/methodNotAllowed"))
                .andExpect(status().isMethodNotAllowed())
                .andDo(print());
    }

    @DisplayName("PathVariable 의 타입을 잘못 입력한 경우")
    @Test
    void error2() throws Exception {
        // given
        doThrow(MethodArgumentTypeMismatchException.class).when(errorController).pathVariableTypeMismatch(anyLong());

        // when

        //then
        mockMvc.perform(get("/errors/r"))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @DisplayName("QueryParameter 을 잘못 요청한 경우")
    @Test
    void error3() throws Exception {
        // given
        // when

        //then
        mockMvc.perform(get("/errors").param("sizee","2"))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @DisplayName("URI 를 잘못 요청한 경우")
    @Test
    void error4() throws Exception {
        // given
        // when

        //then
        mockMvc.perform(get("/errorssssss"))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @DisplayName("RequestBody 의 필드 타입이 안 맞는 경우")
    @Test
    void error5() throws Exception {
        ErrorRequest errRequest = new ErrorRequest("나이", "홍길동");

        // when

        //then
        mockMvc.perform(post("/errors")
                        .header(HttpHeaders.AUTHORIZATION, "bearer AccessToken")
                        .content(objectMapper.writeValueAsString(errRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @DisplayName("RequestBody 의 필드 유효성 검증에 실패한 경우")
    @Test
    void error6() throws Exception {
        ErrorRequest errRequest = new ErrorRequest(0, "홍길동");

        // when

        //then
        mockMvc.perform(post("/errors")
                        .header(HttpHeaders.AUTHORIZATION, "bearer AccessToken")
                        .content(objectMapper.writeValueAsString(errRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @DisplayName("내부 서버 에러")
    @Test
    void error7() throws Exception {

        doThrow(RuntimeException.class).when(errorController).internalServerError();

        // when

        //then
        mockMvc.perform(post("/server-errors"))
                .andExpect(status().isInternalServerError())
                .andDo(print());
    }

    @Getter
    @NoArgsConstructor
    public static class ErrorRequest{
        Object age;
        Object name;

        public ErrorRequest(Object age, Object name) {
            this.age = age;
            this.name = name;
        }
    }
}
