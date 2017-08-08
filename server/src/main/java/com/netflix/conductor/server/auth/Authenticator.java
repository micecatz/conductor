package com.netflix.conductor.server.auth;

import javax.servlet.http.HttpServletResponse;

public interface Authenticator {

    default AuthenticationResult authenticateViaToken(String token) {
        return new AuthenticationResult(HttpServletResponse.SC_UNAUTHORIZED, "No authentication method specified");
    }

}
