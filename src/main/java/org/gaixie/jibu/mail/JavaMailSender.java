/**
 * Copyright (C) 2014 Gaixie.ORG.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gaixie.jibu.mail;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.gaixie.jibu.JibuConfig;

/**
 * JavaMail 基于 smtp 协议的邮件发送类。
 */
public class JavaMailSender {

    /**
     * 通过 javax.mail 发送邮件。
     *
     * @param recipients 接收方邮件地址，多个邮件地址以逗号分隔。   
     * @param subj 邮件标题。
     * @param text 邮件正文。
     */
    public void send(String recipients, String subj, String text) {
        String usr = JibuConfig.getProperty("mail.username");
        String pwd = JibuConfig.getProperty("mail.password");
        Session session =
            Session.getDefaultInstance(JibuConfig.getProperties(),
                                       new javax.mail.Authenticator() {
                                           protected PasswordAuthentication getPasswordAuthentication() {
                                               return new PasswordAuthentication(usr,pwd);
                                           }
                                       });
        try {
            Message msg = new MimeMessage(session);
            msg.addRecipients(Message.RecipientType.TO,
                              InternetAddress.parse(recipients));
            msg.setSubject(subj);
            msg.setText(text);
            Transport.send(msg);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    } 
}
