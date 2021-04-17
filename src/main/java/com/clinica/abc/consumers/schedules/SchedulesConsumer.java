package com.clinica.abc.consumers.schedules;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.ApolloCall.Callback;
import com.apollographql.apollo.ApolloClient;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.clinica.abc.CreateScheduleMutation;
import com.clinica.abc.CreateScheduleMutation.Data;
import com.clinica.abc.SchedulesListQuery;
import com.clinica.abc.common.AppointmentStatus;
import com.clinica.abc.common.SessionValue;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.shiro.session.Session;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SchedulesConsumer {

  private final ApolloClient apolloClient;

  public SchedulesConsumer(@Value("${schedules.url}") String schedulesUrl) {
    this.apolloClient = ApolloClient.builder()
        .serverUrl(schedulesUrl)
        .build();
  }

  public void getSchedules(Session session) {
    session.setAttribute(SessionValue.SCHEDULES_DATES_FINISHED, false);
    this.apolloClient.query(new SchedulesListQuery())
        .enqueue(new ApolloCall.Callback<SchedulesListQuery.Data>() {
          @Override
          public void onResponse(@NotNull Response<SchedulesListQuery.Data> response) {
            if (CollectionUtils.isEmpty(response.getData().SchedulingByDateAndHour())) {
              session.setAttribute(SessionValue.SCHEDULES_DATES, Optional.empty());
            } else {
              session.setAttribute(SessionValue.SCHEDULES_DATES, Optional.of(response.getData().SchedulingByDateAndHour()));
            }

            session.setAttribute(SessionValue.SCHEDULES_DATES_FINISHED, true);
          }

          @Override
          public void onFailure(@NotNull ApolloException e) {
            log.error("Error", e);
            session.setAttribute(SessionValue.SCHEDULES_DATES, Optional.empty());
            session.setAttribute(SessionValue.SCHEDULES_DATES_FINISHED, true);
          }
        });
  }

  public void setAppointment(String date, String hour, String idReservation, Session session) {
    session.setAttribute(SessionValue.SET_APPOINTMENT_FINISHED, false);
    this.apolloClient.mutate(CreateScheduleMutation.builder()
        .date(date)
        .hour(hour)
        .idReservation(idReservation)
        .state(AppointmentStatus.ACTIVE.name())
        .build()).enqueue(new Callback<Data>() {
      @Override
      public void onResponse(@NotNull Response<Data> response) {
        session.setAttribute(SessionValue.SET_APPOINTMENT_FINISHED_OK, true);
        session.setAttribute(SessionValue.SET_APPOINTMENT_FINISHED, true);
      }

      @Override
      public void onFailure(@NotNull ApolloException e) {
        log.error("Error", e);
        session.setAttribute(SessionValue.SET_APPOINTMENT_FINISHED_OK, false);
        session.setAttribute(SessionValue.SET_APPOINTMENT_FINISHED, true);
      }
    });
  }
}
