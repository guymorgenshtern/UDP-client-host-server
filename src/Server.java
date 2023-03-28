import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;

public class Server {
	DatagramPacket sendPacket, receivePacket;
	DatagramSocket receiveSocket, sendSocket;
	private final int MAX_ZERO_BYTES = 2;
	private final int ASCII_RANGE_MIN = 32; //ascii range beginning at "space" character
	private final int ASCII_RANGE_MAX = 126; //ends at ~, includes 0-9 a-Z, and a number of special characters
			

	/**
	 * constructor for client, creates the send/receive socket
	 */
	public Server() {
		try {
			receiveSocket = new DatagramSocket(69);
			receiveSocket.setSoTimeout(10000);
		} catch (SocketException se) { 
			se.printStackTrace();
			System.exit(1);
		}

	}
	
	/**
	 * validate received packet
	 * @param packet
	 * @return
	 */
	private boolean validatePacket(DatagramPacket packet) {
		int numZeroBytes = 0;
		int endOfMessage = 0;
		
		// begins with 0
		if (packet.getData()[0] != (byte)(0)) {
			return false;
			
		}
		
		//followed by 1 for read or 2 for write
		if ( !(packet.getData()[1] == (byte)(1) || packet.getData()[1] == (byte)(2) )) {
			return false;
		};
		
		//includes 2 separation 0 bytes and ASCII characters in between
		for (int i = 2; i < packet.getLength(); i++) {
			if (packet.getData()[i] == (byte)(0)) {
				numZeroBytes++;
			} else if (!(packet.getData()[i] >= ASCII_RANGE_MIN && packet.getData()[i] <= ASCII_RANGE_MAX)){
				return false;
			}
			
			if (numZeroBytes > MAX_ZERO_BYTES) {
				return false;
			}

		}
		
		return true;

	}
	

	/**
	 * Decode a packet into a message object
	 * @param data - byte[] to decode
	 * @return Message 
	 * @throws Exception 
	 */
	private Message decode(DatagramPacket packet) throws Exception {
		
		MessageType type = packet.getData()[1] == (byte) 0001 ? MessageType.READ : MessageType.WRITE;
		String fileName = "";
		int endOfFileName = 0;
		int endOfMessage = 0;

		if (!validatePacket(packet)) {
			throw new Exception("Invalid packet format");
		}
		
		int pointer = 2;
		//find first 0 byte used as separator
		while(pointer < packet.getLength() && endOfFileName == 0) {
			if (packet.getData()[pointer] == (byte) 0) {
				byte[] n = Arrays.copyOfRange(packet.getData(), 2, pointer);
				fileName = new String(n);
				endOfFileName = pointer;
			}
			pointer++;
		}

		String mode = new String (Arrays.copyOfRange(packet.getData(), endOfFileName, packet.getLength()));

		return new Message(type, fileName, mode);
	}

	/**
	 * sends/receives packets to/from hosts
	 */
	public void receive() {


		byte data[] = new byte[100];
		receivePacket = new DatagramPacket(data, data.length);

		
		try {
			receiveSocket.receive(receivePacket);
			
		} catch(IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		System.out.println(receivePacket.getLength());
		
		Message received;
		try {
			received = decode(receivePacket);
			System.out.println("Server received");
			Utils.printPacketInfo(receivePacket);

			reply(received);
		} catch (Exception e) {
			e.printStackTrace();
			receiveSocket.close();
			sendSocket.close();
			System.exit(0);
		}
	}

	/**
	 * reply back to host
	 * @param received
	 */
	public void reply(Message received) {
		//build return message

		try {
			sendSocket = new DatagramSocket();
		} catch (SocketException e1) {
			e1.printStackTrace();
		}

		byte[] returnMessage;

		if (received.getType() == MessageType.READ) {
			returnMessage = new byte[] {(byte)(0), (byte)(3), (byte)(0), (byte)(1)};
		} else {
			returnMessage = new byte[] {(byte)(0), (byte)(4), (byte)(0), (byte)(0)};
		}

		DatagramPacket returnPacket = new DatagramPacket(returnMessage, returnMessage.length, receivePacket.getAddress(), receivePacket.getPort());

		try {
			sendSocket.send(returnPacket);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		System.out.println("Server sending to host");
		Utils.printPacketInfo(returnPacket);
		sendSocket.close();
	}

	public static void main (String[] args) {
		Server s = new Server();

		while(true) {
			s.receive();
		}
	}
}
