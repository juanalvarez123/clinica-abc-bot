package com.clinica.abc.consumers.users.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

  private String idUsuario;
  private String tipoId;
  private int numeroId;
  private String nombres;
  private String apellidos;
  private String genero;
  private String fechaNacimiento;
}
