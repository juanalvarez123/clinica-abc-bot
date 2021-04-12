package com.clinica.abc.services;

import java.time.LocalDate;
import org.springframework.stereotype.Service;

@Service
public class ClinicaAbcBotService {

  private final ScheduleService scheduleService;

  public ClinicaAbcBotService(ScheduleService scheduleService) {
    this.scheduleService = scheduleService;
  }

  public String processMessage(String message) {
    switch (message) {
      case "1":
        return scheduleService.getSchedule();

      case "2":
        return scheduleService.setAppointment(LocalDate.now().toString());

      default:
        return "Bienvenido a la Clínica ABC, que te gustaría hacer? ... "
            + "Opción 1 para ver el calendario de disponibilidad ... "
            + "Opción 2 para realizar un agendamiento";
    }
  }

}
