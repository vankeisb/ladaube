package com.ladaube.util

import com.ladaube.modelcouch.User
import groovy.text.SimpleTemplateEngine
import com.ladaube.model.LaDaube
import com.ladaube.model.LaDaubeSession
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.MimeMessage
import javax.mail.internet.InternetAddress
import javax.mail.Message
import javax.mail.MessagingException

class MassMailer {

  public static void main(String[] args) {

    def engine = new SimpleTemplateEngine()
    def email = '''

Salut $username,

Tu reois cet email car tu as ŽtŽ choisi pour participer au beta-testing de LaDaube.

Normalement, tu connais dŽjˆ le principe du bouzin, je te refais pas tout le film. Si t'as un doute, tu me demandes (reply ˆ cet email).

L'idŽe c'est que tu utilises l'appli afin de donner ton avis, tes recommandations, de traiter... bref, l‰che toi !

On t'a crŽŽ un compte :
url : http://rvkb.dyndns.info/ladaube
login : $username
password : $username

Il n'est pas possible d'inviter des potes pour le moment, tout est "en dur". La feature d'ajout de buddy sera dispo plus tard. 

Tu peux lui bourrer le cul : je lui ai collŽ un bon gros disque qui devrait encaisser (Žvite de dŽpasser les 50Go quand mme).
Par contre a tourne sur mon mac @home, servi par ma livebox, donc ne t'attend pas ˆ des perfs de malades.
Aussi, j'essaie de laisser la machine allumŽe le plus possible sans interrompre le service, mais bon, il se peut que ca tombe. Si c'est le cas, retente ta chance un peu plus tard.

Pour l'instant tu peux pas changer ton mot de passe, donc prire de ne pas faire n'imp avec les logins des copains !

Voilˆ je crois que j'ai tout dit. Si tu as des questions, n'hŽsite pas.

Amuse toi bien, et tiens nous au courant !

A+

RŽmi / LaDaube

PS : Evite de trop parler de tout a, j'ai pas envie de finir en taule pour un beta test :/

'''
    def template = engine.createTemplate(email)

    def userNames = ['iou','jay-d','kakou','polo','remi','tof','tommy']

    LaDaube.get().doInSession { LaDaubeSession s ->
      userNames.each { username ->
        User u = s.getUser(username)
        def mailStr = template.make(['username': u.id]).toString()
        println "Sending to $u.id"
        postMail(u.email, 'LaDaube beta testing...', mailStr, 'ladaube-contact@gmail.com')
      }
    }
  }

  public static void postMail(String recipient, String subject, String messageStr, String from) throws MessagingException {

    Properties props = new Properties();
    props.put("mail.transport.protocol", "smtp");
    props.put("mail.smtp.host", "smtp.gmail.com");
    props.put("mail.smtp.auth", "true");
    props.put("mail.smtp.socketFactory.port","465");
    props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
    props.put("mail.smtp.socketFactory.fallback", "false");


    Session mailSession = Session.getDefaultInstance(props, new SMTPAuthenticator());
    // uncomment for debugging infos to stdout
    mailSession.setDebug(false);
    Transport transport = mailSession.getTransport();

    MimeMessage message = new MimeMessage(mailSession);
    message.setSubject(subject);
    message.setContent(messageStr, "text/plain");
    message.setFrom(new InternetAddress("ladaube-contact@gmail.com"));
    message.addRecipient(Message.RecipientType.TO,
            new InternetAddress(recipient));

    transport.connect();
    transport.sendMessage(message,
            message.getRecipients(Message.RecipientType.TO));
    transport.close();

    println "message sent to $recipient" 

  }

}
