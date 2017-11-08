/*******************************************************************************
 * Copyright (C) 2017 Marcelo Vinícius Cysneiros Aragão
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/
package io.github.marcelovca90.mail;

import java.io.File;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MessageBuilder
{
    private static final Logger LOGGER = LogManager.getLogger(MessageBuilder.class);

    private String from;
    private String recipients;
    private String subject;
    private String text;
    private String filename;
    private Session session;

    public MessageBuilder withFrom(String from)
    {
        this.from = from;
        return this;
    }

    public MessageBuilder withRecipients(String recipients)
    {
        this.recipients = recipients;
        return this;
    }

    public MessageBuilder withSubject(String subject)
    {
        this.subject = subject;
        return this;
    }

    public MessageBuilder withText(String text)
    {
        this.text = text;
        return this;
    }

    public MessageBuilder withFilename(String filename)
    {
        this.filename = filename;
        return this;
    }

    public MessageBuilder withSession(Session session)
    {
        this.session = session;
        return this;
    }

    public Message build()
    {
        Message message = null;

        try
        {
            message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipients));
            message.setSubject(subject);

            BodyPart messageTextPart = new MimeBodyPart();
            messageTextPart.setText(text);

            BodyPart messageAttachmentPart = new MimeBodyPart();
            DataSource source = new FileDataSource(new File(filename));
            messageAttachmentPart.setDataHandler(new DataHandler(source));
            messageAttachmentPart.setFileName(filename);

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageTextPart);
            multipart.addBodyPart(messageAttachmentPart);
            message.setContent(multipart);
        }
        catch (MessagingException e)
        {
            LOGGER.error("Unable to set message properties.", e);
            message = null;
        }

        return message;
    }
}
