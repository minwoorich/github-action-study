package com.objects.marketbridge.common.modifydb;

import com.objects.marketbridge.common.security.annotation.UserAuthorize;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@UserAuthorize
@RequestMapping("/update-db")
public class ModifyDbController {

}
