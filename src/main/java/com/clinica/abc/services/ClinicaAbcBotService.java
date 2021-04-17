package com.clinica.abc.services;

import com.clinica.abc.common.NextStep;
import com.clinica.abc.common.SessionValue;
import com.clinica.abc.consumers.users.UsersConsumer;
import com.clinica.abc.model.UserDTO;
import java.util.Optional;
import org.apache.commons.lang3.EnumUtils;
import org.apache.shiro.session.Session;
import org.springframework.stereotype.Service;

@Service
public class ClinicaAbcBotService {

  private final SchedulesService schedulesService;

  private final UsersConsumer usersConsumer;

  public ClinicaAbcBotService(SchedulesService schedulesService,
      UsersConsumer usersConsumer) {
    this.schedulesService = schedulesService;
    this.usersConsumer = usersConsumer;
  }

  public String processMessage(String message, Optional<Session> optionalSession) {

    if (optionalSession.isPresent()) {
      Session session = optionalSession.get();
      String step = String.valueOf(session.getAttribute(SessionValue.NEXT_STEP));

      if (EnumUtils.getEnum(NextStep.class, step) == null) {
        session.setAttribute(SessionValue.NEXT_STEP, NextStep.WELCOME);
      }

      NextStep nextStep = EnumUtils.getEnum(NextStep.class, String.valueOf(session.getAttribute(SessionValue.NEXT_STEP)));
      return processMessage(message, nextStep, session);
    } else {
      return processMessage("", NextStep.WELCOME, null);
    }
  }

  private String processMessage(String message, NextStep nextStep, Session session) {

    switch (nextStep) {
      case WELCOME:
        session.setAttribute(SessionValue.NEXT_STEP, NextStep.FIND_USER);
        return "Bienvenido a la Clínica ABC, por favor digita tu número de identificación";

      case FIND_USER:
        Optional<UserDTO> optionalUser = usersConsumer.getUser(message);
        if (optionalUser.isPresent()) {
          session.setAttribute(SessionValue.NEXT_STEP, NextStep.USER_FOUND);
          session.setAttribute(SessionValue.CURRENT_USER, optionalUser.get());
          return processMessage(message, NextStep.USER_FOUND, session);
        } else {
          session.setAttribute(SessionValue.NEXT_STEP, NextStep.USER_NOT_FOUND);
          return processMessage(message, NextStep.USER_NOT_FOUND, session);
        }

      case USER_FOUND:
        session.setAttribute(SessionValue.NEXT_STEP, NextStep.DECISION);
        UserDTO user = (UserDTO) session.getAttribute(SessionValue.CURRENT_USER);
        return "Hola " + user.getFullName() + ", ¿que te gustaría hacer? ... "
            + "Opción 1 para realizar un agendamiento ... "
            + "Opción 2 para salir";

      case USER_NOT_FOUND:
        session.stop();
        return "Lo sentimos, no pudimos encontrarte. Por favor llamanos al 01-8000 123-456. Siempre es un placer servirte";

      case DECISION:
        if (message.equals("1")) {
          return schedulesService.getSchedules(session);
        } else if (message.equals("2")) {
          session.setAttribute(SessionValue.NEXT_STEP, NextStep.FAREWELL);
          return processMessage(message, NextStep.FAREWELL, session);
        } else {
          return processMessage(message, NextStep.DEFAULT, session);
        }

      case SELECT_DATE:
        return schedulesService.validateDate(message, session);

      case SELECT_HOUR:
        return schedulesService.validateHour(message, session);

      case CONFIRM_APPOINTMENT:
        if (message.trim().equalsIgnoreCase("si")) {
          return schedulesService.setAppointment(session);
        }

        session.setAttribute(SessionValue.NEXT_STEP, NextStep.DECISION);
        return processMessage("1", NextStep.DECISION, session);

      case FAREWELL:
        session.stop();
        return "Siempre es un placer servirte";

      default:
        session.setAttribute(SessionValue.NEXT_STEP, NextStep.DECISION);
        return "Lo siento, no pude entenderte, ¿me puedes repetir por favor?";
    }
  }
}
