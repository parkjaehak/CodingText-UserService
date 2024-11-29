package org.userservice.userservice.controller.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;


@FeignClient(name = "blog-service")
public interface BlogServiceClient {
    @PostMapping("/blog")
    ResponseEntity<?> createBlog(@RequestHeader("UserId") String userId);

    @DeleteMapping("/blog")
    ResponseEntity<?> deleteBlog(@RequestHeader("UserId") String userId);

    @GetMapping("/blog/user")
    ResponseEntity<Long> findBlog(@RequestHeader("UserId") String userId);
}
