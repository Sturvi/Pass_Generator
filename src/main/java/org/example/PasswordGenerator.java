package org.example;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class PasswordGenerator extends TelegramLongPollingBot {

    HashMap<Long, UserSettings> userSettingsMap;

    public PasswordGenerator() {
        userSettingsMap = new HashMap<>();
    }

    @Override
    public void onUpdateReceived(Update update) {
        Message message;

        if (update.getMessage() == null) {
            message = update.getCallbackQuery().getMessage();
        } else {
            message = update.getMessage();
        }

        if (!userSettingsMap.containsKey(message.getChatId())) {
            userSettingsMap.put(message.getChatId(), new UserSettings());
        }

        if (update.getMessage() == null){
            handleCallback(update.getCallbackQuery());
        } else {
            handleTextMessage(message);
        }


    }

    private void handleCallback (CallbackQuery callbackQuery) {
        String data = callbackQuery.getData();
        Long chatId = callbackQuery.getMessage().getChatId();

        switch (data) {
            case ("length8") -> {
                userSettingsMap.get(chatId).setPasswordLength(8);
            }
            case ("length12") -> {
                userSettingsMap.get(chatId).setPasswordLength(12);
            }
            case ("length16") -> {
                userSettingsMap.get(chatId).setPasswordLength(16);
            }
            case ("specialSymbols") -> {
                userSettingsMap.get(chatId).setSpecialSymbols();
            }
            case ("digits") -> {
                userSettingsMap.get(chatId).setDigits();
            }
            case ("upperCase") -> {
                userSettingsMap.get(chatId).setUpperCaseLetters();
            }
        }
        editKeyboard(callbackQuery.getMessage());
    }


    private void handleTextMessage (Message message){
        String messageText = message.getText();

        Message newMessage = null;

        switch (messageText){
            case ("Сгенерировать пароль") -> {
                newMessage = sendMessageToUser(message, generator(message.getChatId()), null);
            }
            case ("Настройки") -> {
                sendMessageToUser(message, "Выберите пожалуйста настройки для клавиатуры", getInlineKeyboard(message.getChatId()));
            }
            default -> {
                sendMessageToUser(message, "Для генерации пароля прожмите кнопку", null);
            }
        }
    }

    private void editKeyboard (Message message){
        EditMessageText editMessageText = new EditMessageText();

        editMessageText.setChatId(message.getChatId().toString());
        editMessageText.setMessageId(message.getMessageId());
        editMessageText.setText(message.getText());

        editMessageText.setReplyMarkup(getInlineKeyboard(message.getChatId()));

        try {
            execute(editMessageText);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private Message sendMessageToUser(Message message, String messageText, InlineKeyboardMarkup inlineKeyboardMarkup) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(message.getChatId().toString());
        sendMessage.setText(messageText);

        if (inlineKeyboardMarkup == null) {
            keyboard(sendMessage);
        } else {
            sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        }

        Message newMessage = null;

        try {
            newMessage = execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }

        return newMessage;
    }

    private void keyboard (SendMessage sendMessage){
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();

        sendMessage.setReplyMarkup(replyKeyboardMarkup);

        replyKeyboardMarkup.setOneTimeKeyboard(false);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);

        List<KeyboardRow> keyboardRowList = new ArrayList<>();
        KeyboardRow keyboardFirstRow = new KeyboardRow();

        keyboardFirstRow.add(new KeyboardButton("Сгенерировать пароль"));
        keyboardFirstRow.add(new KeyboardButton("Настройки"));

        keyboardRowList.add(keyboardFirstRow);

        replyKeyboardMarkup.setKeyboard(keyboardRowList);
    }

    private InlineKeyboardMarkup getInlineKeyboard (Long chatId) {

        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        keyboard.add(new ArrayList<>());
        keyboard.add(new ArrayList<>());
        keyboard.add(new ArrayList<>());

        String text = userSettingsMap.get(chatId).getPasswordLength() == 8 ? "8 символов ✅" : "8 символов";
        InlineKeyboardButton length8 = new InlineKeyboardButton(text);
        length8.setCallbackData("length8");
        keyboard.get(0).add(length8);

        text = userSettingsMap.get(chatId).getPasswordLength() == 12 ? "12 символов ✅" : "12 символов";
        InlineKeyboardButton length12 = new InlineKeyboardButton(text);
        length12.setCallbackData("length12");
        keyboard.get(0).add(length12);

        text = userSettingsMap.get(chatId).getPasswordLength() == 16 ? "16 символов ✅" : "16 символов";
        InlineKeyboardButton length16 = new InlineKeyboardButton(text);
        length16.setCallbackData("length16");
        keyboard.get(0).add(length16);

        text = userSettingsMap.get(chatId).getUpperCaseLetters() ? "Заглавные буквы ✅" : "Заглавные буквы";
        InlineKeyboardButton upperCase = new InlineKeyboardButton(text);
        upperCase.setCallbackData("upperCase");
        keyboard.get(1).add(upperCase);

        text = userSettingsMap.get(chatId).getDigits() ? "Цифры ✅" : "Цифры";
        InlineKeyboardButton digits = new InlineKeyboardButton(text);
        digits.setCallbackData("digits");
        keyboard.get(2).add(digits);

        text = userSettingsMap.get(chatId).getSpecialSymbols() ? "Спецсимволы ✅" : "Спецсимволы";
        InlineKeyboardButton specialSymbols = new InlineKeyboardButton(text);
        specialSymbols.setCallbackData("specialSymbols");
        keyboard.get(2).add(specialSymbols);

        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        keyboardMarkup.setKeyboard(keyboard);

        return keyboardMarkup;
    }

    private String generator(Long chatId){
        System.out.println("Генерация начата");

        Integer passwordLength = userSettingsMap.get(chatId).getPasswordLength();

        String upperCaseLetters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lowerCaseLetters = "abcdefghijklmnopqrstuvwxyz";
        String digits = "0123456789";
        String specialSymbols = "!@#$%^&*()_-+=<>?";
        String combinedSymbols = lowerCaseLetters;

        if (userSettingsMap.get(chatId).getUpperCaseLetters()) combinedSymbols += upperCaseLetters;
        if (userSettingsMap.get(chatId).getDigits()) combinedSymbols += digits;
        if (userSettingsMap.get(chatId).getSpecialSymbols()) combinedSymbols += specialSymbols;

        Random random = new SecureRandom();

        StringBuilder password = new StringBuilder();

        for (int i = 0; i < passwordLength - userSettingsMap.get(chatId).getTrueCount(); i++) {
            password.append(combinedSymbols.charAt(random.nextInt(combinedSymbols.length())));
        }

        password.insert(random.nextInt(password.length()), lowerCaseLetters.charAt(random.nextInt(lowerCaseLetters.length())));

        if (userSettingsMap.get(chatId).getUpperCaseLetters()) {
            password.insert(random.nextInt(password.length()), upperCaseLetters.charAt(random.nextInt(upperCaseLetters.length())));
        }
        if (userSettingsMap.get(chatId).getDigits()) {
            password.insert(random.nextInt(password.length()), digits.charAt(random.nextInt(digits.length())));
        }
        if (userSettingsMap.get(chatId).getSpecialSymbols()) {
            password.insert(random.nextInt(password.length()), specialSymbols.charAt(random.nextInt(specialSymbols.length())));
        }

        System.out.println("Пароль сгенерирован");
        return password.toString();
    }

    @Override
    public String getBotUsername() {
        return "Password_Generator_on_Java_BOT";
    }

    @Override
    public String getBotToken() {
        return "6148069810:AAE-7SnAgq3i5uBzHm_h66253lA1ooY6FOM";
    }
}
