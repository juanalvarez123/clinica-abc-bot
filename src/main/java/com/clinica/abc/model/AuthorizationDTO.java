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
public class AuthorizationDTO {

  private String authorizationId;
  private String userId;
  private String timestamp;
  private String type;
  private String quantity;
  private String description;
  private String doctor;

  public String getInformation() {
    return "Autorización ID: " + authorizationId + ", Doctor: " + doctor + ", Especialidad: " + description;
  }
}
