package sms;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.smslib.AGateway;
import org.smslib.AGateway.GatewayStatuses;
import org.smslib.GatewayException;
import org.smslib.ICallNotification;
import org.smslib.IGatewayStatusNotification;
import org.smslib.IInboundMessageNotification;
import org.smslib.IOrphanedMessageNotification;
import org.smslib.IOutboundMessageNotification;
import org.smslib.InboundMessage;
import org.smslib.Library;
import org.smslib.Message.MessageEncodings;
import org.smslib.Message.MessageTypes;
import org.smslib.OutboundMessage;
import org.smslib.OutboundTTSMessage;
import org.smslib.OutboundTTSMessage.CallStatuses;
import org.smslib.SMSLibException;
import org.smslib.Service;
import org.smslib.Service.ServiceStatus;
import org.smslib.TimeoutException;
import org.smslib.modem.SerialModemGateway;

import sms.data.CallStatusConverter;
import sms.data.CommandSendResult;
import sms.data.Message;
import sms.data.MessageType;
import utils.DateUtil;

/**
 * 该类代表猫设备。
 * 运行前必须把WEB-INF/lib/中的win32com.dll拷贝到jdk的bin目录或者tomcat的bin目录。
 * 而且必须用32位的jdk。
 */
public class Modem {
	private static final Logger log = Logger.getLogger(Modem.class);
	
	private SerialModemGateway gateway;
	private String modelId = "model.com3";
	private String comPort = "COM3";
	private int baudRate = 115200;
	private boolean isNeedStart = true;
	/**
	 * 定时检查短信猫连接状态
	 */
	private ScheduledExecutorService scheduler;
	
	public Modem(int comPort, int baudRate) {
		this.modelId = "model.com" + comPort;
		this.comPort = "COM" + comPort;
		this.baudRate = baudRate;
		statusChecker();
	}

