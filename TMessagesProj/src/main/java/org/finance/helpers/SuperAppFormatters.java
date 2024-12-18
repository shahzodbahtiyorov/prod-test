package org.finance.helpers;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/** @noinspection ALL*/
public class SuperAppFormatters {


    /**
     * this method formats like this :
     * <p> <b>16 digits of card number -> **** 0000</b> </p>
     */
    public static String getCardLastFourDigits (String cardNumber) {
        String digitsOnly = cardNumber.replaceAll("\\D", "");

        if (digitsOnly.length() < 4) {
            throw new IllegalArgumentException("Card number must have at least 4 digits");
        }
        String lastFourDigits = digitsOnly.substring(digitsOnly.length() - 4);
        String maskedPart = "****";
        return maskedPart + " " + lastFourDigits;
    }
    public static String getCurrencyName(int currencyCode) {
        switch (currencyCode) {
            case 860:
                return "UZS";
            case 643:
                return "RUB";
            default:
                return "Unknown Currency";
        }
    }
    public static String maskCardNumber(String cardNumber) {
        if (cardNumber.length() == 16) {
            return cardNumber.substring(0, 4) + " " +
                    cardNumber.substring(4, 6) + "••  •••• " +
                    cardNumber.substring(12);
        } else if(cardNumber.contains("customer_code")) {
            return getCustomerCode(cardNumber);
        } else if(cardNumber.contains("licshet")) {
            return getCustomerCode(cardNumber);
        }else if(cardNumber.contains("clientid")) {
            return getCustomerCode(cardNumber);
        }else {
            return cardNumber;
        }
    }
    public static String getCustomerCode(String input) {
        String[] pairs = input.split(";");

        for (String pair : pairs) {
            String[] keyValue = pair.split(":");
            if (keyValue.length == 2 && keyValue[0].trim().equals("customer_code")) {
                return keyValue[1].trim();
            }
            else if (keyValue.length == 2 && keyValue[0].trim().equals("licshet")) {
                return keyValue[1].trim();
            }else if (keyValue.length == 2 && keyValue[0].trim().equals("clientid")) {
                return keyValue[1].trim();
            }else {
                keyValue[1].trim();
            }
        }

        return null;
    }
    public static String formatDate(String inputDate) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd.MM.yyyy");

        try {
            Date date = inputFormat.parse(inputDate);
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
    public static String formatWithSpaces(double number) {
        NumberFormat numberFormat = NumberFormat.getInstance(Locale.US);
        String formattedNumber = numberFormat.format(number);
        return formattedNumber.replace(",", " ") + " UZS";
    }
    public static String formatCardInfo(String cardNumber, String ownerName) {
        if (cardNumber == null || cardNumber.length() != 16 || ownerName == null || ownerName.isEmpty()) {
            return "";
        }

        String lastFourDigits = cardNumber.substring(12);
        return "•• " + lastFourDigits + " | " + ownerName;
    }
    public static String formatMaskedCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() != 16) {
            return cardNumber;
        }

        String part1 = cardNumber.substring(0, 4);
        String part2 = cardNumber.substring(4, 6) + "••";
        String part3 = "••••";
        String part4 = cardNumber.substring(12);

        return part1 + " " + part2 + " " + part3 + " " + part4;
    }
}
