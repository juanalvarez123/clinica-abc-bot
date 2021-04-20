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

  private final UsersService usersService;

  public SchedulesService(SchedulesConsumer schedulesConsumer,
      UsersService usersService) {
    this.schedulesConsumer = schedulesConsumer;
    this.usersService = usersService;
  }

  public String getAvailableSchedules(Session session) {

    this.schedulesConsumer.getAvailableSchedules(session);

    boolean schedulesDatesFinished = (boolean) session.getAttribute(SessionValue.AVAILABLE_SCHEDULES_FINISHED);

    while (!schedulesDatesFinished) {
      schedulesDatesFinished = (boolean) session.getAttribute(SessionValue.AVAILABLE_SCHEDULES_FINISHED);
    }

    Optional<List<AvailableSchedule>> optionalSchedules = (Optional<List<AvailableSchedule>>) session.getAttribute(SessionValue.AVAILABLE_SCHEDULES);

    if (!optionalSchedules.isPresent()) {
      session.setAttribute(SessionValue.NEXT_STEP, NextStep.USER_FOUND);
      session.setAttribute(SessionValue.SHOW_INTRO, false);

      return "Lo sentimos, no hay fechas disponibles.\n\n" + usersService.getMenuOptions(session);
    }

    String availableDates = "";
    int index = 0;
    for ( ; index < optionalSchedules.get().size() ; index++) {
      AvailableSchedule schedule = optionalSchedules.get().get(index);
      availableDates += "\n" + (index + 1) + ". " + schedule.date() + " " + schedule.hour() + " (" + schedule.medicalprofessional().name() + " " + schedule.medicalprofessional().lastName() + ").";
    }

    availableDates += "\n" + (index + 1) + ". Regresar.";

    session.setAttribute(SessionValue.NEXT_STEP, NextStep.SELECT_DATE);

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

    int returnOption = optionalSchedules.get().size() + 1;

    if (scheduleOption == returnOption) {
      // Si selecciona la última opción es porque desea regresar al menú de las opciones
      session.setAttribute(SessionValue.NEXT_STEP, NextStep.USER_FOUND);
      session.setAttribute(SessionValue.SHOW_INTRO, false);

      return usersService.getMenuOptions(session);
    } else if (scheduleOption > 0 && scheduleOption <= returnOption) {
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
    session.setAttribute(SessionValue.NEXT_STEP, NextStep.USER_FOUND);
    session.setAttribute(SessionValue.SHOW_INTRO, false);

    AvailableSchedule selectedSchedule = (AvailableSchedule) session.getAttribute(SessionValue.SELECTED_SCHEDULE);
    UserDTO user = (UserDTO) session.getAttribute(SessionValue.CURRENT_USER);

    schedulesConsumer.setAppointment(selectedSchedule.id(), String.valueOf(user.getNumeroId()), session);

    boolean setAppointmentFinished = (boolean) session.getAttribute(SessionValue.SET_APPOINTMENT_FINISHED);

    while (!setAppointmentFinished) {
      setAppointmentFinished = (boolean) session.getAttribute(SessionValue.SET_APPOINTMENT_FINISHED);
    }

    boolean setAppointmentFinishedOk = (boolean) session.getAttribute(SessionValue.SET_APPOINTMENT_FINISHED_OK);

    if (setAppointmentFinishedOk) {
      return "Tu cita ha sido registrada para el " + selectedSchedule.date() + " a las " + selectedSchedule.hour()
          + ".\n\n" + usersService.getMenuOptions(session);
    } else {
      return "Lo sentimos, se presentó un problema registrando tu cita. Por favor llamanos al 01-8000 123-456."
          + "\n\n" + usersService.getMenuOptions(session);
    }
  }

  public String getSchedulesByUser(String userId, Session session) {
    session.setAttribute(SessionValue.NEXT_STEP, NextStep.USER_FOUND);
    session.setAttribute(SessionValue.SHOW_INTRO, false);

    this.schedulesConsumer.getSchedulesByUser(userId, session);

    boolean schedulesbyUserFinished = (boolean) session.getAttribute(SessionValue.SCHEDULES_BY_USER_FINISHED);

    while (!schedulesbyUserFinished) {
      schedulesbyUserFinished = (boolean) session.getAttribute(SessionValue.SCHEDULES_BY_USER_FINISHED);
    }

    Optional<List<SchedulesByUser>> optionalSchedulesByUser = (Optional<List<SchedulesByUser>>) session.getAttribute(SessionValue.SCHEDULES_BY_USER);

    if (!optionalSchedulesByUser.isPresent()) {
      return "Aún no tienes citas programadas.\n\n" + usersService.getMenuOptions(session);
    }

    String schedules = "";
    for (int index=0 ; index < optionalSchedulesByUser.get().size() ; index++) {
      SchedulesByUser schedule = optionalSchedulesByUser.get().get(index);
      schedules += "\n" + (index + 1) + ". " + schedule.date() + " " + schedule.hour() + " (" + schedule.medicalprofessional().name() + " " + schedule.medicalprofessional().lastName() + ").";
    }

    return "Tus citas programadas son:\n" + schedules + "\n\n" + usersService.getMenuOptions(session);
  }
}
