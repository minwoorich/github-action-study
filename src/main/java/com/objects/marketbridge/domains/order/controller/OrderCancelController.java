package com.objects.marketbridge.domains.order.controller;


import com.objects.marketbridge.common.responseobj.ApiResponse;
import com.objects.marketbridge.common.security.annotation.AuthMemberId;
import com.objects.marketbridge.common.security.annotation.UserAuthorize;
import com.objects.marketbridge.common.utils.DateTimeHolder;
import com.objects.marketbridge.domains.member.service.port.MemberRepository;
import com.objects.marketbridge.domains.order.controller.dto.ConfirmCancelHttp;
import com.objects.marketbridge.domains.order.controller.dto.GetCancelDetailHttp;
import com.objects.marketbridge.domains.order.controller.dto.RequestCancelHttp;
import com.objects.marketbridge.domains.order.service.OrderCancelService;
import jakarta.validation.Valid;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Builder
@RestController
@RequiredArgsConstructor
@RequestMapping("/orders")
public class OrderCancelController {

    private final OrderCancelService orderCancelService;
    private final MemberRepository memberRepository;
    private final DateTimeHolder dateTimeHolder;

    @PostMapping("/cancel-flow/thank-you")
    @UserAuthorize
    public ApiResponse<ConfirmCancelHttp.Response> confirmCancel(
            @RequestBody @Valid ConfirmCancelHttp.Request request
    ) {
        return ApiResponse.ok(ConfirmCancelHttp.Response.of(orderCancelService.confirmCancel(request.toDto(), dateTimeHolder)));
    }

    @GetMapping("/cancel-flow")
    public ApiResponse<RequestCancelHttp.Response> requestCancel(
            @RequestParam(name = "orderDetailId") Long orderDetailId,
            @RequestParam(name = "numberOfCancellation") Long numberOfCancellation,
            @AuthMemberId Long memberId
    ) {
        String membership = memberRepository.findById(memberId).getMembership();
        return ApiResponse.ok(RequestCancelHttp.Response.of(orderCancelService.findCancelInfo(orderDetailId, numberOfCancellation, membership)));
    }

    @GetMapping("/cancel/detail")
    public ApiResponse<GetCancelDetailHttp.Response> getCancelDetail(
            @RequestParam(name = "cancelledOrderDetailId") Long cancelledOrderDetailId,
            @AuthMemberId Long memberId
    ) {
        String membership = memberRepository.findById(memberId).getMembership();
        return ApiResponse.ok(GetCancelDetailHttp.Response.of(orderCancelService.findCancelDetail(cancelledOrderDetailId, membership)));
    }
}
