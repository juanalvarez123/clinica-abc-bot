package com.clinica.abc.services;

import com.clinica.abc.SchedulesListQuery.SchedulingByDateAndHour;
import com.clinica.abc.common.NextStep;
import com.clinica.abc.common.SessionValue;
import com.clinica.abc.consumers.schedules.SchedulesConsumer;
import com.clinica.abc.util.HourUtil;
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
    session.setAttribute(SessionValue.NEXT_STEP, NextStep.SELECT_DATE);

    this.schedulesConsumer.getSchedules(session);

    boolean schedulesDatesFinished = (boolean) session.getAttribute(SessionValue.SCHEDULES_DATES_FINISHED);

    while (!schedulesDatesFinished) {
      schedulesDatesFinished = (boolean) session.getAttribute(SessionValue.SCHEDULES_DATES_FINISHED);
    }

    Optional<List<SchedulingByDateAndHour>> optionalSchedules = (Optional<List<SchedulingByDateAndHour>>) session.getAttribute(SessionValue.SCHEDULES_DATES);

    if (!optionalSchedules.isPresent()) {
      session.setAttribute(SessionValue.NEXT_STEP, NextStep.FAREWELL);
      return "Lo sentimos, no hay fechas disponibles";
    }

    String availableDates = optionalSchedules.get().stream()
        .map(SchedulingByDateAndHour::date)
        .reduce((date1, date2) -> date1 + ", " + date2)
        .get();

    return "Las fechas disponibles son: " + availableDates + ". Por favor escribe una fecha";
  }

  public String validateDate(String selectedDate, Session session) {
    Optional<List<SchedulingByDateAndHour>> optionalSchedules = (Optional<List<SchedulingByDateAndHour>>) session.getAttribute(SessionValue.SCHEDULES_DATES);

    boolean isAvailableDate = optionalSchedules.get().stream()
        .filter(schedule -> selectedDate.equals(schedule.date()))
        .findFirst()
        .isPresent();

    if (isAvailableDate) {
      session.setAttribute(SessionValue.NEXT_STEP, NextStep.SELECT_HOUR);
      session.setAttribute(SessionValue.SELECTED_DATE, selectedDate);
      return "Por favor escribe la hora de tu cita, debe ser en formato AM/PM, por ejemplo: 8:00 am o 2:00 pm";
    } else {
      session.setAttribute(SessionValue.NEXT_STEP, NextStep.SELECT_DATE);
      return "Lo siento, has elegido una fecha no válida. Por favor escribe una fecha nuevamente";
    }
  }

  public String validateHour(String selectedHour, Session session) {
    boolean isValidHour = HourUtil.isValidHour(selectedHour);

    if (!isValidHour) {
      session.setAttribute(SessionValue.NEXT_STEP, NextStep.SELECT_HOUR);
      return "Lo siento, es una hora no válida. Por favor escribela de nuevo";
    }

    session.setAttribute(SessionValue.NEXT_STEP, NextStep.CONFIRM_APPOINTMENT);
    session.setAttribute(SessionValue.SELECTED_HOUR, selectedHour);

    String selectedDate = (String) session.getAttribute(SessionValue.SELECTED_DATE);

    return "¿Confirmas que tu cita sea registrada para el " + selectedDate + " a las " + selectedHour + "? (Si/No)";
  }

  public String setAppointment(Session session) {
    session.setAttribute(SessionValue.NEXT_STEP, NextStep.FAREWELL);

    String selectedDate = (String) session.getAttribute(SessionValue.SELECTED_DATE);
    String selectedHour = (String) session.getAttribute(SessionValue.SELECTED_HOUR);

    // TODO mejorar el "ID" de la reservación
    schedulesConsumer.setAppointment(selectedDate, selectedHour, "12345", session);

    boolean setAppointmentFinished = (boolean) session.getAttribute(SessionValue.SET_APPOINTMENT_FINISHED);

    while (!setAppointmentFinished) {
      setAppointmentFinished = (boolean) session.getAttribute(SessionValue.SET_APPOINTMENT_FINISHED);
    }

    boolean setAppointmentFinishedOk = (boolean) session.getAttribute(SessionValue.SET_APPOINTMENT_FINISHED_OK);

    return setAppointmentFinishedOk ?
        "Tu cita ha sido registrada para el " + selectedDate + " a las " + selectedHour :
        "Lo sentimos, se presentó un problema registrando tu cita. Por favor llamanos al 01-8000 123-456. Siempre es un placer servirte";
  }
}
