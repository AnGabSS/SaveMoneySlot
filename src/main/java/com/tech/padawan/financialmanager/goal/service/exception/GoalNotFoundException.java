package com.tech.padawan.financialmanager.goal.service.exception;

import com.tech.padawan.financialmanager.global.exception.NotFoundException;

public class GoalNotFoundException extends NotFoundException {
    public GoalNotFoundException(String message) {
        super(message);
    }
}
