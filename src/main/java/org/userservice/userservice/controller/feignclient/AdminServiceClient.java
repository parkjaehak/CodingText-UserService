package org.userservice.userservice.controller.feignclient;

import org.springframework.cloud.openfeign.FeignClient;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.userservice.userservice.dto.adminclient.AnnounceResponse;

@FeignClient(name = "admin-service")
public interface AdminServiceClient {

    @GetMapping("/admins/announce")
    ResponseEntity<Page<AnnounceResponse>> getAnnouncements(@RequestParam("page") int page, @RequestParam("size") int size);
}
