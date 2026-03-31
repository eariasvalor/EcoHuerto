package com.huerto.api.domain.exception;

public class InactiveAdminException extends RuntimeException {
        public InactiveAdminException() {
            super("Administrator account is inactive");
        }
    }
