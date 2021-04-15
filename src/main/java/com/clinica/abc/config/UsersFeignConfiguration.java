package com.clinica.abc.config;

import com.clinica.abc.consumers.users.IUsersClient;
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
public class UsersFeignConfiguration {

  @Getter
  private IUsersClient usersClient;

  public UsersFeignConfiguration(
      Decoder decoder,
      Encoder encoder,
      Contract contract,
      @Value("${users.url}") String usersUrl) {
    this.usersClient =
        Feign.builder()
            .encoder(encoder)
            .decoder(decoder)
            .contract(contract)
            .target(IUsersClient.class, usersUrl);
  }
}
