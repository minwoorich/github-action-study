package com.objects.marketbridge.errors;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@Getter
@NoArgsConstructor
public class ErrorController {

    @GetMapping("/errors/{id}")
    public void pathVariableTypeMismatch(@PathVariable("id") Long id) {
    }

    @GetMapping("/errors")
    public void queryParameterError(@RequestParam("size") Long size) {
    }

    @PostMapping("/errors")
    public void fieldTypeError(@Validated  @RequestBody TestRequest request) {
    }

    @PostMapping("/server-errors")
    public void internalServerError() {
    }

    @Getter
    @NoArgsConstructor
    public static class TestRequest{
        @Min(value = 1, message = "1이상 100이하만 가능합니다")
        @Max(value = 100, message = "1이상 100이하만 가능합니다")
        int age;
        String  name;

        public TestRequest(int age, String name) {
            this.age = age;
            this.name = name;
        }
    }
}
