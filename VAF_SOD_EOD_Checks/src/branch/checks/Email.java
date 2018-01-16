package branch.checks;

import java.util.ArrayList;

import java.util.Date;

import java.util.Properties;

 

import javax.mail.Message;

import javax.mail.Session;

import javax.mail.Transport;

import javax.mail.internet.InternetAddress;

import javax.mail.internet.MimeMessage;

 

/**

*

* @author a153672

*/

public class Email {

   

    

                private static String mailServer = "192.168.101.34";

                static String  toAddress = "muzi.phage@standardbank.co.za";

                static String from ="muzi.phage@standardbank.co.za";

                static String subject = "Test";

                static String message = "12345";

                static InternetAddress[] to;

                String[] sendTo = {"muzi.phage@standardbank.co.za","vafsit@sbpush.net"};

 

               

                public Email() {

 

                }

 

                public void sendMail() throws Exception {

                                Properties props = System.getProperties();

                                props.put("mail.smtp.host", getMailServer());

                                Session session = Session.getDefaultInstance(props, null);

                                Message msg = new MimeMessage(session);

                                msg.setFrom(new InternetAddress(getFrom()));

                                InternetAddress[] to1 = getToAddress();

                                msg.addRecipients(Message.RecipientType.TO, to1);

                                msg.setSubject(getSubject());

                                msg.setSentDate(new Date());

                                msg.setHeader("X-Mailer", "msgsend");

                                msg.setContent(getMessage(),"text/plain");

 

                                Transport.send(msg);

                }

 

                private InternetAddress[] getToAddress() throws Exception {

                                ArrayList<InternetAddress> to = new ArrayList<InternetAddress>();

                                int z=0;

                                for (int x = 0; x < sendTo.length; x ++) {

                                                new InternetAddress(sendTo[x]);

                                                to.add(new InternetAddress(sendTo[x]));

                                                z++;

                                }

                                return (InternetAddress[]) to.toArray(new InternetAddress[to.size()]);

                }

 

                public String getMailServer() {

                                return mailServer;

                }

 

                public String getFrom() {

                                return from;

                }

 

                public String getSubject() {

                                return subject;

                }

 

                public void setSubject(String text) {

                                subject = text;

                }

 

                public String getMessage() {

                                return message;

                }

 

                public void setMessage(String text) {

                                message = text;

                }

 

                public void setTo(String[] sendto) {

                                this.sendTo = sendto;

                }

 

   

}
