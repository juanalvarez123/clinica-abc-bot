package com.clinica.abc.consumers.authorizations;

import com.clinica.abc.consumers.authorizations.response.AuthorizationResponse;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("authorizations-client")
public interface IAuthorizationsClient {

  @GetMapping
  List<AuthorizationResponse> getAuthorization(@RequestParam("numDoc") String userId,
    @RequestParam("autorizacionId") String authorizationId);
}
