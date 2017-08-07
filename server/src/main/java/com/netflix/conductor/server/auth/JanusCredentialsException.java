package com.netflix.conductor.server.auth;

public class JanusCredentialsException extends Exception {
    public JanusCredentialsException() {
        super();
    }
    public JanusCredentialsException(String message) {
        super(message);
    }
}
