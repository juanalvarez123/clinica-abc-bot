package com.clinica.abc.services;

import java.util.Random;
import org.springframework.stereotype.Service;

@Service
public class ScheduleService {

  public String getSchedule() {
    Random random = new Random();
    int value = random.nextInt(10);

    return value % 2 == 0 ? "Hay disponibiliad para todo el més de Abril"
        : "Lo sentimos, no hay disponibilidad de citas para este mes";
  }

  public String setAppointment(String date) {
    return "Su cita fue agendada para el ".concat(date);
  }
}
