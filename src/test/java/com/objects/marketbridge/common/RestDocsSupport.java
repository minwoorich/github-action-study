package com.objects.marketbridge.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;


@ExtendWith(RestDocumentationExtension.class)
public abstract class RestDocsSupport {

    protected MockMvc mockMvc;
    protected ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp(RestDocumentationContextProvider provider) {
        this.mockMvc =
                configureMockMvc(provider) ;
    }

    private MockMvc configureMockMvc(RestDocumentationContextProvider provider) {
        return MockMvcBuilders.standaloneSetup(initController())
                .apply(documentationConfiguration(provider))
                .build();
    }

    // 기본 값은 true 이고 만일 webApplicationContext 이 필요하면 오버라이드해서 false로 바꾸세요
    protected boolean useStandaloneSetup() {
        return true;
    }

    protected abstract Object initController();
}
