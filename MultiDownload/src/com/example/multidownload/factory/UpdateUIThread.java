package com.example.multidownload.factory;

import java.io.File;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class UpdateUIThread extends Thread {
	private static final String TAG = "MultiThreadDownload";
	/*** 文件大小 */
	private int fileSize;
	/** * 已经下载多少 */
	private int downloadSize;
	/**文件的url,线程编号，文件名称*/
	private String urlStr,threadNo,fileName;
	/***保存的路径*/
	private String savePath;
	/**下载的百分比*/
	private int downloadPercent = 0;
	/**下载的 平均速度*/
	private int downloadSpeed = 0;
	/**下载用的时间*/
	private int usedTime = 0;
	/**开始时间*/
	private long startTime;
	/**当前时间*/
	private long curTime;
	/**是否已经下载完成*/
	private int threadCount=3;
	public final static int DOWNLOAD_ERROR=1;
	public final static int DOWNLOAD_SUCCESS=2;
	public final static int SERVER_ERROR=3;
	private boolean completed = false;
	private Handler handler;
	public UpdateUIThread(String urlStr, String fileName,
			String savePath, Handler handler) {
		super();
		this.urlStr = urlStr;
		this.fileName = fileName;
		this.savePath = savePath;
		this.handler = handler;
		Log.e(TAG, toString());
	}
	public void run(){
		try {
			URL url=new URL(urlStr);
			HttpURLConnection connection=(HttpURLConnection) url.openConnection();
			connection.setConnectTimeout(5000);
			connection.setRequestMethod("GET");
			int code = connection.getResponseCode();
			if(code==200){
				int length = connection.getContentLength();
				Log.e(TAG, "文件一共："+length);
				File file=new File(savePath+fileName);
				RandomAccessFile raf=new RandomAccessFile(file, "rwd");
				raf.setLength(length);
				raf.close();
				int blockSize=length/threadCount;
				for(int threadId=1;threadId<=threadCount;threadId++){
					int startIndex = (threadId - 1) * blockSize;
					int endIndex = threadId * blockSize - 1;
					if (threadId == threadCount) {// 如果文件不能整除，最后一个线程要稍稍多下载一些
						endIndex = length;
					}
					new ThreadDownload(urlStr, startIndex, endIndex, threadId,savePath+File.separator)
					.start();
				}
			}
			else {
				sendMsg(DOWNLOAD_ERROR);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			sendMsg(SERVER_ERROR);
		}
	}
	public void sendMsg(int what)
	{
		Message msg = new Message();
		msg.what = what;
		handler.sendMessage(msg);
	}
}