	private void statusChecker(){
		scheduler = Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
			@Override
			public Thread newThread(Runnable r) {
				return new Thread(r, "短息服务状态检查服务_" + r.hashCode());
			}
		});
		scheduler.scheduleWithFixedDelay(new Runnable() {
			public void run() {
				if(!hasStarted()){
					destroy();
					try {
						Thread.sleep(30000);
					} catch (InterruptedException e) {
					}
					start();
				}
			}
		}, 5, 5, TimeUnit.MINUTES);
	}
	
	public synchronized boolean hasStarted() {
		return Service.getInstance().getServiceStatus() == ServiceStatus.STARTED;
	}
	
	public synchronized void start() {
		if (!isNeedStart) {
			return;
		}
		System.out.println("Example: Send message from a serial gsm modem.");
		System.out.println(Library.getLibraryDescription());
		System.out.println("Version: " + Library.getLibraryVersion());
		// 设置串口指令超时时间
//		System.setProperty("smslib.serial.timeout", "10000");
		
		// 获取短信配置的信息
		// ---------------创建串口设备，如果有多个，就创建多个--------------
		// 1、modem.com1:网关ID（即短信猫端口编号）
		// 2、com口名称，如COM1或/dev/ttyS1（根据实际情况修改）
		// 3、串口波特率，如9600（根据实际情况修改）
		// 4、开发商
		// 5、型号
		gateway = new SerialModemGateway(modelId, comPort, baudRate, "wavecom", "");
		gateway.setInbound(true); // 设置true，表示该网关可以接收短信
		gateway.setOutbound(true); // 设置true，表示该网关可以发送短信
		// -----------------创建发送短信的服务（它是单例的）----------------
		Service.getInstance().setOutboundMessageNotification(new OutboundNotification()); // 发送短信成功后的回调函方法
		Service.getInstance().setInboundMessageNotification(new InboundNotification());
		Service.getInstance().setCallNotification(new CallNotification()); // 电话call
		Service.getInstance().setGatewayStatusNotification(new GatewayStatusNotification());
		Service.getInstance().setOrphanedMessageNotification(new OrphanedMessageNotification());

		Service.getInstance().getSettings().MASK_IMSI = false;
		Service.getInstance().S.SERIAL_POLLING = true;
		// ---------------------- 将设备加到服务中----------------------
		try {
			Service.getInstance().addGateway(gateway);
			// ------------------------- 启动服务 -------------------------
			Service.getInstance().startService();
			System.out.println("短信/语音猫信息:");
			System.out.println("  Manufacturer: " + gateway.getManufacturer());
			System.out.println("  Model: " + gateway.getModel());
			System.out.println("  Serial No: " + gateway.getSerialNo());
			System.out.println("  SIM IMSI: " + gateway.getImsi());
			System.out.println("  Signal Level: " + gateway.getSignalLevel() + " dBm");
			System.out.println("  Battery Level: " + gateway.getBatteryLevel() + "%");
		} catch (GatewayException e) {
			e.printStackTrace();
			log.error("无法正常链接gsm网络");
			// 原因1：SIM卡没插好
			//
			// 解决1：请重新插卡
			//
			// 原因2：SIM卡没钱了
			//
			// 解决2：请查看账户余额，或打电话试试
			//
			// 原因3：周围的网络信号不强，导致SIM卡无法连接到网络
			//
			// 解决3：请使用自己的手机进行移动信号强度测试
		} catch (TimeoutException e) {
			e.printStackTrace();
		} catch (SMSLibException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/*
	 * 短信发送成功后，调用该接口。并将发送短信的网关和短信内容对象传给process接口
	 */
	public class OutboundNotification implements IOutboundMessageNotification {
		@SuppressWarnings("static-access")
		public void process(AGateway gateway, OutboundMessage msg) {
			System.out.println("Outbound handler called from Gateway: " + gateway.getGatewayId());
			System.out.println("成功了吗？");
			for (OutboundMessage.MessageStatuses c : msg.getMessageStatus().values()) {
				System.out.println(c);
			}
		}
	}

	public class InboundNotification implements IInboundMessageNotification {
		@Override
		public void process(AGateway gateway, MessageTypes msgType, InboundMessage msg) {
			log.info(DateUtil.dateToStr(msg.getDate()) + " - " + msg.getOriginator() + ":" + msg.getText());
			try {
				// TODO: 接收短信后可判断处理业务逻辑
				// 收到后要删除否则此回调一直调用
				gateway.deleteMessage(msg);
			} catch (TimeoutException e) {
				e.printStackTrace();
			} catch (GatewayException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public class CallNotification implements ICallNotification {
		public void process(AGateway gateway, String callerId) {
			System.out.println(">>> New call detected from Gateway: " + gateway.getGatewayId() + " : " + callerId);
		}
	}

	public class GatewayStatusNotification implements IGatewayStatusNotification {
		public void process(AGateway gateway, GatewayStatuses oldStatus, GatewayStatuses newStatus) {
			System.out.println(">>> Gateway Status change for " + gateway.getGatewayId() + ", OLD: " + oldStatus
					+ " -> NEW: " + newStatus);
		}
	}

	public class OrphanedMessageNotification implements IOrphanedMessageNotification {
		public boolean process(AGateway gateway, InboundMessage msg) {
			System.out.println(DateUtil.dateToStr(msg.getDate()) + " - " + msg.getOriginator() + ":" + msg.getText());
			// Since we are just testing, return FALSE and keep the orphaned
			// message part.
			return false;
		}
	}

	public synchronized void destroy() {
		// ------------------------- 关闭服务 -------------------------
		try {
			if (isNeedStart) {
				Service.getInstance().stopService();
				gateway.stopGateway();
				Service.getInstance().removeGateway(gateway);
			}
			if (scheduler != null) {
				scheduler.shutdown();
			}
		} catch (TimeoutException e) {
			e.printStackTrace();
		} catch (GatewayException e) {
			e.printStackTrace();
		} catch (SMSLibException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public synchronized CommandSendResult sendMsg(Message message) {
		checkMessage(message);
		CommandSendResult commandSendResult = sendMsgWithResult(message.getId(), message.getPhoneNum(), message.getText());
		return commandSendResult;
	}
	
	private void checkMessage(Message message) {
		if (message == null || message.getText() == null || message.getText().trim().length() == 0) {
			throw new RuntimeException("消息不能为空");
		}
		if (message.getPhoneNum() == null || message.getPhoneNum().trim().length() == 0) {
			throw new RuntimeException("电话号码不能为空");
		}
	}
	
	private CommandSendResult sendMsgWithResult(String messageId, String phoneNumber, String text) {
		boolean sendSuccess = sendMsg(phoneNumber, text);
		return new CommandSendResult(
				messageId, 
				MessageType.SMS, 
				phoneNumber, 
				text,
				sendSuccess);
	}
	
	private boolean sendMsg(String phoneNumber, String text) {
		OutboundMessage msg = new OutboundMessage(phoneNumber, text);
		msg.setEncoding(MessageEncodings.ENCUCS2);
		boolean sendSuccess = false;
		try {
			sendSuccess = Service.getInstance().sendMessage(msg);
		} catch (TimeoutException e) {
			e.printStackTrace();
		} catch (GatewayException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			log.error("短信发送服务可能未正常启动！！！");
		}
		return sendSuccess;
	}
	
	public synchronized CommandSendResult textToSpeech(Message message) {
		checkMessage(message);
		
		CallStatuses callStatuses = textToSpeech(message.getPhoneNum(), message.getText());
		CommandSendResult commandSendResult = new CommandSendResult(
				message.getId(),
				MessageType.TTS, 
				message.getPhoneNum(), 
				message.getText(), 
				CallStatusConverter.fromCallStatusInLib(callStatuses));
		// 对于呼叫，只要不是出错，多认为消息已发送，由调用者再根据status 进一步判断
		commandSendResult.setSendSuccess(!callStatuses.equals(CallStatuses.ERROR));
		return commandSendResult;
	}
	
	private synchronized OutboundTTSMessage.CallStatuses textToSpeech(String phoneNumber, String text) {
		log.info("开始呼叫" + phoneNumber + "...");
		OutboundTTSMessage msg = new OutboundTTSMessage(phoneNumber, text);
		OutboundTTSMessage.CallStatuses sendResult = OutboundTTSMessage.CallStatuses.ERROR;
		try {
			sendResult = Service.getInstance().textToSpeech(msg);
		} catch (TimeoutException e) {
			e.printStackTrace();
		} catch (GatewayException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (Exception e) {
			log.error("modem服务可能未正常启动！！！");
		}
		return sendResult;
	}
	
//	private long lastTextToSpeechTime = 0L;
//	private static final long MIN_INTERVAL_IN_MILLI_SECONDS = 3000L;
//	/**
//	 * 如果连续两次的调用时间太接近会出错，所以，要加一些时间间隙
//	 */
//	private void waitForTimeInternal() {
//		long nowTime = System.currentTimeMillis();
//		long interval = nowTime - lastTextToSpeechTime;
//		if (interval > MIN_INTERVAL_IN_MILLI_SECONDS) {
//			return;
//		} else {
//			log.info("两次调用时间间隔不够" + MIN_INTERVAL_IN_MILLI_SECONDS + "毫秒，等待时间间隔到达中...");
//			try {
//				Thread.sleep(MIN_INTERVAL_IN_MILLI_SECONDS - interval);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//		}
//	}
}
