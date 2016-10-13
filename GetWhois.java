import java.io.*;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.*;


public class GetWhois {  
  
    private static final int DEFAULT_PORT = 43;  
     
    public static String getWhoisServer(String domain) throws Exception {
        String server = "";  
        String tld = getTLD(domain);  
        if ("com".equals(tld)) {  
            server = "whois.verisign-grs.com";  
        } else if ("net".equals(tld)) {  
            server = "whois.verisign-grs.com";  
        } else if ("org".equals(tld)) {  
            server = "whois.pir.org";  
        } else if ("cn".equals(tld)) {  
            server = "whois.cnnic.cn";  
        }
        String whoisInfo = null;
        try {
            whoisInfo = query(domain, server);
        } catch (Exception e) {
            System.out.println("[SOCKET ERROR] " + domain);
            return null;
        }
        System.out.println(whoisInfo);
        String[] lines = whoisInfo.split("\n");
        System.out.println("Tokens size: " + lines.length);
        for (String line : lines) {
            line = line.trim();
            if (line.startsWith("Whois Server:")) {
              System.out.println("##################################");
              System.out.println(line);
              String[] tokens = line.split(":");
              String whoisServer = tokens[1].trim();
              System.out.println("Whois Server = " + whoisServer);
              System.out.println("##################################");
              return whoisServer;
            }
        }
        return null;
    }

    public static String query(String domain, String server) throws Exception {  
        Socket socket = new Socket(server, DEFAULT_PORT);  
        String lineSeparator = "\r\n";  
        StringBuilder ret = new StringBuilder();  
        
        PrintWriter out = new PrintWriter(socket.getOutputStream());  
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));  
        out.println(domain);  
        out.flush();  
        String line;  
        while ((line = in.readLine()) != null) {  
            ret.append(line + lineSeparator);  
        }  
        socket.close();  
        return ret.toString();  
    }  
      
    private static String getTLD(String domain) {  
        final int index;  
        return (domain == null || (index = domain.lastIndexOf('.') + 1) < 1) ? domain  
                : (index < (domain.length())) ? domain.substring(index) : "";  
    }  
      
    public static void main(String[] args) throws Exception {  
        //Whois w = new Whois();  
        //System.out.println(w.query("paopaomi.com"));  
        //System.out.println(w.query("csdn.net"));  
        //System.out.println(w.query("apache.org"));  
        //System.out.println(w.query("360.cn"));          //china  
        //

        ArrayList<String> domains = new ArrayList<String>();
        domains.add("paopaomi.com");
        //domains.add("=baidu.com");
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(2);  
        for (int i = 0; i < domains.size(); i++) {  
          final String domain = domains.get(i);  
          fixedThreadPool.execute(new Runnable() {  
            public void run() {  
              try {  
                System.out.println("Processing " + domain);
                String whoisServer = getWhoisServer("=" + domain);
                String whoisInfo = query(domain, whoisServer);
                System.out.println("Whois Info: " + whoisInfo);
                Thread.sleep(10);  
              } catch (Exception e) {  
                e.printStackTrace();  
              }  
            }  
          });
        } 
        fixedThreadPool.shutdown();
        fixedThreadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
        System.out.println("all thread complete");
    }  
      
}
