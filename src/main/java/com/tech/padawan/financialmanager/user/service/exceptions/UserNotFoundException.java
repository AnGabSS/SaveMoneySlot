package com.tech.padawan.financialmanager.user.service.exceptions;

import com.tech.padawan.financialmanager.global.exception.NotFoundException;

public class UserNotFoundException extends NotFoundException {
    public UserNotFoundException(String message){
        super(message);
    }
}
