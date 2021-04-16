package com.clinica.abc.services;

import com.clinica.abc.LaunchListQuery.Launch;
import com.clinica.abc.common.SessionValue;
import com.clinica.abc.consumers.schedules.SchedulesConsumer;
import java.util.List;
import java.util.Optional;
import org.apache.shiro.session.Session;
import org.springframework.stereotype.Service;

@Service
public class SchedulesService {

  private final SchedulesConsumer schedulesConsumer;

  public SchedulesService(SchedulesConsumer schedulesConsumer) {
    this.schedulesConsumer = schedulesConsumer;
  }

  public String getSchedules(Session session) {
    this.schedulesConsumer.getSchedules(session);

    boolean schedulesDatesFinished = (boolean) session.getAttribute(SessionValue.SCHEDULES_DATES_FINISHED);

    while (!schedulesDatesFinished) {
      schedulesDatesFinished = (boolean) session.getAttribute(SessionValue.SCHEDULES_DATES_FINISHED);
    }

    Optional<List<Launch>> optionalLaunches = (Optional<List<Launch>>) session.getAttribute(SessionValue.SCHEDULES_DATES);

    if (!optionalLaunches.isPresent()) {
      return "Lo sentimos, no hay fechas disponibles";
    }

    String availableDates = optionalLaunches.get().stream()
        .map(date -> date.site())
        .reduce((date1, date2) -> date1 + ", " + date2)
        .get();

    return "Las fechas disponibles son: " + availableDates + ". Por favor elige una";
  }

  public String setAppointment(String date) {
    return "Su cita fue agendada para el ".concat(date);
  }
}
