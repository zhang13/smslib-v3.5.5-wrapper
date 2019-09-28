package sms.command;

import org.apache.log4j.Logger;

import sms.command.callback.CommandCallback;
import sms.data.CommandSendResult;
import sms.data.Message;
import sms.data.MessageType;

public class SmsCommand extends Command {
	private static final Logger log = Logger.getLogger(SmsCommand.class);

	public SmsCommand(Message message, CommandCallback callback) {
		super(MessageType.SMS, message, callback);
	}

	@Override
	public void run() {
		CommandSendResult result = getModem().sendMsg(getMessage());
		if (result.isSendSuccess()) {
			log.info("短信发送成功：" + result.getPhoneNum());
		} else {
			log.info("短信发送失败：" + result.getPhoneNum());
		}
		
		CommandCallback callback = getCallback();
		if (callback != null) {
			log.info("开始调用回调:" + callback);
			try {
				callback.onComplete(result);
			} catch (Exception e) {
				log.error("调用回调失败:" + callback, e);
			}
		}
	}
}
