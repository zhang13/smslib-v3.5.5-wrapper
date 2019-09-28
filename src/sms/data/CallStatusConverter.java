package sms.data;

import org.smslib.OutboundTTSMessage.CallStatuses;

public class CallStatusConverter {
	/**
	 * 把库返回的呼叫状态枚举转换成我们自己的，便于客户端处理，我们自己的呼叫状态枚举位于我们自己的包里面：
	 */
	public static CallStatus fromCallStatusInLib(CallStatuses callStatuses) {
		return CallStatus.valueOf(callStatuses.name());
	}
}
