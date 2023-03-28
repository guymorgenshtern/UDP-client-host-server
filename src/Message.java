
public class Message {
	private MessageType type;
	private String fileName;
	private String mode;
	
	public Message(MessageType type, String fileName, String mode) {
		this.type = type;
		this.fileName = fileName;
		this.mode = mode;
	}

	public MessageType getType() {
		return type;
	}

	public String getFileName() {
		return fileName;
	}

	public String getMode() {
		return mode;
	}
	
	
}
