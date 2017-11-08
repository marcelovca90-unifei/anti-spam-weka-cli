package io.github.marcelovca90.mail;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import javax.mail.Message;
import javax.mail.Session;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith (MockitoJUnitRunner.class)
public class MessageDispatcherTest
{
    private final String from = "from";
    private final String recipients = "recipients";
    private final String subject = "subject";
    private final String text = "text";
    private final String filename = "filename";
    private final String host = "host";
    private final String username = "username";
    private final String password = "password";
    private final MessageBuilder messageBuilder = new MessageBuilder();

    private Session session;
    private Message message;

    @InjectMocks
    private MessageDispatcher messageDispatcher;

    @Test
    public void buildSession_usingSSLandFakeCredentials_shouldReturnNotNullSession()
    {
        assertThat(messageDispatcher.buildSession(CryptoProtocol.SSL, host, username, password), notNullValue());
    }

    @Test
    public void buildSession_usingTLSandFakeCredentials_shouldReturnNotNullSession()
    {
        assertThat(messageDispatcher.buildSession(CryptoProtocol.TLS, host, username, password), notNullValue());
    }

    @Test
    public void send_usingSSLandFakeCredentials_shouldReturnFalse()
    {
        // given
        session = messageDispatcher.buildSession(CryptoProtocol.SSL, host, username, password);
        message = messageBuilder.withFrom(from).withRecipients(recipients).withSubject(subject).withText(text).withFilename(filename).withSession(session).build();

        // when
        boolean success = messageDispatcher.send(message);

        // then
        assertThat(success, equalTo(false));
    }

    @Test
    public void send_usingTLSandFakeCredentialsDryRun_shouldReturnFalse()
    {
        // given
        session = messageDispatcher.buildSession(CryptoProtocol.TLS, host, username, password);
        message = messageBuilder.withFrom(from).withRecipients(recipients).withSubject(subject).withText(text).withFilename(filename).withSession(session).build();

        // when
        boolean success = messageDispatcher.send(message);

        // then
        assertThat(success, equalTo(false));
    }
}
