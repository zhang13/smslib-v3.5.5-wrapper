package sms.data;

import java.util.Date;

public class CommandSendResult {
	private String messageId;
	private MessageType messageType;
	private String phoneNum;
	private String text;
	private boolean sendSuccess;
	/**
	 * 呼叫状态。
	 * 对于呼叫成功的判断，不仅仅要判断sendSuccess为true，可以要判断callStatus的值，详见CallStatus的枚举定义。
	 */
	private CallStatus callStatus;
	private Date sendTime;
	
	public CommandSendResult() {
	}

	public CommandSendResult(String messageId, MessageType messageType, String phoneNum, String text, boolean sendSuccess) {
		this.messageId = messageId;
		this.messageType = messageType;
		this.text = text;
		this.phoneNum = phoneNum;
		this.sendSuccess = sendSuccess;
		this.sendTime = new Date();
	}

	public CommandSendResult(String messageId, MessageType messageType, String phoneNum, String text, CallStatus callStatus) {
		this.messageId = messageId;
		this.messageType = messageType;
		this.phoneNum = phoneNum;
		this.text = text;
		this.callStatus = callStatus;
		this.sendTime = new Date();
	}

	public Date getSendTime() {
		return sendTime;
	}

	public void setSendTime(Date sendTime) {
		this.sendTime = sendTime;
	}

	public String getPhoneNum() {
		return phoneNum;
	}

	public void setPhoneNum(String phoneNum) {
		this.phoneNum = phoneNum;
	}

	public boolean isSendSuccess() {
		return sendSuccess;
	}

	public void setSendSuccess(boolean sendSuccess) {
		this.sendSuccess = sendSuccess;
	}

	public CallStatus getCallStatus() {
		return callStatus;
	}

	public void setCallStatus(CallStatus callStatus) {
		this.callStatus = callStatus;
	}

	public MessageType getMessageType() {
		return messageType;
	}

	public void setMessageType(MessageType messageType) {
		this.messageType = messageType;
	}
	
	public String getMessageId() {
		return messageId;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	@Override
	public String toString() {
		return "SmsSendResult ["
				+ "messageId=" + messageId 
				+ ", messageType="+ messageType 
				+ ", phoneNum="+ phoneNum 
				+ ", text="+ text 
				+ ", sendSuccess="+ sendSuccess 
				+ ", callStatus=" + callStatus
				+ ", sendTime=" + sendTime 
				+ "]";
	}

}
