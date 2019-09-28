package sms.data;

public class Message {
	private String id;
	private String phoneNum;
	/**
	 * 针对语音播放，文本长度最大为512
	 */
	private String text;
	/**
	 * 接收发送结果的回调地址
	 */
	private String callbackUrl;
	
	public Message() {
	}

	public Message(String id, String phoneNum, String text, String callbackUrl) {
		this.id = id;
		this.phoneNum = phoneNum;
		this.text = text;
		this.callbackUrl = callbackUrl;
	}
	
	@Override
	public String toString() {
		return "SendMessage ["
				+ "id=" + id 
				+ ", phoneNum=" + phoneNum 
				+ ", text=" + text 
				+ ", callbackUrl=" + callbackUrl 
				+ "]";
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPhoneNum() {
		return phoneNum;
	}

	public void setPhoneNum(String phoneNum) {
		this.phoneNum = phoneNum;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getCallbackUrl() {
		return callbackUrl;
	}

	public void setCallbackUrl(String callbackUrl) {
		this.callbackUrl = callbackUrl;
	}
	
}
