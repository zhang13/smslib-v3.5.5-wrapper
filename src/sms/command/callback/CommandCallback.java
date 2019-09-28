package sms.command.callback;

import sms.data.CommandSendResult;

/**
 * 命令执行完成后回调的接口。不管成功与失败，都会回调。
 */
public interface CommandCallback {
	void onComplete(CommandSendResult result);
}
