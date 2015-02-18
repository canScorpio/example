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
	/*** �ļ���С */
	private int fileSize;
	/** * �Ѿ����ض��� */
	private int downloadSize;
	/**�ļ���url,�̱߳�ţ��ļ�����*/
	private String urlStr,threadNo,fileName;
	/***�����·��*/
	private String savePath;
	/**���صİٷֱ�*/
	private int downloadPercent = 0;
	/**���ص� ƽ���ٶ�*/
	private int downloadSpeed = 0;
	/**�����õ�ʱ��*/
	private int usedTime = 0;
	/**��ʼʱ��*/
	private long startTime;
	/**��ǰʱ��*/
	private long curTime;
	/**�Ƿ��Ѿ��������*/
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
				Log.e(TAG, "�ļ�һ����"+length);
				File file=new File(savePath+fileName);
				RandomAccessFile raf=new RandomAccessFile(file, "rwd");
				raf.setLength(length);
				raf.close();
				int blockSize=length/threadCount;
				for(int threadId=1;threadId<=threadCount;threadId++){
					int startIndex = (threadId - 1) * blockSize;
					int endIndex = threadId * blockSize - 1;
					if (threadId == threadCount) {// ����ļ��������������һ���߳�Ҫ���Զ�����һЩ
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
