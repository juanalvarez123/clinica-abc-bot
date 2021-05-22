package com.clinica.abc.consumers.authorizations;

import com.clinica.abc.config.AuthorizationsFeignConfiguration;
import com.clinica.abc.consumers.authorizations.response.AuthorizationResponse;
import com.clinica.abc.model.AuthorizationDTO;
import java.rmi.NotBoundException;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AuthorizationsConsumer {

  private final AuthorizationsFeignConfiguration authorizationsFeignConfiguration;

  public AuthorizationsConsumer(AuthorizationsFeignConfiguration authorizationsFeignConfiguration) {
    this.authorizationsFeignConfiguration = authorizationsFeignConfiguration;
  }

  public Optional<AuthorizationDTO> getAuthorization(String userId, String authorizationId) {
    try {
      List<AuthorizationResponse> authorizationListResponse = authorizationsFeignConfiguration.getAuthorizationsClient()
        .getAuthorization(userId, authorizationId);

      if (CollectionUtils.isEmpty(authorizationListResponse)) {
        throw new NotBoundException("Empty authorization array");
      }

      return Optional.of(getAuthorizationListDTO(authorizationListResponse));
    } catch (Exception ex) {
      log.warn("Autorización " + authorizationId + " no encontrada para el usuario " + userId);
      return Optional.empty();
    }
  }

  // TODO mejorar
  private AuthorizationDTO getAuthorizationListDTO(List<AuthorizationResponse> authorizationListResponse) {
    AuthorizationResponse authorizationResponse = authorizationListResponse.get(0);
    return AuthorizationDTO.builder()
      .authorizationId(authorizationResponse.getAutorizacionId())
      .userId(authorizationResponse.getNumDoc())
      .timestamp(authorizationResponse.getTimestamp())
      .type(authorizationResponse.getTipo())
      .quantity(authorizationResponse.getCantidad())
      .description(authorizationResponse.getDescripcion())
      .doctor(authorizationResponse.getDoctor())
      .build();
  }
}
