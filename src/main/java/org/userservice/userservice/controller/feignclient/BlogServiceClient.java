package org.userservice.userservice.controller.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


@FeignClient(name = "blogService", url = "http://localhost:8081")
public interface BlogServiceClient {
    @PostMapping("/blogs")
    ResponseEntity<?> createBlog(@RequestParam("userId") String userId);
}
