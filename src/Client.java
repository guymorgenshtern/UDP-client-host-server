import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;

public class Client {
	   private DatagramPacket sendPacket, receivePacket;
	   private DatagramSocket sendReceiveSocket;
	   private final byte ZERO_BYTE = (byte)(0);
	   private static final Message READ_MESSAGE = new Message(MessageType.READ, "read.txt", "netASSci");
	   private static final Message WRITE_MESSAGE = new Message(MessageType.WRITE, "write.txt", "Netassci");
	
	   /**
	    * constructor for client, creates the send/receive socket
	    */
	   public Client() {
	      try {
	         sendReceiveSocket = new DatagramSocket();
	         sendReceiveSocket.setSoTimeout(5000);
	      } catch (SocketException se) { 
	         se.printStackTrace();
	         System.exit(1);
	      }
	     
	   }
	   
	   /**
	    * builds a byte message from a fileName, mode, and read/write
	    * @param fileName
	    * @param mode
	    * @param isReadMessage
	    * @return byte[] - translated byte array
	    */
	   private byte[] buildMessage(Message message) {
		    
		    byte[] fileBytes = message.getFileName().getBytes();
		    byte[] modeBytes = message.getMode().getBytes();
		    
		    byte[] byteMessage = new byte[fileBytes.length + modeBytes.length + 4];
		    byteMessage[0] = ZERO_BYTE;
		    byteMessage[1] = message.getType() == MessageType.READ ? (byte)(1) : (byte)(2);
		    
		    //re-factor to array copy method
		    int position = 2;
		    
		    for (byte b : fileBytes) {
		    	byteMessage[position] = b;
		    	position++;
		    }
		    
		    byteMessage[position] = ZERO_BYTE;
		    position++;
		    
		    for (byte b : modeBytes) {
		    	byteMessage[position] = b;
		    	position++;
		    }
		    
		   
		   return byteMessage;
	   }
	  
	   
	   /**
	    * sends/receives packets to/from hosts
	    */
	   public void sendAndReceive(byte[] msg) {
    	  
    	  try {
 	         sendPacket = new DatagramPacket(msg, msg.length,
 	                                         InetAddress.getLocalHost(), 23);
 	      } catch (UnknownHostException e) {
 	         e.printStackTrace();
 	         System.exit(1);
 	      }
    	  
    	  System.out.println("Sending to host: ");
    	  Utils.printPacketInfo(sendPacket);
	      try {
	    	  sendReceiveSocket.send(sendPacket);
		  } catch (IOException e) {
		      e.printStackTrace();
		      System.exit(1);
		  }

	      byte data[] = new byte[4];
	      receivePacket = new DatagramPacket(data, data.length);
	     

	      System.out.println("trying to receive from host \n");
	      try {
	         // Block until a datagram is received via sendReceiveSocket.  
	         sendReceiveSocket.receive(receivePacket);
	         System.out.println("received");
	      } catch(IOException e) {
	         e.printStackTrace();
	         System.exit(1);
	      }
	      
	      Utils.printPacketInfo(receivePacket);
	   }
	   
	   /**
	    * main method for clients, sends 10 messages and then an invalid packet
	    * @param args
	    */
	   public static void main (String[] args) {
		   
		   Client c = new Client();
		   Message msg;
		   for (int i = 0; i < 10; i++) {
			   msg = (i % 2 == 0) ? READ_MESSAGE
		    			  : WRITE_MESSAGE;
			   System.out.println("message " + i);
			   
			   c.sendAndReceive(c.buildMessage(msg)); 
			   try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		   }
		   
		   //send invalid packet
		   byte[] invalidMessage = new byte[] {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
		   System.out.println("\nsending invalid packet");
		   c.sendAndReceive(invalidMessage); 
		   
		   c.sendReceiveSocket.close();
	   }
}
