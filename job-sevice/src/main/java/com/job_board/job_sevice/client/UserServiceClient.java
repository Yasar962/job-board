package com.job_board.job_sevice.client;

import com.job_board.job_sevice.Dtos.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-sevice", url = "http://localhost:8081")
public interface UserServiceClient {
    @GetMapping("/api/users/{userId}")
    UserDto getUserById(@PathVariable Long userId);
}