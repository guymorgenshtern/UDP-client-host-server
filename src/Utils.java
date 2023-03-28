import java.net.DatagramPacket;

public class Utils {
	public static void printPacketInfo(DatagramPacket packet) {
		System.out.println("Sender: " + packet.getAddress() + " Port: " + packet.getPort());
		System.out.println("Data:" );
		for (int i = 0; i < packet.getLength(); i++) {
			System.out.print(packet.getData()[i]);
		}
		
		String s = new String(packet.getData());
		System.out.println("\n" + s);
		System.out.println("-------------------------------");
	}
}
