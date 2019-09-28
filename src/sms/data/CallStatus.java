package sms.data;

public enum CallStatus {
	/**
	 * �����쳣
	 */
	ERROR("ERROR", "�����쳣"),
	/**
	 * ���ź�
	 */
	NO_SINGAL("NO_SINGAL", "���ź�"),
	/**
	 * δ����
	 */
	NO_ANSWER("NO_ANSWER", "δ����"),
	/**
	 * �Ҷ�
	 */
	HANG_UP("HANG_UP", "�Ҷ�"),
	/**
	 * �����Ҷ�
	 */
	ANSWER_HANG_UP("ANSWER_HANG_UP", "�����Ҷ�"),
	/**
	 * �ȴ�����������ɳ�ʱ
	 */
	TIME_OUT("TIME_OUT", "�ȴ�����������ɳ�ʱ"),
	/**
	 * �������
	 */
	OK("OK", "�������");
	
	private final String code;
	private final String description;
	
	private CallStatus(String code, String description) {
		this.code = code;
		this.description = description;
	}

	public String getCode() {
		return code;
	}

	public String getDescription() {
		return description;
	}
	
}
