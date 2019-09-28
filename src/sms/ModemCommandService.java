package sms;

import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import sms.command.Command;

/**
 * 该类通过Modem对象连接一个设备，接收发送语音呼叫或短信发送命令,通过队列排队发送各个指令。
 */
public class ModemCommandService {
	/**
	 * 用来提交短信或语音命令。使用优先级队列，实现短信优先发送，因为短信发送比较快。
	 */
	private ThreadPoolExecutor executor;
	private int comPort = 3;
	private int bautRate = 115200;
	private Modem sender;
	private final int MAX_QUEUE_SIZE = 10;
	private final long KEEP_ALIVE_TIME = Long.MAX_VALUE;

	public ModemCommandService(int comPort, int bautRate) {
		this.comPort = comPort;
		this.bautRate = bautRate;

		executor = new ThreadPoolExecutor(1, MAX_QUEUE_SIZE, KEEP_ALIVE_TIME, TimeUnit.SECONDS,
				new PriorityBlockingQueue<Runnable>(), new ThreadFactory() {
					@Override
					public Thread newThread(Runnable r) {
						return new Thread(r, "指令调度服务_" + r.hashCode());
					}
				});
	}

	public void init() {
		sender = new Modem(comPort, bautRate);
		sender.start();
	}

	public void add(Command command) {
		command.setSender(sender);
		if (!sender.hasStarted()) {
			throw new RuntimeException("设备尚没有连接!");
		}
		executor.execute(command);
	}

	public boolean senderStarted() {
		return sender.hasStarted();
	}

	public boolean isEmpty() {
		return executor.getQueue().size() == 0;
	}

	public void destroy() {
		if (executor != null) {
			executor.shutdown();
		}
		if (sender != null) {
			sender.destroy();
		}
	}

	public int getComPort() {
		return comPort;
	}

	public void setComPort(int comPort) {
		this.comPort = comPort;
	}

	public int getBautRate() {
		return bautRate;
	}

	public void setBautRate(int bautRate) {
		this.bautRate = bautRate;
	}
}
