package sms.command.callback;

import org.apache.log4j.Logger;

import sms.data.CommandSendResult;

/**
 * 通过http url进行回调的回调实现。
 */
public class HttpCommandCallback implements CommandCallback {
	private static final Logger log = Logger.getLogger(HttpCommandCallback.class);
	
	private String url;
	
	public HttpCommandCallback(String url) {
		this.url = url;
	}

	@Override
	public void onComplete(CommandSendResult result) {
		final int maxRetryTimes = 1;
		int retryTimes = 0;
		while (true) {
			if (retryTimes > 0) {
				sleepFor(3000);
			}
			
//			RestTemplate restTemplate = new RestTemplate();
//			try {
//				restTemplate.postForObject(url, result, Void.class);
//				log.info("调用回调成功.");
//				return;
//			} catch (Exception e) {
//				retryTimes++;
//				if (retryTimes > maxRetryTimes) {
//					throw new RuntimeException("调用消息的回调接口失败，错误原因: " + e.getMessage() 
//						+ ".要回传的信息:" + result, e);
//				}
//				log.error("调用消息的回调接口失败，错误原因: " + e.getMessage() 
//							+ ". 要回传的信息:" + result + ",等待3秒钟将重试", e);
//				continue;
//			}
		}
	}
	
	private void sleepFor(int millis){
		long now = System.currentTimeMillis();
		try {
			while(true){
				Thread.sleep(1000);
				if (System.currentTimeMillis() - now >= millis) {
					break;
				}
			}
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public String toString() {
		return "HttpCommandCallback [url=" + url + "]";
	}

}
