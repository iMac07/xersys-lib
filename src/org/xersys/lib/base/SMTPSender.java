package org.xersys.lib.base;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class SMTPSender {
    private String _email;
    private String _password;
    private String _message;
    
    private String _title;
    private String _body;
    
    private JSONArray _recipient;
    private JSONArray _cc;
    private JSONArray _bcc;
    private JSONArray _attachment;
    
    private Session _session;
    private Properties _property;
    
    public SMTPSender(){
        _cc = new JSONArray();
        _bcc  = new JSONArray();
        _recipient = new JSONArray();
        _attachment = new JSONArray();
        
        _email = "";
        _password = "";
        _title = "";
        _body = "";
        _message = "";
        
        _session = null;
        _property = System.getProperties();
    }
    
    public void setTitle(String lsValue){
        _title = lsValue;
    }
    
    public void addRecipient(String lsValue){
        _recipient.add(lsValue);
    }
    
    public void addCC(String lsValue){
        _cc.add(lsValue);
    }
    
    public void addBCC(String lsValue){
        _bcc.add(lsValue);
    }
    
    public void setBody(String lsValue){
        _body = lsValue;
    }
    
    public void addAttachment(String lsValue){
        _attachment.add(lsValue);
    }
    
    public String getMessage(){
        return _message;
    }
    
    private boolean loadConfig(){
        try {
            Properties loProps = new Properties();
            loProps.load(new FileInputStream(System.getProperty("sys.default.path.config") + "app-config.properties"));
            
            _email = loProps.getProperty("mail.user");
            _password = loProps.getProperty("mail.password");
            
            _property.put("mail.smtp.host", loProps.getProperty("mail.smtp.host"));
            _property.put("mail.smtp.auth", "true");
            _property.put("mail.debug", "false");
            _property.put("mail.smtp.port", loProps.getProperty("mail.smtp.port"));
            _property.put("mail.smtp.socketFactory.port", loProps.getProperty("mail.smtp.port"));
            _property.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            _property.put("mail.smtp.socketFactory.fallback", "false");
                        
            return true;
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
            return false;
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
    }
    
    private boolean createSession(){
        _session = Session.getInstance(_property,
                    new javax.mail.Authenticator() {
                        @Override
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(_email, _password);
                        }
                    });
        _session.setDebug(true); // Enable the debug mode
        
        return true;
    }
    
    public boolean SendEmail(){
        if (!loadConfig()){
            _message = "Unable to load email configurations.";
            return false;
        }
        
        if (!createSession()){
            _message = "Unable to create email session.";
            return false;
        }
        
        try {
            MimeMessage message = new MimeMessage(_session);
            message.setFrom(new InternetAddress(_email));
            
            if (!_recipient.isEmpty()){
                for (int lnCtr = 0; lnCtr <= _recipient.size()-1; lnCtr++){
                    message.addRecipient(Message.RecipientType.TO, new InternetAddress((String) _recipient.get(lnCtr)));
                }
            } else {
                _message = "Recipient address is empty.";
                return false;
            }
            
            if (!_cc.isEmpty()){
                for (int lnCtr = 0; lnCtr <= _cc.size()-1; lnCtr++){
                    message.addRecipient(Message.RecipientType.CC, new InternetAddress((String) _cc.get(lnCtr)));
                }
            }
            
            if (!_bcc.isEmpty()){
                for (int lnCtr = 0; lnCtr <= _bcc.size()-1; lnCtr++){
                    message.addRecipient(Message.RecipientType.BCC, new InternetAddress((String) _bcc.get(lnCtr)));
                }
            }
            
            message.setSubject(_title);
            
            Multipart multipart = new MimeMultipart();
            
            MimeBodyPart textBodyPart = new MimeBodyPart();
            textBodyPart.setText(_body);
            multipart.addBodyPart(textBodyPart);
            message.setContent(multipart);
            
            if (!_attachment.isEmpty()){
                MimeBodyPart attachmentBodyPart = new MimeBodyPart();
                
                File loFile;
                DataSource loSource;
                
                for (int lnCtr = 0; lnCtr <= _attachment.size()-1; lnCtr++){
                    loFile = new File((String) _attachment.get(lnCtr));
                    
                    if (!loFile.isFile()){
                        _message = (String) _attachment.get(lnCtr) + " is not a valid file.";
                        return false;
                    }
                    
                    loSource = new FileDataSource(loFile.getAbsolutePath());
                    attachmentBodyPart.setDataHandler(new DataHandler(loSource));
                    attachmentBodyPart.setFileName(loFile.getName());
                    multipart.addBodyPart(attachmentBodyPart); 
                }
            }   
            
            Transport.send(message);
        } catch (AddressException ex) {
            ex.printStackTrace();
            return false;
        } catch (MessagingException ex) {
            ex.printStackTrace();
            return false;
        }
        
        return true;   
    }
}
