package com.clinica.abc.consumers.users;

import com.clinica.abc.consumers.users.response.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("users-client")
public interface IUsersClient {

  @GetMapping("/user/{userId}")
  UserResponse getUser(@PathVariable String userId);
}
