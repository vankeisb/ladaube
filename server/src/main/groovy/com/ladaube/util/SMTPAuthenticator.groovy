package com.ladaube.util

import javax.mail.PasswordAuthentication


class SMTPAuthenticator extends javax.mail.Authenticator {

    public PasswordAuthentication getPasswordAuthentication() {
       return new PasswordAuthentication("ladaube.contact", "ladaube.3");
    }
}
