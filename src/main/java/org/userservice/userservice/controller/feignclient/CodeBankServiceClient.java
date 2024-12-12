package org.userservice.userservice.controller.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestHeader;


@FeignClient(name = "code-bank-service")
public interface CodeBankServiceClient {
    @DeleteMapping("/user")
    ResponseEntity<String> deleteCodeHistory(@RequestHeader("UserId") String userId);
}