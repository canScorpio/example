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
				startIndex = downloadlength;// ��startIndex����Ϊ�ϴζϵ�����ʱ��ֵ
				System.out.println("��ʵ����λ��" + startIndex + "----");
				fis.close();
			}
			URL url = new URL(urlStr);
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			connection.setConnectTimeout(5000);
			connection.setRequestMethod("GET");
			// ������������ز��ֵ��ļ���ָ���ļ���λ��
			connection.setRequestProperty("Range", "bytes=" + startIndex
					+ "-" + endIndex);
			int code = connection.getResponseCode();// �ӷ���������ȫ������Դ200�����󲿷���Դ206
			if(code==206){
				InputStream is = connection.getInputStream();// ��ȡ������
				RandomAccessFile raf=new RandomAccessFile(savePath+FileUtil.getFileName(urlStr), "rwd");
				raf.seek(startIndex);// ������startIndexλ��
				byte[] bytes = new byte[1024];
				int total=0;//�Ѿ����ص����ݳ���
				int len = 0;
				while ((len = is.read(bytes)) != -1) {
					//���ã�������¼��ǰ�߳��������ݵĳ���
					RandomAccessFile file=new RandomAccessFile(savePath+threadId+".txt", "rwd");
					raf.write(bytes, 0, len);
					total+=len;
					System.out.println("�߳�"+threadId+"total:"+total);
					file.write(String.valueOf(total+startIndex).getBytes());//��¼�Ѿ����ص�λ��
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
			//���ļ����سɹ���ɾ����¼���ļ�
			System.out.println(runningThread);
			if(runningThread==0){
				for(int threadId=1;threadId<=3;threadId++){
					File file=new File(savePath+threadId+".txt");
					file.delete();
				}
				System.out.println("�ļ�������ϣ�ɾ���������ؼ�¼");
			}
		}
		super.run();
	}

}
