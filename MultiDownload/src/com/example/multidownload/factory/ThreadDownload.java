package com.example.multidownload.factory;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

import com.example.multidownload.tools.FileUtil;

public class ThreadDownload extends Thread {

	private String urlStr;
	private int startIndex;
	private int endIndex;
	private int threadId;
	private String savePath;
	private static int runningThread=3;
	public ThreadDownload(String urlStr, int startIndex, int endIndex,
			int threadId, String savePath) {
		// TODO Auto-generated constructor stub
		super();
		this.urlStr = urlStr;
		this.startIndex = startIndex;
		this.endIndex = endIndex;
		this.threadId = threadId;
		this.savePath = savePath;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		File tempFile = new File(savePath + threadId + ".txt");
		try {
			if (tempFile.exists() && tempFile.length() > 0) {
				FileInputStream fis;
				fis = new FileInputStream(tempFile);
				int templength = 0;
				byte[] bytes = new byte[1024];
				templength = fis.read(bytes);
				int downloadlength = Integer.parseInt(new String(bytes, 0,
						templength));
				startIndex = downloadlength;// 将startIndex设置为上次断点下载时的值
				System.out.println("真实下载位置" + startIndex + "----");
				fis.close();
			}
			URL url = new URL(urlStr);
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			connection.setConnectTimeout(5000);
			connection.setRequestMethod("GET");
			// 请求服务器下载部分的文件的指定文件的位置
			connection.setRequestProperty("Range", "bytes=" + startIndex
					+ "-" + endIndex);
			int code = connection.getResponseCode();// 从服务器请求全部的资源200，请求部分资源206
			if(code==206){
				InputStream is = connection.getInputStream();// 获取输入流
				RandomAccessFile raf=new RandomAccessFile(savePath+FileUtil.getFileName(urlStr), "rwd");
				raf.seek(startIndex);// 搜索到startIndex位置
				byte[] bytes = new byte[1024];
				int total=0;//已经下载的数据长度
				int len = 0;
				while ((len = is.read(bytes)) != -1) {
					//作用：用来记录当前线程下载数据的长度
					RandomAccessFile file=new RandomAccessFile(savePath+threadId+".txt", "rwd");
					raf.write(bytes, 0, len);
					total+=len;
					System.out.println("线程"+threadId+"total:"+total);
					file.write(String.valueOf(total+startIndex).getBytes());//记录已经下载的位置
					file.close();
				}
				is.close();
				raf.close();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}
		finally{
			runningThread--;
			//当文件下载成功，删除记录的文件
			System.out.println(runningThread);
			if(runningThread==0){
				for(int threadId=1;threadId<=3;threadId++){
					File file=new File(savePath+threadId+".txt");
					file.delete();
				}
				System.out.println("文件下载完毕，删除所有下载记录");
			}
		}
		super.run();
	}

}
