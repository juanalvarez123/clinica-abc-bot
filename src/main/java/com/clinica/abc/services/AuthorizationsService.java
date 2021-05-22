package com.clinica.abc.services;

import com.clinica.abc.common.NextStep;
import com.clinica.abc.common.SessionValue;
import com.clinica.abc.consumers.authorizations.AuthorizationsConsumer;
import com.clinica.abc.model.AuthorizationDTO;
import java.util.Optional;
import org.apache.shiro.session.Session;
import org.springframework.stereotype.Service;

@Service
public class AuthorizationsService {

  private final AuthorizationsConsumer authorizationsConsumer;

  private final UsersService usersService;

  public AuthorizationsService(AuthorizationsConsumer authorizationsConsumer,
    UsersService usersService) {
    this.authorizationsConsumer = authorizationsConsumer;
    this.usersService = usersService;
  }

  public String queryForAuthorization(String userId, String authorizationId, Session session) {
    session.setAttribute(SessionValue.NEXT_STEP, NextStep.USER_FOUND);
    session.setAttribute(SessionValue.SHOW_INTRO, false);

    Optional<AuthorizationDTO> authorization = authorizationsConsumer.getAuthorization(userId, authorizationId);

    if (authorization.isPresent()) {
      return "La autorización es válida, esta es la información: " + authorization.get().getInformation() +
        ".\n\n" + usersService.getMenuOptions(session);
    } else {

      return "Lo sentimos, no tienes registrada esa autorización.\n\n" + usersService.getMenuOptions(session);
    }
  }
}
