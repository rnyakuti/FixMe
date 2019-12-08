package router;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;

public class Router
{

    public static final int brokerPort = 5000;
    public static final int marketPort = 5001;
	
	static private String brokerMessages = "";
	
    /*Colours*/
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE = "\u001B[34m";
    public static final String PURPLE = "\u001B[35m";
    public static final String CYAN = "\u001B[36m";
    public static final String WHITE = "\u001B[37m";
    public static final String RESET_CO = "\u001B[0m";

     public static void main(String[] args) {

         Server broker= new Server(brokerPort, "BROKER");
         broker.start();
         Server market = new Server(marketPort, "MARKET");
         market.start();
		 
		    while (true) {
            try {
                
                brokerMessages = broker.getMessages();
			    System.out.println( "Broker Server Port:5000 msg:"+brokerMessages);
				market.sendMessage(brokerMessages);
				 brokerMessages = "";
			}
			catch(Exception e)
			{
				
			}
			
			}

     }
}