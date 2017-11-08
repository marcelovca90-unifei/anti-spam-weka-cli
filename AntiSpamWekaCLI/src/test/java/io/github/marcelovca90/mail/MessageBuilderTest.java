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

import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import javax.mail.Message;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith (MockitoJUnitRunner.class)
public class MessageBuilderTest
{
    private final String from = "from";
    private final String invalidRecipients = "john.doe @ domain.com";
    private final String validRecipients = "john.doe@domain.com";
    private final String subject = "subject";
    private final String text = "text";
    private final String filename = "filename";

    @InjectMocks
    private MessageBuilder messageBuilder;

    @Test
    public void build_withInvalidRecipientAndAllFields_shouldReturnNullMessage()
    {
        Message message = messageBuilder.withFrom(from).withRecipients(invalidRecipients).withSubject(subject).withText(text).withFilename(filename).withSession(null).build();

        assertThat(message, nullValue());
    }

    @Test
    public void build_withValidRecipientAndAllFields_shouldReturnNotNullMessage()
    {
        Message message = messageBuilder.withFrom(from).withRecipients(validRecipients).withSubject(subject).withText(text).withFilename(filename).withSession(null).build();

        assertThat(message, notNullValue());
    }
}
