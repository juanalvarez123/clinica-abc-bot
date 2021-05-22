package com.clinica.abc.services;

import com.clinica.abc.common.NextStep;
import com.clinica.abc.common.SessionValue;
import com.clinica.abc.consumers.users.UsersConsumer;
import com.clinica.abc.model.UserDTO;
import java.util.Optional;
import org.apache.shiro.session.Session;
import org.springframework.stereotype.Service;

@Service
public class UsersService {

  private final UsersConsumer usersConsumer;

  public UsersService(UsersConsumer usersConsumer) {
    this.usersConsumer = usersConsumer;
  }

  private static final String OPTIONS =
      "1. Realizar un agendamiento."
          + "\n2. Consultar tus citas."
          + "\n3. Consulta tus autorizaciones."
          + "\n4. Salir.";

  public String getMenuOptions(Session session) {
    session.setAttribute(SessionValue.NEXT_STEP, NextStep.DECISION);

    UserDTO user = (UserDTO) session.getAttribute(SessionValue.CURRENT_USER);
    boolean showIntro = (boolean) session.getAttribute(SessionValue.SHOW_INTRO);

    return showIntro ?
        "Hola " + user.getFullName() + ", ¿que te gustaría hacer?\n\n" + OPTIONS :
        "¿Que te gustaría hacer?\n\n" + OPTIONS;
  }

  public Optional<UserDTO> getUser(String userId) {
    return usersConsumer.getUser(userId);
  }
}
