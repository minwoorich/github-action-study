package com.objects.marketbridge.domains.member.controller;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.objects.marketbridge.common.exception.exceptions.CustomLogicException;
import com.objects.marketbridge.common.exception.exceptions.ErrorCode;
import com.objects.marketbridge.common.security.annotation.WithMockCustomUser;
import com.objects.marketbridge.common.security.config.SpringSecurityTestConfig;
import com.objects.marketbridge.common.utils.DateTimeHolder;
import com.objects.marketbridge.domains.coupon.domain.Coupon;
import com.objects.marketbridge.domains.member.constant.MemberConst;
import com.objects.marketbridge.domains.member.domain.AddressValue;
import com.objects.marketbridge.domains.member.domain.Member;
import com.objects.marketbridge.domains.member.domain.Wishlist;
import com.objects.marketbridge.domains.member.dto.*;
import com.objects.marketbridge.domains.member.service.GetMemberCouponService;
import com.objects.marketbridge.domains.member.service.MemberService;
import com.objects.marketbridge.domains.member.service.RegisterCouponService;
import com.objects.marketbridge.domains.order.mock.TestContainer;
import com.objects.marketbridge.domains.order.mock.TestDateTimeHolder;
import com.objects.marketbridge.domains.product.domain.Option;
import com.objects.marketbridge.domains.product.domain.ProdOption;
import com.objects.marketbridge.domains.product.domain.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
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

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static com.objects.marketbridge.common.exception.exceptions.ErrorCode.INVALID_PASSWORD;
import static com.objects.marketbridge.common.exception.exceptions.ErrorCode.MEMBER_NOT_FOUND;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ActiveProfiles("test")
@WebMvcTest(MemberController.class)
@AutoConfigureMockMvc
@ContextConfiguration(classes = {SpringSecurityTestConfig.class})
@ExtendWith(RestDocumentationExtension.class)
public class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    private MemberService memberService;

    @MockBean
    private RegisterCouponService registerCouponService;

    @MockBean
    private GetMemberCouponService getMemberCouponService;


    private final LocalDateTime orderDate = LocalDateTime.of(2024, 2, 9, 3, 9);
    private final DateTimeHolder dateTimeHolder = TestDateTimeHolder.builder()
            .createTime(orderDate)
            .build();
    private final TestContainer testContainer = TestContainer.builder()
            .dateTimeHolder(dateTimeHolder)
            .build();


    @BeforeEach
    public void setUp(WebApplicationContext webApplicationContext,
                      RestDocumentationContextProvider restDocumentationContextProvider) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentationContextProvider))
                .build();
    }

    @Test
    public void checkDuplicateEmail() throws Exception {
        //given
        String email = "member@example.com";
        CheckedResultDto checkedResultDto = CheckedResultDto.builder().checked(true).build();
        given(memberService.isDuplicateEmail(anyString()))
                .willReturn(checkedResultDto);

        //when
        ResultActions actions = mockMvc.perform(get("/member/email-check")
                .param("email", email)
                .contentType(MediaType.APPLICATION_JSON));

        //then
        actions.andExpect(status().isOk())
                .andExpect(jsonPath("$.data.checked").value(checkedResultDto.getChecked()))
                .andDo(document("member-email-check",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(ResourceSnippetParameters.builder()
                                .queryParameters(
                                        parameterWithName("email").description("중복 체크할 이메일")
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
                                        fieldWithPath("data.checked").type(JsonFieldType.BOOLEAN)
                                                .description("중복 체크 결과")
                                )
                                .requestSchema(Schema.schema("MemberEmailCheckReq"))
                                .responseSchema(Schema.schema("MemberEmailCheckRes"))
                                .build())

                ));
    }

    @Test
    @DisplayName("등록되어 있는 주소를 찾아서 반환")
    public void testFindAddress() throws Exception {
        //given
        AddressValue addressValue1 = AddressValue.builder()
                .phoneNo("12312341234")
                .name("집")
                .city("인천")
                .street("소래역남로 40")
                .zipcode("12345")
                .detail("C동 307호")
                .alias("우리집").build();

        AddressValue addressValue2 = AddressValue.builder()
                .phoneNo("56756785678")
                .name("회사")
                .city("서울")
                .street("강남대로 123")
                .zipcode("54321")
                .detail("A동 101호")
                .alias("회사").build();

        List<GetAddressesResponse> responses = Arrays.asList(
                GetAddressesResponse.builder()
                        .addressValue(addressValue1)
                        .addressId(1004L)
                        .isDefault(true)
                        .build(),
                GetAddressesResponse.builder()
                        .addressValue(addressValue2)
                        .addressId(1005L)
                        .isDefault(false)
                        .build()
        );
        //when
        given(memberService.findByMemberId(any())).willReturn(responses);

        //then
        mockMvc.perform(get("/member/address")
                        .header(HttpHeaders.AUTHORIZATION, "bearer AccessToken")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("find-address",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(ResourceSnippetParameters.builder()
                                .responseFields(
                                        fieldWithPath("code").type(JsonFieldType.NUMBER)
                                                .description("응답 코드"),
                                        fieldWithPath("status").type(JsonFieldType.STRING)
                                                .description("HTTP 응답"),
                                        fieldWithPath("message").type(JsonFieldType.STRING)
                                                .description("응답 메시지"),
                                        fieldWithPath("data").type(JsonFieldType.ARRAY)
                                                .description("주소 목록"),
                                        fieldWithPath("data[].addressId").type(JsonFieldType.NUMBER)
                                                .description("주소 ID"),
                                        fieldWithPath("data[].addressValue").type(JsonFieldType.OBJECT)
                                                .description("주소 정보"),
                                        fieldWithPath("data[].addressValue.phoneNo").type(JsonFieldType.STRING)
                                                .description("전화번호"),
                                        fieldWithPath("data[].addressValue.name").type(JsonFieldType.STRING)
                                                .description("이름"),
                                        fieldWithPath("data[].addressValue.city").type(JsonFieldType.STRING)
                                                .description("도시"),
                                        fieldWithPath("data[].addressValue.street").type(JsonFieldType.STRING)
                                                .description("거리"),
                                        fieldWithPath("data[].addressValue.zipcode").type(JsonFieldType.STRING)
                                                .description("우편번호"),
                                        fieldWithPath("data[].addressValue.detail").type(JsonFieldType.STRING)
                                                .description("상세주소"),
                                        fieldWithPath("data[].addressValue.alias").type(JsonFieldType.STRING)
                                                .description("별칭"),
                                        fieldWithPath("data[].isDefault").type(JsonFieldType.BOOLEAN)
                                                .description("기본 주소 여부")
                                ).responseSchema(Schema.schema("GetMemberAddressRes")).build()
                        )
                ));
    }

    @Test
    @DisplayName("주소를 새로 추가한다.")
    void addAddress() throws Exception {
        //given
        AddressValue addressValue1 = AddressValue.builder()
                .phoneNo("01012341234")
                .name("김민수")
                .city("인천")
                .street("소래역남로 40")
                .zipcode("12345")
                .detail("C동 307호")
                .alias("우리집").build();

        AddressValue addressValue2 = AddressValue.builder()
                .phoneNo("01012341234")
                .name("이민수")
                .city("서울")
                .street("강남대로 123")
                .zipcode("54321")
                .detail("A동 101호")
                .alias("회사").build();

        AddressValue addressValue3 = AddressValue.builder()
                .phoneNo("01012341234")
                .name("박민수")
                .city("서울")
                .street("123 Main St")
                .zipcode("12345")
                .detail("Apt 101")
                .alias("단골집").build();

        List<GetAddressesResponse> responses = Arrays.asList(
                GetAddressesResponse.builder()
                        .addressValue(addressValue1)
                        .addressId(1004L)
                        .isDefault(true)
                        .build(),
                GetAddressesResponse.builder()
                        .addressValue(addressValue2)
                        .addressId(1005L)
                        .isDefault(false)
                        .build(),
                GetAddressesResponse.builder()
                        .addressValue(addressValue3)
                        .addressId(1006L)
                        .isDefault(false)
                        .build()
        );

        AddAddressRequestDto request = AddAddressRequestDto.builder()
                .phoneNo("01012341234")
                .name("박민수")
                .city("서울")
                .street("123 Main St")
                .zipcode("12345")
                .detail("Apt 101")
                .alias("단골집").isDefault(false).build();

        // when ,then
        given(memberService.addMemberAddress(any(), any())).willReturn(responses);

        mockMvc.perform(post("/member/address")
                        .header(HttpHeaders.AUTHORIZATION, "bearer AccessToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("add-address",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(ResourceSnippetParameters.builder()
                                .requestFields(
                                        fieldWithPath("phoneNo").type(JsonFieldType.STRING)
                                                .description("전화번호"),
                                        fieldWithPath("name").type(JsonFieldType.STRING)
                                                .description("이름"),
                                        fieldWithPath("city").type(JsonFieldType.STRING)
                                                .description("도시"),
                                        fieldWithPath("street").type(JsonFieldType.STRING)
                                                .description("거리"),
                                        fieldWithPath("zipcode").type(JsonFieldType.STRING)
                                                .description("우편번호"),
                                        fieldWithPath("detail").type(JsonFieldType.STRING)
                                                .description("상세주소"),
                                        fieldWithPath("alias").type(JsonFieldType.STRING)
                                                .description("별칭"),
                                        fieldWithPath("isDefault").type(JsonFieldType.BOOLEAN)
                                                .description("기본 주소 여부")
                                )
                                .responseFields(
                                        fieldWithPath("code").type(JsonFieldType.NUMBER)
                                                .description("응답 코드"),
                                        fieldWithPath("status").type(JsonFieldType.STRING)
                                                .description("HTTP 응답"),
                                        fieldWithPath("message").type(JsonFieldType.STRING)
                                                .description("응답 메시지"),
                                        fieldWithPath("data").type(JsonFieldType.ARRAY)
                                                .description("주소 목록").optional(),
                                        fieldWithPath("data[].addressId").type(JsonFieldType.NUMBER)
                                                .description("주소 ID"),
                                        fieldWithPath("data[].addressValue").type(JsonFieldType.OBJECT)
                                                .description("주소 정보"),
                                        fieldWithPath("data[].addressValue.phoneNo").type(JsonFieldType.STRING)
                                                .description("전화번호"),
                                        fieldWithPath("data[].addressValue.name").type(JsonFieldType.STRING)
                                                .description("이름"),
                                        fieldWithPath("data[].addressValue.city").type(JsonFieldType.STRING)
                                                .description("도시"),
                                        fieldWithPath("data[].addressValue.street").type(JsonFieldType.STRING)
                                                .description("거리"),
                                        fieldWithPath("data[].addressValue.zipcode").type(JsonFieldType.STRING)
                                                .description("우편번호"),
                                        fieldWithPath("data[].addressValue.detail").type(JsonFieldType.STRING)
                                                .description("상세주소"),
                                        fieldWithPath("data[].addressValue.alias").type(JsonFieldType.STRING)
                                                .description("별칭"),
                                        fieldWithPath("data[].isDefault").type(JsonFieldType.BOOLEAN)
                                                .description("기본 주소 여부")
                                ).requestSchema(Schema.schema("PostMemberAddressReq"))
                                .responseSchema(Schema.schema("PostMemberAddressRes")).build()
                        )
                ));
    }

    @Test
    @DisplayName("등록되어 있는 주소를 찾아서 수정")
    void updateAddress() throws Exception {
        //given
        AddressValue addressValue1 = AddressValue.builder()
                .phoneNo("01012341234")
                .name("김길동")
                .city("인천")
                .street("소래역남로 40")
                .zipcode("12345")
                .detail("C동 307호")
                .alias("우리집").build();

        AddressValue addressValue2 = AddressValue.builder()
                .phoneNo("01012341234")
                .name("홍길동")
                .city("서울")
                .street("강남대로 123")
                .zipcode("54321")
                .detail("A동 101호")
                .alias("회사").build();

        AddressValue addressValue3 = AddressValue.builder()
                .phoneNo("01012341234")
                .name("이승우")
                .city("서울")
                .street("메인로 123")
                .zipcode("12345")
                .detail("101호")
                .alias("회식장소").build();

        List<GetAddressesResponse> responses = Arrays.asList(
                GetAddressesResponse.builder()
                        .addressValue(addressValue1)
                        .addressId(1004L)
                        .isDefault(true)
                        .build(),
                GetAddressesResponse.builder()
                        .addressValue(addressValue2)
                        .addressId(1005L)
                        .isDefault(false)
                        .build(),
                GetAddressesResponse.builder()
                        .addressValue(addressValue3)
                        .addressId(1006L)
                        .isDefault(false)
                        .build()
        );

        UpdateAddressRequestDto request = UpdateAddressRequestDto.builder().city("제주").phoneNo("01088881111").build();


        //when
        given(memberService.updateMemberAddress(any(), any(), any())).willReturn(responses);

        //then
        String addressId = "1006"; // 예시로 사용할 주문 번호를 지정합니다.
        mockMvc.perform(patch("/member/address/{addressId}", Long.parseLong(addressId))
                        .header(HttpHeaders.AUTHORIZATION, "bearer AccessToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("update-address",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(ResourceSnippetParameters.builder()
                                .pathParameters(
                                        parameterWithName("addressId")
                                                .description("수정할 주소 id")
                                )
                                .requestFields(
                                        fieldWithPath("phoneNo").type(JsonFieldType.STRING)
                                                .description("전화번호").optional(),
                                        fieldWithPath("name").type(JsonFieldType.STRING)
                                                .description("이름").optional(),
                                        fieldWithPath("city").type(JsonFieldType.STRING)
                                                .description("도시").optional(),
                                        fieldWithPath("street").type(JsonFieldType.STRING)
                                                .description("거리").optional(),
                                        fieldWithPath("zipcode").type(JsonFieldType.STRING)
                                                .description("우편번호").optional(),
                                        fieldWithPath("detail").type(JsonFieldType.STRING)
                                                .description("상세주소").optional(),
                                        fieldWithPath("alias").type(JsonFieldType.STRING)
                                                .description("별칭").optional(),
                                        fieldWithPath("isDefault").type(JsonFieldType.BOOLEAN)
                                                .description("기본 주소 여부").optional()
                                )
                                .responseFields(
                                        fieldWithPath("code").type(JsonFieldType.NUMBER)
                                                .description("응답 코드"),
                                        fieldWithPath("status").type(JsonFieldType.STRING)
                                                .description("HTTP 응답"),
                                        fieldWithPath("message").type(JsonFieldType.STRING)
                                                .description("응답 메시지"),
                                        fieldWithPath("data").type(JsonFieldType.ARRAY)
                                                .description("주소 목록").optional(),
                                        fieldWithPath("data[].addressId").type(JsonFieldType.NUMBER)
                                                .description("주소 ID"),
                                        fieldWithPath("data[].addressValue").type(JsonFieldType.OBJECT)
                                                .description("주소 정보"),
                                        fieldWithPath("data[].addressValue.phoneNo").type(JsonFieldType.STRING)
                                                .description("전화번호"),
                                        fieldWithPath("data[].addressValue.name").type(JsonFieldType.STRING)
                                                .description("이름"),
                                        fieldWithPath("data[].addressValue.city").type(JsonFieldType.STRING)
                                                .description("도시"),
                                        fieldWithPath("data[].addressValue.street").type(JsonFieldType.STRING)
                                                .description("거리"),
                                        fieldWithPath("data[].addressValue.zipcode").type(JsonFieldType.STRING)
                                                .description("우편번호"),
                                        fieldWithPath("data[].addressValue.detail").type(JsonFieldType.STRING)
                                                .description("상세주소"),
                                        fieldWithPath("data[].addressValue.alias").type(JsonFieldType.STRING)
                                                .description("별칭"),
                                        fieldWithPath("data[].isDefault").type(JsonFieldType.BOOLEAN)
                                                .description("기본 주소 여부")
                                ).requestSchema(Schema.schema("PatchMemberAddressReq"))
                                .responseSchema(Schema.schema("PatchMemberAddressRes"))
                                .build()
                        )
                ));
    }

    @Test
    @DisplayName("등록되어 있는 주소를 찾아서 삭제")
    void deleteAddress() throws Exception {
        String responses = MemberConst.DELETE_ADDRESS_SUCCESSFULLY;

        //when
        given(memberService.deleteMemberAddress(any())).willReturn(responses);

        //then
        String addressId = "1006";
        mockMvc.perform(delete("/member/address/{addressId}", addressId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer AccessToken")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("delete-address",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(ResourceSnippetParameters.builder()
                                .pathParameters(
                                        parameterWithName("addressId")
                                                .description("삭제할 주소 id")
                                )
                                .responseFields(
                                        fieldWithPath("code").type(JsonFieldType.NUMBER)
                                                .description("응답 코드"),
                                        fieldWithPath("status").type(JsonFieldType.STRING)
                                                .description("HTTP 응답"),
                                        fieldWithPath("message").type(JsonFieldType.STRING)
                                                .description("응답 메시지"),
                                        fieldWithPath("data").type(JsonFieldType.STRING)
                                                .description("메세지")
                                ).responseSchema(Schema.schema("DeleteMemberAddressRes"))
                                .build()
                        )
                ));
    }

    @Test
    @DisplayName("wishlist 추가")
    void addWishlist() throws Exception {
        // Given
        WishlistRequest request = WishlistRequest.builder()
                .productId(1L)
                .build();

        // When Then
        mockMvc.perform(post("/member/wishlist")
                        .header(HttpHeaders.AUTHORIZATION, "bearer AccessToken")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("add-wishlist",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(ResourceSnippetParameters.builder()
                                .requestFields(
                                        fieldWithPath("productId").type(JsonFieldType.NUMBER)
                                                .description("추가할 제품의 ID")
                                )
                                .responseFields(
                                        fieldWithPath("code").type(JsonFieldType.NUMBER)
                                                .description("응답 코드"),
                                        fieldWithPath("status").type(JsonFieldType.STRING)
                                                .description("HTTP 응답"),
                                        fieldWithPath("message").type(JsonFieldType.STRING)
                                                .description("응답 메시지"),
                                        fieldWithPath("data").type(JsonFieldType.NULL)
                                                .description("응답 데이터")
                                ).requestSchema(Schema.schema("PostMemberWishlistReq"))
                                .responseSchema(Schema.schema("PostMemberWishlistRes"))
                                .build()
                        )
                ));


    }

    @Test
    @DisplayName("Wishlist에 담겨있는 상품인지 아닌지 조회")
    void checkWishlist() throws Exception {
        //given
        Boolean response = false;

        WishlistRequest request = WishlistRequest.builder()
                .productId(1L)
                .build();
        //when,then
        given(memberService.checkWishlist(any(), any())).willReturn(response);

        mockMvc.perform(get("/member/wishlist-check")
                        .header(HttpHeaders.AUTHORIZATION, "bearer AccessToken")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("check-wishlist",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(ResourceSnippetParameters.builder()
                                .requestFields(
                                        fieldWithPath("productId").type(JsonFieldType.NUMBER)
                                                .description("추가할 제품의 ID")
                                )
                                .responseFields(
                                        fieldWithPath("code").type(JsonFieldType.NUMBER)
                                                .description("응답 코드"),
                                        fieldWithPath("status").type(JsonFieldType.STRING)
                                                .description("HTTP 응답"),
                                        fieldWithPath("message").type(JsonFieldType.STRING)
                                                .description("응답 메시지"),
                                        fieldWithPath("data").type(JsonFieldType.BOOLEAN)
                                                .description("응답 데이터(false 추가 가능)")
                                ).requestSchema(Schema.schema("GetMemberWishlistCheckReq"))
                                .responseSchema(Schema.schema("GetMemberWishlistCheckRes"))
                                .build()
                        )
                ));

    }

    @Test
    @DisplayName("wishlist 삭제")
    void deleteWishlist() throws Exception {
        // Given
        WishlistRequest request = WishlistRequest.builder()
                .productId(1L)
                .build();

        // When Then
        mockMvc.perform(delete("/member/wishlist")
                        .header(HttpHeaders.AUTHORIZATION, "bearer AccessToken")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("delete-wishlist",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(ResourceSnippetParameters.builder()
                                .requestFields(
                                        fieldWithPath("productId").type(JsonFieldType.NUMBER)
                                                .description("삭제할 제품의 ID")
                                )
                                .responseFields(
                                        fieldWithPath("code").type(JsonFieldType.NUMBER)
                                                .description("응답 코드"),
                                        fieldWithPath("status").type(JsonFieldType.STRING)
                                                .description("HTTP 응답"),
                                        fieldWithPath("message").type(JsonFieldType.STRING)
                                                .description("응답 메시지"),
                                        fieldWithPath("data").type(JsonFieldType.NULL)
                                                .description("응답 데이터")
                                ).requestSchema(Schema.schema("DeleteMemberWishlistReq"))
                                .responseSchema(Schema.schema("DeleteMemberWishlistRes"))
                                .build()
                        )
                ));


    }


    @Test
    @DisplayName("MemberId로 wishlist 조회")
    void findWishlistByMemberId() throws Exception {
        //given
        Member member = Member.builder().email("wishTest@naver.con").build();

        Product product1 = Product.builder()
                .productNo("1")
                .price(100L)
                .name("롱패딩")
                .thumbImg("static/image1")
                .stock(1L)
                .build();

        Product product2 = Product.builder()
                .productNo("2")
                .price(200L)
                .name("티셔츠")
                .thumbImg("static/image2")
                .stock(2L)
                .build();

        Product product3 = Product.builder()
                .productNo("3")
                .price(300L)
                .name("숏패딩")
                .thumbImg("static/image3")
                .stock(3L)
                .build();

        Product product4 = Product.builder()
                .productNo("4")
                .price(400L)
                .name("긴팔티셔츠")
                .thumbImg("static/image4")
                .stock(4L)
                .build();

        Product product5 = Product.builder()
                .productNo("5")
                .price(500L)
                .name("후드티")
                .thumbImg("static/image1")
                .stock(5L)
                .build();

        testContainer.productRepository.save(product1);
        testContainer.productRepository.save(product2);
        testContainer.productRepository.save(product3);
        testContainer.productRepository.save(product4);
        testContainer.productRepository.save(product5);

        Option option1 = Option.builder()
                .name("M사이즈").build();

        Option option2 = Option.builder()
                .name("네이비컬러").build();

        ProdOption prodOption1_1 = ProdOption.builder()
                .option(option1).build();
        ProdOption prodOption1_2 = ProdOption.builder()
                .option(option2).build();


        ProdOption prodOption2_1 = ProdOption.builder()
                .option(option1).build();
        ProdOption prodOption2_2 = ProdOption.builder()
                .option(option2).build();


        ProdOption prodOption3_1 = ProdOption.builder()
                .option(option1).build();
        ProdOption prodOption3_2 = ProdOption.builder()
                .option(option2).build();


        ProdOption prodOption4_1 = ProdOption.builder()
                .option(option1).build();
        ProdOption prodOption4_2 = ProdOption.builder()
                .option(option2).build();


        ProdOption prodOption5_1 = ProdOption.builder()
                .option(option1).build();
        ProdOption prodOption5_2 = ProdOption.builder()
                .option(option2).build();

        product1.addProdOptions(prodOption1_1);
        product1.addProdOptions(prodOption1_2);

        product2.addProdOptions(prodOption2_1);
        product2.addProdOptions(prodOption2_2);

        product3.addProdOptions(prodOption3_1);
        product3.addProdOptions(prodOption3_2);

        product4.addProdOptions(prodOption4_1);
        product4.addProdOptions(prodOption4_2);

        product5.addProdOptions(prodOption5_1);
        product5.addProdOptions(prodOption5_2);

        Wishlist wishlist1 = Wishlist.builder()
                .member(member)
                .product(product1).build();

        Wishlist wishlist2 = Wishlist.builder()
                .member(member)
                .product(product2).build();

        Wishlist wishlist3 = Wishlist.builder()
                .member(member)
                .product(product3).build();

        Wishlist wishlist4 = Wishlist.builder()
                .member(member)
                .product(product4).build();

        Wishlist wishlist5 = Wishlist.builder()
                .member(member)
                .product(product5).build();

        int pageNumber = 0;
        int pageSize = 1;
        Sort sort = Sort.by(Sort.Order.desc("createdAt"));
        Pageable pageRequest = PageRequest.of(pageNumber, pageSize, sort);
        //when

        Slice<Wishlist> mockSlice = new SliceImpl<>(Arrays.asList(wishlist1, wishlist2, wishlist3, wishlist4, wishlist5));
        Slice<WishlistResponse> mockResponseSlice = new SliceImpl<>(mockSlice.getContent().stream()
                .map(WishlistResponse::of)
                .collect(Collectors.toList()), pageRequest, mockSlice.hasNext());

        given(memberService.findWishlistById(any(Pageable.class), any())).willReturn(mockResponseSlice);
        System.out.println("mockResponseSlice = " + mockResponseSlice.getContent().get(0).getProductId());
        System.out.println("product1.getId() = " + product1.getId());

        //then
        mockMvc.perform(get("/member/wishlist")
                        .param("page", "0")
                        .param("size", "2")
                        .param("sort", "createdAt,DESC")
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "bearer AccessToken"))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("find-wishlist",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(ResourceSnippetParameters.builder()
                                .queryParameters(
                                        parameterWithName("page").description("페이지 번호"),
                                        parameterWithName("size").description("페이지 사이즈"),
                                        parameterWithName("sort").description("정렬기준,정렬순서")
                                )
                                .responseFields(
                                        fieldWithPath("code").type(JsonFieldType.NUMBER)
                                                .description("응답 코드"),
                                        fieldWithPath("status").type(JsonFieldType.STRING)
                                                .description("HTTP 응답"),
                                        fieldWithPath("message").type(JsonFieldType.STRING)
                                                .description("응답 메시지"),
                                        fieldWithPath("data").type(JsonFieldType.OBJECT)
                                                .description("데이터"),
                                        fieldWithPath("data.content").type(JsonFieldType.ARRAY)
                                                .description("위시 리스트"),
                                        fieldWithPath("data.content[].productId").type(JsonFieldType.NUMBER)
                                                .description("제품 ID").optional(),
                                        fieldWithPath("data.content[].optionNameList[]").type(JsonFieldType.ARRAY)
                                                .description("옵션 목록"),
                                        fieldWithPath("data.content[].optionNameList[]").type(JsonFieldType.ARRAY)
                                                .description("옵션 이름"),
                                        fieldWithPath("data.content[].price").type(JsonFieldType.NUMBER)
                                                .description("가격"),
                                        fieldWithPath("data.content[].productName").type(JsonFieldType.STRING)
                                                .description("제품 이름"),
                                        fieldWithPath("data.content[].thumbImgUrl").type(JsonFieldType.STRING)
                                                .description("썸네일 이미지 URL"),
                                        fieldWithPath("data.content[].isSoldOut").type(JsonFieldType.BOOLEAN)
                                                .description("품절 여부"),
                                        fieldWithPath("data.pageable").type(JsonFieldType.OBJECT)
                                                .description("페이징 정보"),
                                        fieldWithPath("data.pageable.pageNumber").type(JsonFieldType.NUMBER)
                                                .description("페이지 번호"),
                                        fieldWithPath("data.pageable.pageSize").type(JsonFieldType.NUMBER)
                                                .description("페이지 크기"),
                                        fieldWithPath("data.pageable.sort").type(JsonFieldType.OBJECT)
                                                .description("정렬 정보"),
                                        fieldWithPath("data.pageable.sort.empty").type(JsonFieldType.BOOLEAN)
                                                .description("정렬 정보: 비어 있음 여부").optional(),
                                        fieldWithPath("data.pageable.sort.sorted").type(JsonFieldType.BOOLEAN)
                                                .description("정렬 정보: 정렬되었는지 여부").optional(),
                                        fieldWithPath("data.pageable.sort.unsorted").type(JsonFieldType.BOOLEAN)
                                                .description("정렬 정보: 정렬되지 않았는지 여부").optional(),
                                        fieldWithPath("data.pageable.offset").type(JsonFieldType.NUMBER)
                                                .description("오프셋").optional(),
                                        fieldWithPath("data.pageable.paged").type(JsonFieldType.BOOLEAN)
                                                .description("페이징 여부").optional(),
                                        fieldWithPath("data.pageable.unpaged").type(JsonFieldType.BOOLEAN)
                                                .description("페이징되지 않음 여부").optional(),
                                        fieldWithPath("data.size").type(JsonFieldType.NUMBER)
                                                .description("데이터 크기").optional(),
                                        fieldWithPath("data.number").type(JsonFieldType.NUMBER)
                                                .description("현재 페이지 번호").optional(),
                                        fieldWithPath("data.sort").type(JsonFieldType.OBJECT)
                                                .description("정렬 정보").optional(),
                                        fieldWithPath("data.sort.empty").type(JsonFieldType.BOOLEAN)
                                                .description("정렬 정보: 비어 있음 여부").optional(),
                                        fieldWithPath("data.sort.sorted").type(JsonFieldType.BOOLEAN)
                                                .description("정렬 정보: 정렬되었는지 여부").optional(),
                                        fieldWithPath("data.sort.unsorted").type(JsonFieldType.BOOLEAN)
                                                .description("정렬 정보: 정렬되지 않았는지 여부").optional(),
                                        fieldWithPath("data.numberOfElements").type(JsonFieldType.NUMBER)
                                                .description("현재 페이지의 요소 수").optional(),
                                        fieldWithPath("data.last").type(JsonFieldType.BOOLEAN)
                                                .description("마지막 페이지 여부").optional(),
                                        fieldWithPath("data.first").type(JsonFieldType.BOOLEAN)
                                                .description("첫 번째 페이지 여부").optional(),
                                        fieldWithPath("data.empty").type(JsonFieldType.BOOLEAN)
                                                .description("데이터가 비어 있는지 여부").optional()
                                ).responseSchema(Schema.schema("GetMemberWishlistRes"))
                                .build()
                        )
                ));
    }

    @Test
    @WithMockCustomUser
    public void getEmail() throws Exception {
        //given
        Long memberId = 1L;
        MemberEmail memberEmail = MemberEmail.builder().email("test@test.com").build();

        given(memberService.getEmail(memberId)).willReturn(memberEmail);

        //when
        ResultActions actions = mockMvc.perform(get("/member/account-email")
                .header(HttpHeaders.AUTHORIZATION, "bearer AccessToken")
                .contentType(MediaType.APPLICATION_JSON));

        //then
        actions.andExpect(status().isOk())
                .andDo(document("member-account-email",
                        preprocessRequest(prettyPrint()),
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
                                        fieldWithPath("data.email").type(JsonFieldType.STRING)
                                                .description("로그인 이메일")
                                )
                                .responseSchema(Schema.schema("MemberAccountEmailRes"))
                                .build())

                ));
    }

    @Test
    @WithMockCustomUser
    public void getMemberInfo() throws Exception {
        //given
        Long memberId = 1L;
        String password = "password";
        GetMemberInfo memberInfo = GetMemberInfo.builder()
                .email("test@test.com")
                .name("테스트")
                .phoneNo("01012341234")
                .isAlert(true)
                .isAgree(true)
                .build();

        given(memberService.getMemberInfo(memberId, password)).willReturn(memberInfo);

        //when
        ResultActions actions = mockMvc.perform(get("/member/account-info")
                .header(HttpHeaders.AUTHORIZATION, "bearer AccessToken")
                .param("password", password)
                .contentType(MediaType.APPLICATION_JSON));

        //then
        actions.andExpect(status().isOk())
                .andDo(document("get-member-account-info",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(ResourceSnippetParameters.builder()
                                .queryParameters(
                                        parameterWithName("password").description("암호화된 비밀번호")
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
                                        fieldWithPath("data.email").type(JsonFieldType.STRING)
                                                .description("로그인 이메일"),
                                        fieldWithPath("data.name").type(JsonFieldType.STRING)
                                                .description("이름"),
                                        fieldWithPath("data.phoneNo").type(JsonFieldType.STRING)
                                                .description("휴대폰 번호"),
                                        fieldWithPath("data.isAlert").type(JsonFieldType.BOOLEAN)
                                                .description("알림 동의 여부"),
                                        fieldWithPath("data.isAgree").type(JsonFieldType.BOOLEAN)
                                                .description("마케팅 수신 동의 여부")
                                )
                                .requestSchema(Schema.schema("GetMemberAccountInfoReq"))
                                .responseSchema(Schema.schema("GetMemberAccountInfoRes"))
                                .build())

                ));
    }

    @Test
    @WithMockCustomUser
    public void getMemberInfo_pw_err() throws Exception {
        String password = "password";


        CustomLogicException badRequestError = CustomLogicException.createBadRequestError(INVALID_PASSWORD);

        doThrow(badRequestError).when(memberService).getMemberInfo(anyLong(), anyString());

        ResultActions actions = mockMvc.perform(get("/member/account-info")
                .header(HttpHeaders.AUTHORIZATION, "bearer AccessToken")
                .param("password", password)
                .contentType(MediaType.APPLICATION_JSON));

        //then
        actions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value(INVALID_PASSWORD.name()))
                .andDo(document("get-member-account-info-pw-err",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())));
    }

    @Test
    @WithMockCustomUser
    public void updateMemberInfo() throws Exception {
        //given
        Long memberId = 1L;
        UpdateMemberInfo updateMemberInfo = UpdateMemberInfo.builder()
                .name("테스트")
                .phoneNo("01012341234")
                .newPassword("변경이 있을 때만 보내기, 변경이 없으면 null이나 보내지 않기")
                .build();

        willDoNothing().given(memberService).updateMemberInfo(memberId, updateMemberInfo);

        //when
        ResultActions actions = mockMvc.perform(patch("/member/account-info")
                .header(HttpHeaders.AUTHORIZATION, "bearer AccessToken")
                .content(objectMapper.writeValueAsString(updateMemberInfo))
                .contentType(MediaType.APPLICATION_JSON));

        //then
        actions.andExpect(status().isOk())
                .andDo(document("patch-member-account-info",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(ResourceSnippetParameters.builder()
                                .requestFields(
                                        fieldWithPath("name").type(JsonFieldType.STRING)
                                                .description("이름"),
                                        fieldWithPath("phoneNo").type(JsonFieldType.STRING)
                                                .description("휴대폰 번호"),
                                        fieldWithPath("newPassword").type(JsonFieldType.STRING).optional()
                                                .description("변경된 비밀번호")
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
                                .requestSchema(Schema.schema("PatchMemberAccountInfoReq"))
                                .responseSchema(Schema.schema("PatchMemberAccountInfoRes"))
                                .build())

                ));
    }

    @Test
    @WithMockCustomUser
    public void updateMemberInfo_valid_err() throws Exception {
        UpdateMemberInfo updateMemberInfo = UpdateMemberInfo.builder()
                .name("테스트")
                .phoneNo("0101234123")
                .newPassword("변경이 있을 때만 보내기, 변경이 없으면 null이나 보내지 않기")
                .build();

        ResultActions actions = mockMvc.perform(patch("/member/account-info")
                .header(HttpHeaders.AUTHORIZATION, "bearer AccessToken")
                .content(objectMapper.writeValueAsString(updateMemberInfo))
                .contentType(MediaType.APPLICATION_JSON));

        actions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value(ErrorCode.INVALID_INPUT_VALUE.name()))
                .andDo(document("patch-member-account-info-err",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())));
    }

    @Test
    public void findMemberEmail() throws Exception {
        //given;
        String name = "테스트";
        String phoneNo = "01000000000";
        MemberEmail memberEmail = MemberEmail.builder().email("test@test.com").build();

        given(memberService.findMemberEmail(name, phoneNo)).willReturn(memberEmail);

        //when
        ResultActions actions = mockMvc.perform(get("/member/email-find")
                .param("name", name).param("phoneNo", phoneNo)
                .contentType(MediaType.APPLICATION_JSON));

        //then
        actions.andExpect(status().isOk())
                .andDo(document("member-email-find",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(ResourceSnippetParameters.builder()
                                .queryParameters(
                                        parameterWithName("name").description("이름"),
                                        parameterWithName("phoneNo").description("휴대폰 번호")
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
                                        fieldWithPath("data.email").type(JsonFieldType.STRING)
                                                .description("가입된 멤버의 이메일(아이디)")
                                ).requestSchema(Schema.schema("MemberEmailFindReq"))
                                .responseSchema(Schema.schema("MemberEmailFindRes"))
                                .build())

                ));
    }

    @Test
    public void findMemberEmail_err() throws Exception {
        String name = "테스트";
        String phoneNo = "01000000000";
        CustomLogicException badRequestError = CustomLogicException.createBadRequestError(MEMBER_NOT_FOUND);

        doThrow(badRequestError).when(memberService).findMemberEmail(anyString(), anyString());
        //when
        ResultActions actions = mockMvc.perform(get("/member/email-find")
                .param("name", name).param("phoneNo", phoneNo)
                .contentType(MediaType.APPLICATION_JSON));

        //then
        actions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value(MEMBER_NOT_FOUND.name()))
                .andDo(document("member-email-find-err",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())));
    }

    @Test
    public void findMemberId() throws Exception {
        //given;
        String name = "테스트";
        String email = "test@test.com";
        MemberId memberId = MemberId.builder()
                .memberId(1L)
                .build();

        given(memberService.findMemberId(name, email)).willReturn(memberId);

        //when
        ResultActions actions = mockMvc.perform(get("/member/password-find")
                .param("name", name).param("email", email)
                .contentType(MediaType.APPLICATION_JSON));

        //then
        actions.andExpect(status().isOk())
                .andDo(document("member-password-find",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(ResourceSnippetParameters.builder()
                                .queryParameters(
                                        parameterWithName("name").description("이름"),
                                        parameterWithName("email").description("이메일")
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
                                        fieldWithPath("data.memberId").type(JsonFieldType.NUMBER)
                                                .description("가입된 멤버의 아이디")

                                ).requestSchema(Schema.schema("MemberPasswordFindReq"))
                                .responseSchema(Schema.schema("MemberPasswordFindRes"))
                                .build())

                ));
    }

    @Test
    public void findMemberId_err() throws Exception {
        String name = "테스트";
        String email = "test@test.com";

        CustomLogicException badRequestError = CustomLogicException.createBadRequestError(MEMBER_NOT_FOUND);

        doThrow(badRequestError).when(memberService).findMemberId(anyString(), anyString());

        ResultActions actions = mockMvc.perform(get("/member/password-find")
                .param("name", name).param("email", email)
                .contentType(MediaType.APPLICATION_JSON));

        //then
        actions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value(MEMBER_NOT_FOUND.name()))
                .andDo(document("member-password-find-err",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())));
    }


    @Test
    public void updatePassword() throws Exception {
        //given
        UpdatePassword updatePassword = UpdatePassword.builder()
                .memberId(1L)
                .password("password")
                .build();

        willDoNothing().given(memberService).updatePassword(updatePassword);

        //when
        ResultActions actions = mockMvc.perform(patch("/member/password-reset")
                .content(objectMapper.writeValueAsString(updatePassword))
                .contentType(MediaType.APPLICATION_JSON));

        //then
        actions.andExpect(status().isOk())
                .andDo(document("member-password-reset",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(ResourceSnippetParameters.builder()
                                .requestFields(
                                        fieldWithPath("memberId").type(JsonFieldType.NUMBER)
                                                .description("GET /member/account-find 찾은 멤버 아이디"),
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
                                        fieldWithPath("data").type(JsonFieldType.NULL)
                                                .description("응답 데이터")
                                )
                                .requestSchema(Schema.schema("MemberPasswordResetReq"))
                                .responseSchema(Schema.schema("MemberPasswordResetRes"))
                                .build()

                        )

                ));
    }

    @DisplayName("[API] POST /member/coupons/{couponId}")
    @Test
    @WithMockCustomUser
    void registerCoupon() throws Exception {
        //given
        Coupon coupon = Coupon.builder()
                .id(1L)
                .name("1000원 할인 쿠폰")
                .price(1000L)
                .productGroupId(111111L)
                .count(10L)
                .minimumPrice(15000L)
                .startDate(LocalDateTime.of(2024, 1, 1, 12, 0, 0))
                .endDate(LocalDateTime.of(2025, 1, 1, 12, 0, 0))
                .build();

        given(registerCouponService.registerCouponToMember(anyLong(), anyLong())).willReturn(coupon);

        //when
        ResultActions actions = mockMvc.perform(
                post("/member/coupons/{couponId}", coupon.getId())
                .header(HttpHeaders.AUTHORIZATION, "bearer AccessToken")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        //then
        actions.andExpect(status().isOk())
                .andDo(print())
                .andDo(document("member-register-coupon",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .pathParameters(
                                                parameterWithName("couponId").description("쿠폰 아이디")
                                        )
                                        .responseFields(
                                                fieldWithPath("code").type(JsonFieldType.NUMBER)
                                                        .description("응답 코드"),
                                                fieldWithPath("status").type(JsonFieldType.STRING)
                                                        .description("HTTP 응답"),
                                                fieldWithPath("message").type(JsonFieldType.STRING)
                                                        .description("메시지"),
                                                fieldWithPath("data").type(JsonFieldType.OBJECT)
                                                        .description("응답 데이터"),

                                                fieldWithPath("data.name").type(JsonFieldType.STRING)
                                                        .description("등록된 쿠폰 여부"),
                                                fieldWithPath("data.price").type(JsonFieldType.NUMBER)
                                                        .description("쿠폰 가격"),
                                                fieldWithPath("data.productGroupId").type(JsonFieldType.NUMBER)
                                                        .description("상품 그룹 아이디"),
                                                fieldWithPath("data.count").type(JsonFieldType.NUMBER)
                                                        .description("남은 쿠폰 수량 (판매자가 등록한 총 쿠폰 수량)"),
                                                fieldWithPath("data.minimumPrice").type(JsonFieldType.NUMBER)
                                                        .description("최소 구매 조건 금액"),
                                                fieldWithPath("data.startDate").type(JsonFieldType.STRING)
                                                        .description("쿠폰 시작기한 (yyyy-MM-dd HH:mm:ss) "),
                                                fieldWithPath("data.endDate").type(JsonFieldType.STRING)
                                                        .description("쿠폰 만료기한 (yyyy-MM-dd HH:mm:ss) ")
                                        )
                                        .responseSchema(Schema.schema("PostMembersCouponsRes"))
                                        .build()
                        )
                ));
    }

    @Test
    @WithMockCustomUser
    @DisplayName("[API] GET /member/coupons")
    void findCouponsForMember() throws Exception {
        // given
        Coupon coupon = Coupon.builder()
                .id(1L)
                .name("1000원 할인 쿠폰")
                .price(1000L)
                .productGroupId(111111L)
                .count(9999L)
                .minimumPrice(15000L)
                .startDate(LocalDateTime.of(2024, 1, 1, 12, 0, 0))
                .endDate(LocalDateTime.of(2025, 1, 1, 12, 0, 0))
                .build();

        given(getMemberCouponService.findCouponsForMember(anyLong())).willReturn(List.of(coupon));

        // when
        ResultActions actions = mockMvc.perform(get("/member/coupons")
                .header(HttpHeaders.AUTHORIZATION, "bearer AccessToken")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        //then
        actions.andExpect(status().isOk())
                .andDo(print())
                .andDo(document("member-find-coupon",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .responseFields(
                                                fieldWithPath("code").type(JsonFieldType.NUMBER)
                                                        .description("응답 코드"),
                                                fieldWithPath("status").type(JsonFieldType.STRING)
                                                        .description("HTTP 응답"),
                                                fieldWithPath("message").type(JsonFieldType.STRING)
                                                        .description("메시지"),
                                                fieldWithPath("data").type(JsonFieldType.OBJECT)
                                                        .description("응답 데이터"),

                                                fieldWithPath("data.memberId").type(JsonFieldType.NUMBER)
                                                        .description("회원 아이디"),
                                                fieldWithPath("data.hasCoupons").type(JsonFieldType.BOOLEAN)
                                                        .description("쿠폰 존재 여부"),

                                                fieldWithPath("data.couponInfos[].couponId").type(JsonFieldType.NUMBER)
                                                        .description("쿠폰 아이디"),
                                                fieldWithPath("data.couponInfos[].name").type(JsonFieldType.STRING)
                                                        .description("쿠폰 이름"),
                                                fieldWithPath("data.couponInfos[].price").type(JsonFieldType.NUMBER)
                                                        .description("쿠폰 금액"),
                                                fieldWithPath("data.couponInfos[].productGroupId").type(JsonFieldType.NUMBER)
                                                        .description("상품 그룹 아이디"),
                                                fieldWithPath("data.couponInfos[].count").type(JsonFieldType.NUMBER)
                                                        .description("남은 쿠폰 수량 (판매자가 등록한 총 쿠폰 수량)"),
                                                fieldWithPath("data.couponInfos[].minimumPrice").type(JsonFieldType.NUMBER)
                                                        .description("최소 구매 조건 금액"),
                                                fieldWithPath("data.couponInfos[].startDate").type(JsonFieldType.STRING)
                                                        .description("쿠폰 시작기한 (yyyy-MM-dd HH:mm:ss) "),
                                                fieldWithPath("data.couponInfos[].endDate").type(JsonFieldType.STRING)
                                                        .description("쿠폰 만료기한 (yyyy-MM-dd HH:mm:ss) ")
                                        )
                                        .responseSchema(Schema.schema("GetMembersCouponsRes"))
                                        .build()
                        )
                ));
    }


}
