package com.objects.marketbridge.domains.member.controller;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.objects.marketbridge.common.exception.exceptions.CustomLogicException;
import com.objects.marketbridge.common.security.annotation.WithMockCustomUser;
import com.objects.marketbridge.common.security.config.SpringSecurityTestConfig;
import com.objects.marketbridge.common.security.domain.CustomUserDetails;
import com.objects.marketbridge.common.security.dto.JwtTokenDto;
import com.objects.marketbridge.common.security.service.JwtTokenProvider;
import com.objects.marketbridge.domains.member.dto.SignInDto;
import com.objects.marketbridge.domains.member.dto.SignUpDto;
import com.objects.marketbridge.domains.member.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static com.objects.marketbridge.common.exception.exceptions.ErrorCode.DUPLICATE_ERROR;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc
@ContextConfiguration(classes = {SpringSecurityTestConfig.class})
@ExtendWith(RestDocumentationExtension.class)
public class AuthControllerTest {

    @MockBean
    AuthService authService;
    @MockBean
    JwtTokenProvider jwtTokenProvider;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp(WebApplicationContext webApplicationContext,
                      RestDocumentationContextProvider restDocumentationContextProvider) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentationContextProvider))
                .build();
    }

    @Test
    public void singUp_ok() throws Exception {
        //given
        SignUpDto signUpDto = SignUpDto.builder()
                .email("member@example.com")
                .password("암호화_된_비밀번호")
                .name("테스트")
                .phoneNo("01000000000")
                .isAgree(true)
                .build();

        willDoNothing().given(authService).signUp(signUpDto);

        //when
        ResultActions actions = mockMvc.perform(post("/auth/sign-up")
                .content(objectMapper.writeValueAsString(signUpDto))
                .contentType(MediaType.APPLICATION_JSON));

        //then
        actions.andExpect(status().isCreated())
                .andDo(document("auth-sign-up",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .requestFields(
                                        fieldWithPath("email").type(JsonFieldType.STRING)
                                                .description("중복 체크가 완료된 이메일"),
                                        fieldWithPath("password").type(JsonFieldType.STRING)
                                                .description("암호화 된 비밀번호"),
                                        fieldWithPath("name").type(JsonFieldType.STRING)
                                                .description("이름"),
                                        fieldWithPath("phoneNo").type(JsonFieldType.STRING)
                                                .description("연락처"),
                                        fieldWithPath("isAgree").type(JsonFieldType.BOOLEAN)
                                                .description("회원가입 동의 여부")
                                )
                                        .responseFields(
                                        fieldWithPath("code").type(JsonFieldType.NUMBER)
                                                .description("코드"),
                                        fieldWithPath("status").type(JsonFieldType.STRING)
                                                .description("상태"),
                                        fieldWithPath("message").type(JsonFieldType.STRING)
                                                .description("메시지"),
                                        fieldWithPath("data").type(JsonFieldType.NULL)
                                                .description("응답 데이터")
                                )
                                .requestSchema(Schema.schema("AuthSignUpReq"))
                                .responseSchema(Schema.schema("AuthSignUpRes"))
                                .build()
                        )

                ));
    }

    @Test
    public void singUp_valid_err() throws Exception {
        //given
        SignUpDto signUpDto = SignUpDto.builder()
                .email("member@example.com")
                .password("암호화_된_비밀번호")
                .name("테스트")
                .phoneNo("01000000")
                .isAgree(true)
                .build();

        willDoNothing().given(authService).signUp(signUpDto);

        //when
        ResultActions actions = mockMvc.perform(post("/auth/sign-up")
                .content(objectMapper.writeValueAsString(signUpDto))
                .contentType(MediaType.APPLICATION_JSON));

        //then
        actions.andExpect(status().isBadRequest())
                .andDo(document("auth-sign-up-valid-err",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())
                ));
    }

    @Test
    public void singUp_email_dup() throws Exception {
        //given
        SignUpDto signUpDto = SignUpDto.builder()
                .email("member@example.com")
                .password("암호화_된_비밀번호")
                .name("테스트")
                .phoneNo("01000000000")
                .isAgree(true)
                .build();

        CustomLogicException badRequestError = CustomLogicException.createBadRequestError(DUPLICATE_ERROR);

        doThrow(badRequestError).when(authService).signUp(any());

        //when
        ResultActions actions = mockMvc.perform(post("/auth/sign-up")
                .content(objectMapper.writeValueAsString(signUpDto))
                .contentType(MediaType.APPLICATION_JSON));

        //then
        actions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value(DUPLICATE_ERROR.name()))
                .andDo(document("auth-sign-up-email-dup",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())
                ));
    }

    @Test
    @WithMockCustomUser
    public void signIn() throws Exception {
        //given
        SignInDto signInDto = SignInDto.builder()
                .email("member@example.com")
                .password("암호화_된_비밀번호")
                .build();

        JwtTokenDto jwtTokenDto = JwtTokenDto.builder()
                .grantType("bearer")
                .accessToken("accessToken")
                .refreshToken("refreshToken")
                .build();

        given(jwtTokenProvider.generateToken(any(CustomUserDetails.class))).willReturn(jwtTokenDto);

        //when
        ResultActions actions = mockMvc.perform(post("/auth/sign-in")
                .content(objectMapper.writeValueAsString(signInDto))
                .contentType(MediaType.APPLICATION_JSON));

        //then
        actions.andExpect(status().isOk())
                .andDo(document("auth-sign-in",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(ResourceSnippetParameters.builder()
                                .requestFields(
                                        fieldWithPath("email").type(JsonFieldType.STRING)
                                                .description("이메일"),
                                        fieldWithPath("password").type(JsonFieldType.STRING)
                                                .description("암호화 된 비밀번호")
                                )
                                .responseFields(
                                        fieldWithPath("code").type(JsonFieldType.NUMBER)
                                                .description("코드"),
                                        fieldWithPath("status").type(JsonFieldType.STRING)
                                                .description("상태"),
                                        fieldWithPath("message").type(JsonFieldType.STRING)
                                                .description("메시지"),
                                        fieldWithPath("data").type(JsonFieldType.OBJECT)
                                                .description("응답 데이터"),
                                        fieldWithPath("data.grantType").type(JsonFieldType.STRING)
                                                .description("토큰 타입: bearer"),
                                        fieldWithPath("data.accessToken").type(JsonFieldType.STRING)
                                                .description("api 요청용 AccessToken"),
                                        fieldWithPath("data.refreshToken").type(JsonFieldType.STRING)
                                                .description("AccessToken 만료시 재발급용 Token 다른 api 요청 안됨")
                                )
                                .requestSchema(Schema.schema("AuthSignInReq"))
                                .responseSchema(Schema.schema("AuthSignInRes"))
                                .build()
                        )

                ));
    }

    @Test
    @WithMockCustomUser
    public void signOut() throws Exception {
        //given
        Long memberId = 1L;
        willDoNothing().given(jwtTokenProvider).deleteToken(memberId);

        //when
        ResultActions actions = mockMvc.perform(delete("/auth/sign-out")
                .header(HttpHeaders.AUTHORIZATION, "bearer AccessToken")
                .contentType(MediaType.APPLICATION_JSON));

        //then
        actions.andExpect(status().isOk())
                .andDo(document("auth-sign-out",
                        preprocessResponse(prettyPrint()),
                        resource(ResourceSnippetParameters.builder()
                                .responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER)
                                        .description("코드"),
                                fieldWithPath("status").type(JsonFieldType.STRING)
                                        .description("상태"),
                                fieldWithPath("message").type(JsonFieldType.STRING)
                                        .description("메시지"),
                                fieldWithPath("data").type(JsonFieldType.NULL)
                                        .description("응답 데이터")
                        )
                                .responseSchema(Schema.schema("AuthSignOutRes"))
                                .build()
                        )
                ));

    }

    @Test
    @WithMockCustomUser
    public void reIssueToken() throws Exception {
        //given
        JwtTokenDto jwtTokenDto = JwtTokenDto.builder()
                .grantType("bearer")
                .accessToken("accessToken")
                .refreshToken("refreshToken")
                .build();

        given(jwtTokenProvider.generateToken(any(CustomUserDetails.class))).willReturn(jwtTokenDto);


        //when
        ResultActions actions = mockMvc.perform(put("/auth/re-issue")
                .header(HttpHeaders.AUTHORIZATION, "bearer RefreshToken")
                .contentType(MediaType.APPLICATION_JSON));

        //then
        actions.andExpect(status().isOk())
                .andDo(document("auth-re-issue",
                        preprocessResponse(prettyPrint()),
                        resource(ResourceSnippetParameters.builder()
                                        .responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER)
                                        .description("코드"),
                                fieldWithPath("status").type(JsonFieldType.STRING)
                                        .description("상태"),
                                fieldWithPath("message").type(JsonFieldType.STRING)
                                        .description("메시지"),
                                fieldWithPath("data").type(JsonFieldType.OBJECT)
                                        .description("응답 데이터"),
                                fieldWithPath("data.grantType").type(JsonFieldType.STRING)
                                        .description("토큰 타입: bearer"),
                                fieldWithPath("data.accessToken").type(JsonFieldType.STRING)
                                        .description("api 요청용 AccessToken"),
                                fieldWithPath("data.refreshToken").type(JsonFieldType.STRING)
                                        .description("AccessToken 만료시 재발급용 Token 다른 api 요청 안됨")
                        )                                .responseSchema(Schema.schema("AuthReissueRes"))
                                        .build()
                        )

                ));
    }

}
