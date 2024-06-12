package com.objects.marketbridge.domains.order.controller;


import com.objects.marketbridge.common.responseobj.ApiResponse;
import com.objects.marketbridge.common.security.annotation.AuthMemberId;
import com.objects.marketbridge.common.utils.DateTimeHolder;
import com.objects.marketbridge.domains.member.service.port.MemberRepository;
import com.objects.marketbridge.domains.order.controller.dto.ConfirmReturnHttp;
import com.objects.marketbridge.domains.order.controller.dto.GetReturnDetailHttp;
import com.objects.marketbridge.domains.order.controller.dto.RequestReturnHttp;
import com.objects.marketbridge.domains.order.service.OrderReturnService;
import jakarta.validation.Valid;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Builder
@RestController
@RequiredArgsConstructor
@RequestMapping("/orders")
public class OrderReturnController {

    private final OrderReturnService orderReturnService;
    private final MemberRepository memberRepository;
    private final DateTimeHolder dateTimeHolder;

    @PostMapping("/return-flow/thank-you")
    public ApiResponse<ConfirmReturnHttp.Response> confirmReturn(
            @RequestBody @Valid ConfirmReturnHttp.Request request
    ) {
        return ApiResponse.ok(ConfirmReturnHttp.Response.of(orderReturnService.confirmReturn(request.toDto(), dateTimeHolder)));
    }

    @GetMapping("/return-flow")
    public ApiResponse<RequestReturnHttp.Response> requestReturn (
            @RequestParam(name = "orderDetailId") Long orderDetailId,
            @RequestParam(name = "numberOfReturns") Long numberOfReturns,
            @AuthMemberId Long memberId
    ) {
        String membership = memberRepository.findById(memberId).getMembership();
        return ApiResponse.ok(RequestReturnHttp.Response.of(orderReturnService.findReturnInfo(orderDetailId, numberOfReturns, membership)));
    }

    @GetMapping("/return/detail")
    public ApiResponse<GetReturnDetailHttp.Response> getReturnDetail(
            @RequestParam(name = "returnedOrderDetailId") Long returnedOrderDetailId,
            @AuthMemberId Long memberId
    ) {
        String membership = memberRepository.findById(memberId).getMembership();
        return ApiResponse.ok(GetReturnDetailHttp.Response.of(orderReturnService.findReturnDetail(returnedOrderDetailId, membership)));
    }

}
