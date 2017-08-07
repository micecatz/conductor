package com.netflix.conductor.server.auth;

public class AuthenticationResult {

    private final int HTTP_RESPONSE;
    private final String MESSAGE;

    public AuthenticationResult(int HTTP_RESPONSE) {
        this(HTTP_RESPONSE, null);
    }

    public AuthenticationResult(int response, String message) {
        this.HTTP_RESPONSE = response;
        this.MESSAGE = message;
    }

    public int getResponse() {
        return HTTP_RESPONSE;
    }

    public String getMessage() {
        return MESSAGE;
    }
}
