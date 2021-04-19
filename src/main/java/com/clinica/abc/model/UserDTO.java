package com.clinica.abc.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

  private String idUsuario;
  private String tipoId;
  private int numeroId;
  private String nombres;
  private String apellidos;
  private String genero;
  private String fechaNacimiento;

  public String getFullName() {
      return nombres.concat(" ").concat(apellidos);
  }
}
