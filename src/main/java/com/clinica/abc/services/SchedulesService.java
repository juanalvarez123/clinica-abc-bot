package com.clinica.abc.services;

import com.clinica.abc.AvailableSchedulesListQuery.AvailableSchedule;
import com.clinica.abc.SchedulingByUserQuery.SchedulesByUser;
import com.clinica.abc.common.NextStep;
import com.clinica.abc.common.SessionValue;
import com.clinica.abc.consumers.schedules.SchedulesConsumer;
import com.clinica.abc.model.UserDTO;
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

  public String getAvailableSchedules(Session session) {
    session.setAttribute(SessionValue.NEXT_STEP, NextStep.SELECT_DATE);

    this.schedulesConsumer.getAvailableSchedules(session);

    boolean schedulesDatesFinished = (boolean) session.getAttribute(SessionValue.AVAILABLE_SCHEDULES_FINISHED);

    while (!schedulesDatesFinished) {
      schedulesDatesFinished = (boolean) session.getAttribute(SessionValue.AVAILABLE_SCHEDULES_FINISHED);
    }

    Optional<List<AvailableSchedule>> optionalSchedules = (Optional<List<AvailableSchedule>>) session.getAttribute(SessionValue.AVAILABLE_SCHEDULES);

    if (!optionalSchedules.isPresent()) {
      session.setAttribute(SessionValue.NEXT_STEP, NextStep.FAREWELL);
      return "Lo sentimos, no hay fechas disponibles";
    }

    String availableDates = "";
    for (int i=0 ; i < optionalSchedules.get().size() ; i++) {
      AvailableSchedule schedule = optionalSchedules.get().get(i);
      availableDates += "\n" + (i + 1) + ". " + schedule.date() + " " + schedule.hour() + " (" + schedule.medicalprofessional().name() + " " + schedule.medicalprofessional().lastName() + ").";
    }

    return "Las fechas disponibles son:\n" + availableDates + "\n\nPor favor selecciona una opción";
  }

  public String validateAvailableScheduleOption(String availableScheduleOption, Session session) {
    int scheduleOption = 0;
    try {
      scheduleOption = Integer.parseInt(availableScheduleOption);
    } catch (NumberFormatException nfex) {
      session.setAttribute(SessionValue.NEXT_STEP, NextStep.SELECT_DATE);
      return "Lo siento, no pude entenderte, ¿puedes seleccionar una opción por favor?";
    }

    Optional<List<AvailableSchedule>> optionalSchedules = (Optional<List<AvailableSchedule>>) session.getAttribute(SessionValue.AVAILABLE_SCHEDULES);

    if (scheduleOption > 0 && scheduleOption <= optionalSchedules.get().size()) {
      AvailableSchedule selectedSchedule = optionalSchedules.get().get(scheduleOption - 1);

      session.setAttribute(SessionValue.NEXT_STEP, NextStep.CONFIRM_APPOINTMENT);
      session.setAttribute(SessionValue.SELECTED_SCHEDULE, selectedSchedule);
      return "¿Confirmas que tu cita sea registrada para el " + selectedSchedule.date() + " a las " + selectedSchedule.hour() + "? (Si/No)";
    } else {
      session.setAttribute(SessionValue.NEXT_STEP, NextStep.SELECT_DATE);
      return "Lo siento, has elegido una opción no válida, ¿puedes seleccionar una opción por favor?";
    }
  }

  public String setAppointment(Session session) {
    session.setAttribute(SessionValue.NEXT_STEP, NextStep.FAREWELL);

    AvailableSchedule selectedSchedule = (AvailableSchedule) session.getAttribute(SessionValue.SELECTED_SCHEDULE);
    UserDTO user = (UserDTO) session.getAttribute(SessionValue.CURRENT_USER);

    schedulesConsumer.setAppointment(selectedSchedule.id(), String.valueOf(user.getNumeroId()), session);

    boolean setAppointmentFinished = (boolean) session.getAttribute(SessionValue.SET_APPOINTMENT_FINISHED);

    while (!setAppointmentFinished) {
      setAppointmentFinished = (boolean) session.getAttribute(SessionValue.SET_APPOINTMENT_FINISHED);
    }

    boolean setAppointmentFinishedOk = (boolean) session.getAttribute(SessionValue.SET_APPOINTMENT_FINISHED_OK);

    return setAppointmentFinishedOk ?
        "Tu cita ha sido registrada para el " + selectedSchedule.date() + " a las " + selectedSchedule.hour() :
        "Lo sentimos, se presentó un problema registrando tu cita. Por favor llamanos al 01-8000 123-456. Siempre es un placer servirte";
  }

  public String getSchedulesByUser(String userId, Session session) {
    session.setAttribute(SessionValue.NEXT_STEP, NextStep.FAREWELL);

    this.schedulesConsumer.getSchedulesByUser(userId, session);

    boolean schedulesbyUserFinished = (boolean) session.getAttribute(SessionValue.SCHEDULES_BY_USER_FINISHED);

    while (!schedulesbyUserFinished) {
      schedulesbyUserFinished = (boolean) session.getAttribute(SessionValue.SCHEDULES_BY_USER_FINISHED);
    }

    Optional<List<SchedulesByUser>> optionalSchedulesByUser = (Optional<List<SchedulesByUser>>) session.getAttribute(SessionValue.SCHEDULES_BY_USER);

    if (!optionalSchedulesByUser.isPresent()) {
      return "Aún no tienes citas programadas";
    }

    String schedules = "";
    for (int i=0 ; i < optionalSchedulesByUser.get().size() ; i++) {
      SchedulesByUser schedule = optionalSchedulesByUser.get().get(i);
      schedules += "\n" + (i + 1) + ". " + schedule.date() + " " + schedule.hour() + " (" + schedule.medicalprofessional().name() + " " + schedule.medicalprofessional().lastName() + ").";
    }

    return "Tus citas programadas son:\n" + schedules;
  }
}
