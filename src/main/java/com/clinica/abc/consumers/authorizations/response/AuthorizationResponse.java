package com.clinica.abc.consumers.authorizations.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthorizationResponse {

  private String autorizacionId;
  private String numDoc;
  private String timestamp;
  private String tipo;
  private String cantidad;
  private String descripcion;
  private String hash;
  private String doctor;
}
