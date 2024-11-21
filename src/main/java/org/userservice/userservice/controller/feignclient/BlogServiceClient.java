package org.userservice.userservice.controller.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


@FeignClient(name = "blog-service")
public interface BlogServiceClient {
    @PostMapping("/blogs")
    ResponseEntity<?> createBlog(@RequestParam("userId") String userId);
}
