package sms.command.callback;

import sms.data.CommandSendResult;

/**
 * ����ִ����ɺ�ص��Ľӿڡ����ܳɹ���ʧ�ܣ�����ص���
 */
public interface CommandCallback {
	void onComplete(CommandSendResult result);
}
