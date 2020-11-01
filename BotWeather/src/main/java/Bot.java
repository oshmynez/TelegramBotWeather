import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.send.SendPhoto;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.io.Console;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Bot extends TelegramLongPollingBot {

    public static ArrayList<String> listCommandsBot;
    private String defaultCity;

    static {
        listCommandsBot = new ArrayList<String>();
        listCommandsBot.add("/help - Get list of allCommands");
        listCommandsBot.add("/setcitydefault - Set up City for Default output Weather");
        listCommandsBot.add("/weather - Output Weather's Сity Default");
        listCommandsBot.add("/getdefaultcity - Пet the default city value");
        listCommandsBot.add("SomeСity - Enter instead 'SomeСity' desired city for get data about Weather");
    }


    public static void main(String[] args) {

        ApiContextInitializer.init();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();

        try {
            telegramBotsApi.registerBot(new Bot());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void sendMsg(Message message, String text) {
        SendMessage sendMessage = SetUpMessage(message);
        sendMessage.setText(text);

        try {
            setButtons(sendMessage);
            sendMessage(sendMessage);

        } catch (TelegramApiException e) {

            e.printStackTrace();
        }
    }
    
    public SendMessage SetUpMessage(Message message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableHtml(true);
        sendMessage.setChatId(message.getChatId().toString());
        sendMessage.setReplyToMessageId(message.getMessageId());
        return sendMessage;

    }

    public void sendPhotoMsg(Message message, Model model) {
        SendPhoto sendPhotoRequest = new SendPhoto();
        sendPhotoRequest.setChatId(message.getChatId().toString());
        sendPhotoRequest.setPhoto("http://openweathermap.org/img/w/" + model.getIconPath() + ".png");
        try {
            sendPhoto(sendPhotoRequest);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void sendListCommads(Message message) {
        SendMessage sendMessage = SetUpMessage(message);
        StringBuilder result = new StringBuilder();
        for (String item : listCommandsBot) {
            result.append(item).append('\n');
        }
        sendMessage.setText(String.valueOf(result));
        try {
            sendMessage(sendMessage);
        } catch (TelegramApiException e) {

            e.printStackTrace();
        }

    }

    public void onUpdateReceived(Update update) {
        Model model = new Model();
        Message message = update.getMessage();
        if (message.getText().contains("//")) {
                defaultCity = message.getText().substring(2);
                sendMsg(message, "Default city :" + defaultCity + " successfully set");
        } else if (message.hasText()) {
            switch (message.getText()) {
                case "/help":
                    sendListCommads(message);
                    break;
                case "/getDefaultCity":
                    sendMsg(message,"Default city: "+ defaultCity);
                    break;
                case "/setCityDefault":
                    sendMsg(message, "Input сity for default request, Format: //сity");
                    break;
                case "/weather":
                    if (defaultCity != null) {
                        try {
                            sendMsg(message, Weather.getWeather(defaultCity, model));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        sendMsg(message, "Default city is't set");
                    }

                    sendPhotoMsg(message, model);
                    break;
                default:
                    try {
                        sendMsg(message, Weather.getWeather(message.getText(), model));
                        sendPhotoMsg(message, model);

                    } catch (IOException e) {
                        sendMsg(message, "Такой город не найден");
                    }
            }
        }
    }

    public void setButtons(SendMessage sendMessage) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);
        replyKeyboardMarkup.setOneTimeKeyboard(true);

        List<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow keyboardFirstRow = new KeyboardRow();
        KeyboardRow keyboardSecondRow = new KeyboardRow();

        keyboardFirstRow.add(new KeyboardButton("/help"));
        keyboardFirstRow.add(new KeyboardButton("/setcitydefault"));

        keyboardSecondRow.add(new KeyboardButton("/weather"));
        keyboardSecondRow.add(new KeyboardButton("/getdefaultcity"));

        keyboardRows.add(keyboardFirstRow);
        keyboardRows.add(keyboardSecondRow);
        replyKeyboardMarkup.setKeyboard(keyboardRows);

    }

    public String getBotUsername() {
        return "BotWeather";
    }

    public String getBotToken() {
        return "1349625825:AAE6rmqJG-mYh3jTU2m0dLokW38wYNQ7TY4";
    }
}
