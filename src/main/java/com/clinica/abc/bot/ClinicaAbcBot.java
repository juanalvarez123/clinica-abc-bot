package com.clinica.abc.bot;

import com.clinica.abc.services.ClinicaAbcBotService;
import java.util.Optional;
import org.apache.shiro.session.Session;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.BotSession;
import org.telegram.telegrambots.session.TelegramLongPollingSessionBot;
import org.telegram.telegrambots.starter.AfterBotRegistration;

@Component
public class ClinicaAbcBot extends TelegramLongPollingSessionBot {

  private final String botToken;

  private final ClinicaAbcBotService clinicaAbcBotService;

  public ClinicaAbcBot(@Value("${bot.token}") String botToken,
      ClinicaAbcBotService clinicaAbcBotService) {
    this.botToken = botToken;
    this.clinicaAbcBotService = clinicaAbcBotService;
  }

  /**
   * Esta función se invocará cuando nuestro bot reciba un mensaje
   */
  @Override
  public void onUpdateReceived(final Update update, final Optional<Session> optionalSession) {
    // Se obtiene el mensaje escrito por el usuario
    final String messageTextReceived = update.getMessage().getText();

    // Se obtiene el id de chat del usuario
    final long chatId = update.getMessage().getChatId();

    String response = clinicaAbcBotService.processMessage(messageTextReceived, optionalSession);

    // Se crea un objeto mensaje
    SendMessage message = SendMessage
        .builder()
        .chatId(String.valueOf(chatId))
        .text(response)
        .build();

    try {
      // Se envía el mensaje
      execute(message);
    } catch (TelegramApiException e) {
      e.printStackTrace();
    }
  }

  @Override
  public String getBotUsername() {
    // Se devuelve el nombre que dimos al bot al crearlo con el BotFather
    return "ClinicaAbcBot";
  }

  @Override
  public String getBotToken() {
    // Se devuelve el token que nos generó el BotFather de nuestro bot
    return botToken;
  }

  /**
   * Sirve para "matar" la instancia del bot
   */
  /*@AfterBotRegistration
  public void afterRegistration(BotSession session) {
    session.stop();
  }*/
}
