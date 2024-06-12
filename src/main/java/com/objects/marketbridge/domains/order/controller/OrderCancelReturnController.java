package com.objects.marketbridge.domains.order.controller;


import com.objects.marketbridge.common.responseobj.ApiResponse;
import com.objects.marketbridge.common.security.annotation.AuthMemberId;
import com.objects.marketbridge.domains.order.controller.dto.GetCancelReturnListHttp;
import com.objects.marketbridge.domains.order.service.port.OrderDetailDtoRepository;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Builder
@RestController
@RequiredArgsConstructor
@RequestMapping("/orders")
public class OrderCancelReturnController {

    private final OrderDetailDtoRepository orderDetailDtoRepository;

    @GetMapping("/cancel-return/list")
    public ApiResponse<Page<GetCancelReturnListHttp.Response>> getCancelReturnList (
            @RequestParam(name = "page") Integer page,
            @RequestParam(name = "size") Integer size,
            @AuthMemberId Long memberId
    ) {
        PageRequest pageRequest = PageRequest.of(page, size);
        return ApiResponse.ok(GetCancelReturnListHttp.Response.of(orderDetailDtoRepository.findCancelReturnListDtio(memberId, pageRequest)));
    }
}
