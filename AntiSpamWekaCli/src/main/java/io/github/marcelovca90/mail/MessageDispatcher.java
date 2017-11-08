package io.github.marcelovca90.mail;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MessageDispatcher
{
    private static final Logger LOGGER = LogManager.getLogger(MessageDispatcher.class);

    public Session buildSession(CryptoProtocol protocol, String host, String username, String password)
    {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.host", host);

        switch (protocol)
        {
            case SSL:
                props.put("mail.smtp.port", "465");
                props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
                props.put("mail.smtp.socketFactory.port", "465");
                break;

            case TLS:
                props.put("mail.smtp.port", "587");
                props.put("mail.smtp.starttls.enable", "true");
                break;
        }

        Authenticator authenticator = new Authenticator()
        {
            @Override
            protected PasswordAuthentication getPasswordAuthentication()
            {
                return new PasswordAuthentication(username, password);
            }
        };

        return Session.getDefaultInstance(props, authenticator);
    }

    public boolean send(Message message)
    {
        boolean success;

        try
        {
            Transport.send(message);
            success = true;
        }
        catch (MessagingException e)
        {
            LOGGER.error(e);
            success = false;
        }

        return success;
    }
}
