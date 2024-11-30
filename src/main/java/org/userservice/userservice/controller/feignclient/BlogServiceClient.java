package org.userservice.userservice.controller.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.userservice.userservice.dto.blogclient.BlogServiceResponse;


@FeignClient(name = "blog-service")
public interface BlogServiceClient {
    @PostMapping("/blog")
    ResponseEntity<?> createBlog(@RequestHeader("UserId") String userId);

    @DeleteMapping("/blog")
    ResponseEntity<?> deleteBlog(@RequestHeader("UserId") String userId);

    @GetMapping("/blog/user")
    ResponseEntity<Long> findBlogId(@RequestHeader("UserId") String userId);

    @GetMapping("/blog/home/mainContent")
    ResponseEntity<BlogServiceResponse> findBlogIdAndIntro(@RequestHeader("UserId") String userId);
}
