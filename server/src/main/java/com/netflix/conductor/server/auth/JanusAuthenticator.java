package com.netflix.conductor.server.auth;

import com.jpmorgan.janus.client.JanusClient;
import com.jpmorgan.janus.client.JanusClientSession;

import com.netflix.conductor.server.EhCacheConfig;
import org.apache.commons.lang.StringUtils;

import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.http.HttpServletResponse;

public final class JanusAuthenticator implements Authenticator {

    private EhCacheConfig ehCacheConfig;

    //        private static final String JANUS_APPLICATION_KEY = System.getenv("JANUS_APPLICATION_KEY");
//    private static final String JANUS_AUTH_URL = System.getenv("JANUS_AUTH_URL");
    private static final String JANUS_APPLICATION_KEY = "859C3EE3-8FA8-7BF1-A47B-6C8DBC1F1B2B";
    private static final String JANUS_AUTH_URL = "https://janus-sso-uat.jpmorgan.com/authz/action/authorize";

    public JanusAuthenticator(EhCacheConfig ehCacheConfig) {
        this.ehCacheConfig = ehCacheConfig;
    }

    @Override
    public AuthenticationResult authenticateViaToken(String janusToken) {
        if (ehCacheConfig.getTokenCache().containsKey(janusToken) && ehCacheConfig.getTokenCache().get(janusToken)) {
            return new AuthenticationResult(HttpServletResponse.SC_OK);
        }
        if (StringUtils.isEmpty(janusToken)) {
            return new AuthenticationResult(HttpServletResponse.SC_UNAUTHORIZED, "Janus token is not set");
        }
        try {
            JanusClientSession session = JanusClient.createSession(JANUS_APPLICATION_KEY, null, null);
            authenticateSession(session, janusToken);
            authoriseSession(session);
            ehCacheConfig.getTokenCache().put(janusToken, true);
            return new AuthenticationResult(HttpServletResponse.SC_OK);
        } catch (Exception e) {
            return new AuthenticationResult(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
        }
    }

    private static void authenticateSession(JanusClientSession session, String janusToken) throws JanusCredentialsException {
        boolean res = session.setSecurityToken(JanusClientSession.TRUST_LEVEL_MEDIUM, janusToken);
        if (!res) {
            throw new JanusCredentialsException("Janus token couldn't be set in session: " + janusToken);
        }
        if (!session.isAuthenticated()) {
            throw new JanusCredentialsException("User is not authenticated");
        }
    }

    private static void authoriseSession(JanusClientSession session) throws JanusCredentialsException {
        URL authorizeUrl;
        try {
            authorizeUrl = new URL(JANUS_AUTH_URL);
        } catch (MalformedURLException e) {
            throw new JanusCredentialsException("JANUS_AUTH_URL is invalid: " + JANUS_AUTH_URL);
        }

        String[] allow = {};
        String[] deny = {};
        short minSecurityLevel = 100;

        int
            authorizationResult =
            session.authorize(authorizeUrl, JanusClientSession.TRUST_LEVEL_MEDIUM, minSecurityLevel, allow, deny, JanusClientSession.SESSION_TIME_IGNORED,
                              JanusClientSession.INACTIVITY_TIME_IGNORED);

        if (authorizationResult != JanusClientSession.AUTHZ_OK) {
            throw new JanusCredentialsException(
                "User is not authorized. authorizationResult=" + JanusClientSession.getAuthorizationStatusForDisplay(authorizationResult));
        }
    }

}
