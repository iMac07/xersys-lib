import org.xersys.commander.base.SQLConnection;
import java.util.Properties;
import org.xersys.commander.base.Nautilus;
import org.xersys.commander.base.Property;
import org.xersys.commander.crypt.CryptFactory;
import org.xersys.lib.base.SMTPSender;

public class testEmailSender {
    public static void main(String [] args){
        final String PRODUCTID = "Daedalus";
               
        //get database property
        Property loConfig = new Property("db-config.properties", PRODUCTID);
        if (!loConfig.loadConfig()){
            System.err.println(loConfig.getMessage());
            System.exit(1);
        } else System.out.println("Database configuration was successfully loaded.");

        //connect to database
        SQLConnection loConn = new SQLConnection();
        loConn.setProperty(loConfig);
        if (loConn.getConnection() == null){
            System.err.println(loConn.getMessage());
            System.exit(1);
        } else
            System.out.println("Connection was successfully initialized.");        

        //load application driver
        Nautilus loNautilus = new Nautilus();

        loNautilus.setConnection(loConn);
        loNautilus.setEncryption(CryptFactory.make(CryptFactory.CrypType.AESCrypt));

        loNautilus.setUserID("000100210001");
        if (!loNautilus.load(PRODUCTID)){
            System.err.println(loNautilus.getMessage());
            System.exit(1);
        } else
            System.out.println("Application driver successfully initialized.");

        String path;
        if (System.getProperty("os.name").toLowerCase().contains("win")){
            path = (String) loNautilus.getAppConfig("sApplPath");
        } else {
            path = "/srv/icarus/";
        }
        
        System.setProperty("sys.default.path.config", path);
        
        SMTPSender loSender = new SMTPSender();
        loSender.setTitle("This is a test email.");
        loSender.setBody("The quick brown fox jumps over the lazy dog.");
        loSender.addRecipient("michael_cuison07@yahoo.com");
        loSender.addAttachment(System.getProperty("sys.default.path.config") + "temp/purchases/PO_EP.java");
        
        if (loSender.SendEmail())
            System.out.println("Mail sent successfully.");
        else
            System.err.println(loSender.getMessage());
    }
}
