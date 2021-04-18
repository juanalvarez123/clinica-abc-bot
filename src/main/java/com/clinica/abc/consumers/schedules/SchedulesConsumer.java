package com.clinica.abc.consumers.schedules;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.ApolloCall.Callback;
import com.apollographql.apollo.ApolloClient;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.clinica.abc.AvailableSchedulesListQuery;
import com.clinica.abc.CreateScheduleMutation;
import com.clinica.abc.CreateScheduleMutation.Data;
import com.clinica.abc.SchedulingByUserQuery;
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

  public void getAvailableSchedules(Session session) {
    session.setAttribute(SessionValue.AVAILABLE_SCHEDULES_FINISHED, false);
    this.apolloClient.query(new AvailableSchedulesListQuery())
        .enqueue(new ApolloCall.Callback<AvailableSchedulesListQuery.Data>() {
          @Override
          public void onResponse(@NotNull Response<AvailableSchedulesListQuery.Data> response) {
            if (CollectionUtils.isEmpty(response.getData().availableSchedules())) {
              session.setAttribute(SessionValue.AVAILABLE_SCHEDULES, Optional.empty());
            } else {
              session.setAttribute(SessionValue.AVAILABLE_SCHEDULES, Optional.of(response.getData().availableSchedules()));
            }

            session.setAttribute(SessionValue.AVAILABLE_SCHEDULES_FINISHED, true);
          }

          @Override
          public void onFailure(@NotNull ApolloException e) {
            log.error("Error", e);
            session.setAttribute(SessionValue.AVAILABLE_SCHEDULES, Optional.empty());
            session.setAttribute(SessionValue.AVAILABLE_SCHEDULES_FINISHED, true);
          }
        });
  }

  public void setAppointment(String scheduleId, String userId, Session session) {
    session.setAttribute(SessionValue.SET_APPOINTMENT_FINISHED, false);
    this.apolloClient.mutate(CreateScheduleMutation.builder()
        .scheduleId(scheduleId)
        .userId(userId)
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

  public void getSchedulesByUser(String userId, Session session) {
    session.setAttribute(SessionValue.SCHEDULES_BY_USER_FINISHED, false);
    this.apolloClient.query(new SchedulingByUserQuery(userId))
        .enqueue(new ApolloCall.Callback<SchedulingByUserQuery.Data>() {
          @Override
          public void onResponse(@NotNull Response<SchedulingByUserQuery.Data> response) {
            if (CollectionUtils.isEmpty(response.getData().schedulesByUser())) {
              session.setAttribute(SessionValue.SCHEDULES_BY_USER, Optional.empty());
            } else {
              session.setAttribute(SessionValue.SCHEDULES_BY_USER, Optional.of(response.getData().schedulesByUser()));
            }

            session.setAttribute(SessionValue.SCHEDULES_BY_USER_FINISHED, true);
          }

          @Override
          public void onFailure(@NotNull ApolloException e) {
            log.error("Error", e);
            session.setAttribute(SessionValue.SCHEDULES_BY_USER, Optional.empty());
            session.setAttribute(SessionValue.SCHEDULES_BY_USER_FINISHED, true);
          }
        });
  }
}
