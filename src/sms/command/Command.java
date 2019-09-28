package sms.command;

import java.util.Date;

import sms.Modem;
import sms.command.callback.CommandCallback;
import sms.data.Message;
import sms.data.MessageType;

public abstract class Command implements Runnable, Comparable<Command> {
	private Modem modem;
	private Date createTime;
	private MessageType type;
	private Message message;
	private CommandCallback callback;

	public Command(MessageType type, Message message) {
		this(type, message, null);
	}
	
	public Command(MessageType type, Message message, CommandCallback callback) {
		this.type = type;
		this.message = message;
		this.createTime = new Date();
		this.callback = callback;
	}
	
	public MessageType getType() {
		return type;
	}

	public void setType(MessageType type) {
		this.type = type;
	}
	
	public Message getMessage() {
		return message;
	}

	public void setMessage(Message message) {
		this.message = message;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Modem getModem() {
		return modem;
	}

	public void setSender(Modem modem) {
		this.modem = modem;
	}

	@Override
	public int compareTo(Command command) {
		if (getType() != command.getType()) {
    		return getType().compareTo(command.getType());
    	}
        return getCreateTime().compareTo(command.getCreateTime());
	}
	
	public CommandCallback getCallback() {
		return callback;
	}

	public void setCallback(CommandCallback callback) {
		this.callback = callback;
	}
	
	@Override
	public String toString() {
		return "Command ["
				+ "createTime" + createTime 
				+ ", type=" + type
				+ ", message=" + message
				+ ", callback=" + callback+ "]";
	}

}
