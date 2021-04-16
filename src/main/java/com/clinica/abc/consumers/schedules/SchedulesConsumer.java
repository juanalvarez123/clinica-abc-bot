package com.clinica.abc.consumers.schedules;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.ApolloClient;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.clinica.abc.LaunchListQuery;
import com.clinica.abc.common.SessionValue;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
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
    this.apolloClient.query(new LaunchListQuery())
        .enqueue(new ApolloCall.Callback<LaunchListQuery.Data>() {
          @Override
          public void onResponse(@NotNull Response<LaunchListQuery.Data> response) {
            session.setAttribute(SessionValue.SCHEDULES_DATES, Optional.of(response.getData().launches().launches()));
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
}
