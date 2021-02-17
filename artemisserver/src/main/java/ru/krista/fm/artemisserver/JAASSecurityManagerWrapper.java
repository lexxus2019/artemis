package ru.krista.fm.artemisserver;

import org.apache.activemq.artemis.core.config.impl.SecurityConfiguration;
import org.apache.activemq.artemis.core.remoting.CertificateUtil;
import org.apache.activemq.artemis.core.security.CheckType;
import org.apache.activemq.artemis.core.security.Role;
import org.apache.activemq.artemis.logs.AuditLogger;
import org.apache.activemq.artemis.spi.core.protocol.RemotingConnection;
import org.apache.activemq.artemis.spi.core.security.ActiveMQJAASSecurityManager;
import org.apache.activemq.artemis.spi.core.security.ActiveMQSecurityManager;
import org.apache.activemq.artemis.spi.core.security.ActiveMQSecurityManager4;
import org.apache.activemq.artemis.spi.core.security.jaas.JaasCallbackHandler;
import org.apache.activemq.artemis.spi.core.security.jaas.UserPrincipal;

import javax.security.auth.Subject;
import javax.security.auth.login.Configuration;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class JAASSecurityManagerWrapper extends ActiveMQJAASSecurityManager {
    private ActiveMQJAASSecurityManager activeMQJAASSecurityManager;

    private String configurationName;
    private SecurityConfiguration configuration;

    public JAASSecurityManagerWrapper(String confName) {
        super(confName);
    }

 /*   @Override
    public Subject authenticate(String user, String password, RemotingConnection remotingConnection, String securityDomain) {
        System.out.println("authenticate(" + user + ", " + password + ", " + remotingConnection.getRemoteAddress() + ", " + securityDomain + ")");
        return activeMQJAASSecurityManager.authenticate(user, password, remotingConnection, securityDomain);
    }

    @Override
    public boolean authorize(Subject subject,
                             Set<Role> roles,
                             CheckType checkType,
                             String address) {
        System.out.println("authorize(" + subject + ", " + roles + ", " + checkType + ", " + address + ")");
        return activeMQJAASSecurityManager.authorize(subject, roles, checkType, address);
    }*/

    @Override
    public String getDomain() {
        return activeMQJAASSecurityManager.getDomain();
    }

    @Override
    public boolean validateUser(String user, String password) {
        return activeMQJAASSecurityManager.validateUser(user, password);
    }

    @Override
    public boolean validateUserAndRole(String user, String password, Set<Role> roles, CheckType checkType) {
        return activeMQJAASSecurityManager.validateUserAndRole(user, password, roles, checkType);
    }

    @Override
    public ActiveMQSecurityManager init(Map<String, String> properties) {
        activeMQJAASSecurityManager = new ActiveMQJAASSecurityManager(properties.get("domain"));
        return this;
    }

    @Override
    public void setConfiguration(SecurityConfiguration configuration) {
        super.setConfiguration(configuration);
        this.configuration = configuration;
    }

    /*@Override
    public String validateUser(String s, String s1, RemotingConnection remotingConnection, String s2) {
        return null;
    }*/

    @Override
    public String validateUserAndRole(String s, String s1, Set<Role> set, CheckType checkType, String s2, RemotingConnection remotingConnection, String s3) {
        return null;
    }

    public String validateUser(String user, String password, RemotingConnection remotingConnection, String securityDomain) {
        try {
            return this.getUserFromSubject(this.getAuthenticatedSubject(user, password, remotingConnection, securityDomain));
        } catch (LoginException var6) {
/*            if (logger.isDebugEnabled()) {
                logger.debug("Couldn't validate user", var6);
            }*/

            return null;
        }
    }

    public String getUserFromSubject(Subject subject) {
        String validatedUser = "";
        Set<UserPrincipal> users = subject.getPrincipals(UserPrincipal.class);

        UserPrincipal userPrincipal;
        for(Iterator var4 = users.iterator(); var4.hasNext(); validatedUser = userPrincipal.getName()) {
            userPrincipal = (UserPrincipal)var4.next();
        }

        return validatedUser;
    }

    private Subject getAuthenticatedSubject(String user, String password, RemotingConnection remotingConnection, String securityDomain) throws LoginException {
        ClassLoader currentLoader = Thread.currentThread().getContextClassLoader();
        ClassLoader thisLoader = this.getClass().getClassLoader();

        Subject var8;
        try {
            if (thisLoader != currentLoader) {
                Thread.currentThread().setContextClassLoader(thisLoader);
            }

            LoginContext lc;
      /*      if (securityDomain != null) {
                lc = new LoginContext(securityDomain, (Subject)null, new JaasCallbackHandler(user, password, remotingConnection), (Configuration)null);
            } else if (this.certificateConfigurationName != null && this.certificateConfigurationName.length() > 0 && CertificateUtil.getCertsFromConnection(remotingConnection) != null) {
                lc = new LoginContext(this.certificateConfigurationName, (Subject)null, new JaasCallbackHandler(user, password, remotingConnection), this.certificateConfiguration);
            } else {*/
                lc = new LoginContext(this.configurationName, (Subject)null, new JaasCallbackHandler(user, password, remotingConnection), this.configuration);
            //}

            try {
        //        lc.login();
                if (AuditLogger.isAnyLoggingEnabled() && remotingConnection != null) {
                    remotingConnection.setAuditSubject(lc.getSubject());
                }

                if (AuditLogger.isResourceLoggingEnabled()) {
                    AuditLogger.userSuccesfullyLoggedInAudit(lc.getSubject());
                }
            } catch (Exception var12) {
                if (AuditLogger.isResourceLoggingEnabled()) {
                    AuditLogger.userFailedLoggedInAudit(lc.getSubject(), var12.getMessage());
                }

                throw var12;
            }

            var8 = lc.getSubject();
        } finally {
            if (thisLoader != currentLoader) {
                Thread.currentThread().setContextClassLoader(currentLoader);
            }

        }

        return var8;
    }
}
