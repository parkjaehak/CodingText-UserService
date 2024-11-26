package org.userservice.userservice.controller.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;


@FeignClient(name = "blog-service")
public interface BlogServiceClient {
    @PostMapping("/blog")
    ResponseEntity<?> createBlog(@RequestHeader("userId") String userId);

    @DeleteMapping("/blog")
    ResponseEntity<?> deleteBlog(@RequestHeader("userId") String userId);
}
