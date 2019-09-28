package sms;
import java.util.concurrent.CountDownLatch;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import sms.ModemCommandService;
import sms.command.SmsCommand;
import sms.command.TTSCommand;
import sms.command.callback.CommandCallback;
import sms.data.CommandSendResult;
import sms.data.Message;

public class ModemCommandServiceSysTests {
	private static String phoneNum = "13400000000";
	private static int comPort = 3;
	private static int bautRate = 115200;
	
	private CountDownLatch waitForCompleteLatch;
	private CommandCallback waitForCompleteCallback;
	// 记录回调传递的结果
	private CommandSendResult commandSendResult;	
	
	private ModemCommandService modemCommandService;
	
	@Before
	public void setUp() {
		waitForCompleteLatch = new CountDownLatch(1);
		waitForCompleteCallback = new CommandCallback() {
			@Override
			public void onComplete(CommandSendResult result) {
				commandSendResult = result;
				waitForCompleteLatch.countDown();
			}
		};
		
		modemCommandService = new ModemCommandService(comPort, bautRate);
		modemCommandService.init();
	}
	
	@Test
	public void call() throws Exception {
		String msg = "第1个,测试消息测试消息测试消息测试消息测试消息测试消息测试消息测试消息测试消息测试消息测试消息测试消息测试消息测试消息测试消息测试消息测试消息测试消息测试消息测试消息测试消息测试消息测试消息测试消息测试消息测试消息测试消息测试消息测试消息测试消息测试消息测试消息测试消息测试消息测试消息测试消息测试消息测试消息测试消息测试消息测试消息测试消息测试消息测试消息测试消息测试消息测试消息测试消息测试消息测试消息测试消息测试消息测试消息测试消息测试消息测试消息测试消息测试消息测试消息测试消息测试消息测试消息测试消息测试消息测试消息测试消息测试消息测试消息测试消息测试消息测试消息测试消息测试消息测试消息测试消息测试消息测试消息测试消息测试消息测试消息测试消息测试消息测试消息测试消息测试消息测试消息测试消息测试消息测试消息测试消息测试消息测试消息测试消息测试消息测试消息测试消息测试消息测试消息测试消息测试消息测试消息测试消息测试消息测试消息测试消息测试消息测试消息测试消息测试消息测试消息测试消息测试消息测试消息测试消息测试消息测试消息测试消息测试消息测试消息测试消息测试消息测试消息测试消息测试消息测试消息测试消息测试消息";
		System.out.println("msg length:" + msg.length());
		Message message = new Message("id1", phoneNum, msg, null);
		modemCommandService.add(new TTSCommand(message, waitForCompleteCallback));
		
		waitForCompleteLatch.await();
		System.out.println("执行完成,结果：" + commandSendResult);
	}
	
	@Test
	public void sendShortMessage() throws Exception {
		Message message = new Message("id1", phoneNum, "测试消息，第1个", null); 
		modemCommandService.add(new SmsCommand(message, waitForCompleteCallback));
		
		waitForCompleteLatch.await();
		System.out.println("执行完成,结果：" + commandSendResult);
	}
	
	@After
	public void tearDown() {
		modemCommandService.destroy();
	}
	
}
