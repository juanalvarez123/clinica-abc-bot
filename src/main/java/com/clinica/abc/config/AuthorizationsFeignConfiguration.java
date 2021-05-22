package com.clinica.abc.config;

import com.clinica.abc.consumers.authorizations.IAuthorizationsClient;
import feign.Contract;
import feign.Feign;
import feign.codec.Decoder;
import feign.codec.Encoder;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.openfeign.FeignClientsConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;

@Component
@Import(FeignClientsConfiguration.class)
public class AuthorizationsFeignConfiguration {

  @Getter
  private IAuthorizationsClient authorizationsClient;

  public AuthorizationsFeignConfiguration(
      Decoder decoder,
      Encoder encoder,
      Contract contract,
      @Value("${authorizations.url}") String authorizationsUrl) {
    this.authorizationsClient =
        Feign.builder()
            .encoder(encoder)
            .decoder(decoder)
            .contract(contract)
            .target(IAuthorizationsClient.class, authorizationsUrl);
  }
}
