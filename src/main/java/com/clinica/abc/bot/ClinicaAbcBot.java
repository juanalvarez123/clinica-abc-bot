package com.clinica.abc.bot;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class ClinicaAbcBot extends TelegramLongPollingBot {

  @Value("${bot.token}")
  private String botToken;

  /**
   * Esta función se invocará cuando nuestro bot reciba un mensaje
   */
  @Override
  public void onUpdateReceived(final Update update) {
    // Se obtiene el mensaje escrito por el usuario
    final String messageTextReceived = update.getMessage().getText();

    // Se obtiene el id de chat del usuario
    final long chatId = update.getMessage().getChatId();

    // Se crea un objeto mensaje
    SendMessage message = SendMessage
        .builder()
        .chatId(String.valueOf(chatId))
        .text(messageTextReceived)
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
}
