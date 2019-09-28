package sms.command;

import org.apache.log4j.Logger;

import sms.command.callback.CommandCallback;
import sms.data.CommandSendResult;
import sms.data.Message;
import sms.data.MessageType;

public class TTSCommand extends Command {
	private static final Logger log = Logger.getLogger(TTSCommand.class);

	public TTSCommand(Message message, CommandCallback callback) {
		super(MessageType.TTS, message, callback);
	}

	@Override
	public void run() {
		CommandSendResult result = getModem().textToSpeech(getMessage());
		log.info(result.getPhoneNum() + ":" + result.getCallStatus());
		
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
