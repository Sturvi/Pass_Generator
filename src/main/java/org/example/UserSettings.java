package org.example;

public class UserSettings {
    Boolean upperCaseLetters;
    Boolean digits;
    Boolean specialSymbols;
    Integer passwordLength;

    public UserSettings() {
        upperCaseLetters = true;
        digits = true;
        specialSymbols = true;
        passwordLength = 12;
    }

    public Boolean getUpperCaseLetters() {
        return upperCaseLetters;
    }

    public void setUpperCaseLetters() {
        upperCaseLetters = !upperCaseLetters;
    }

    public Boolean getDigits() {
        return digits;
    }

    public void setDigits() {
        digits = !digits;
    }

    public Boolean getSpecialSymbols() {
        return specialSymbols;
    }

    public void setSpecialSymbols() {
        specialSymbols = !specialSymbols;
    }

    public Integer getPasswordLength() {
        return passwordLength;
    }

    public void setPasswordLength(Integer passwordLength) {
        this.passwordLength = passwordLength;
    }

    public Integer getTrueCount (){
        Integer count = 4;
        if (!upperCaseLetters) count--;
        if (!digits) count--;
        if (!specialSymbols) count--;

        return count;
    }
}
