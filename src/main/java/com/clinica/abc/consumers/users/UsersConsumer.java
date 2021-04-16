package com.clinica.abc.consumers.users;

import com.clinica.abc.config.UsersFeignConfiguration;
import com.clinica.abc.consumers.users.response.UserResponse;
import com.clinica.abc.model.UserDTO;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UsersConsumer {

  private final UsersFeignConfiguration usersFeignConfiguration;

  public UsersConsumer(UsersFeignConfiguration usersFeignConfiguration) {
    this.usersFeignConfiguration = usersFeignConfiguration;
  }

  public Optional<UserDTO> getUser(String userId) {
    try {
      UserResponse userResponse = usersFeignConfiguration.getUsersClient().getUser(userId);
      return Optional.of(getUserDTO(userResponse));
    } catch (Exception ex) {
      log.warn("Usuario " + userId + " no encontrado");
      return Optional.empty();
    }
  }

  private UserDTO getUserDTO(UserResponse userResponse) {
    return UserDTO.builder()
        .idUsuario(userResponse.getIdUsuario())
        .tipoId(userResponse.getTipoId())
        .numeroId(userResponse.getNumeroId())
        .nombres(userResponse.getNombres())
        .apellidos(userResponse.getApellidos())
        .genero(userResponse.getGenero())
        .fechaNacimiento(userResponse.getFechaNacimiento())
        .build();
  }

}
