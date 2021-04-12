package com.clinica.abc.services;

import com.clinica.abc.common.NextStep;
import java.time.LocalDate;
import java.util.Optional;
import org.apache.commons.lang3.EnumUtils;
import org.apache.shiro.session.Session;
import org.springframework.stereotype.Service;

@Service
public class ClinicaAbcBotService {

  private static final String NEXT_STEP = "NEXT_STEP";

  private final ScheduleService scheduleService;

  public ClinicaAbcBotService(ScheduleService scheduleService) {
    this.scheduleService = scheduleService;
  }

  public String processMessage(String message, Optional<Session> optionalSession) {

    if (optionalSession.isPresent()) {
      Session session = optionalSession.get();
      String step = String.valueOf(session.getAttribute(NEXT_STEP));

      if (EnumUtils.getEnum(NextStep.class, step) == null) {
        session.setAttribute(NEXT_STEP, NextStep.WELCOME);
      }

      NextStep nextStep = EnumUtils.getEnum(NextStep.class, String.valueOf(session.getAttribute(NEXT_STEP)));
      return processMessage(message, nextStep, session);
    } else {
      return processMessage("", NextStep.WELCOME, null);
    }
  }

  private String processMessage(String message, NextStep nextStep, Session session) {

    switch (nextStep) {
      case WELCOME:
        session.setAttribute(NEXT_STEP, NextStep.DECISION);
        return "Bienvenido a la Clínica ABC, que te gustaría hacer? ... "
            + "Opción 1 para ver el calendario de disponibilidad ... "
            + "Opción 2 para realizar un agendamiento ... "
            + "Opción 3 para salir";

      case DECISION:
        session.setAttribute(NEXT_STEP, NextStep.FAREWELL);
        if (message.equals("1")) {
          return scheduleService.getSchedule();
        } else if (message.equals("2")) {
          return scheduleService.setAppointment(LocalDate.now().toString());
        } else if (message.equals("3")) {
          return processMessage(message, NextStep.FAREWELL, session);
        } else {
          return processMessage(message, NextStep.DEFAULT, session);
        }

      case FAREWELL:
        session.stop();
        return "Fue un placer servirte";

      default:
        session.setAttribute(NEXT_STEP, NextStep.DECISION);
        return "Lo siento, no pude entenderte, ¿Me podrías repetir por favor?";
    }
  }
}
