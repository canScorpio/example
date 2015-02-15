package com.example.readhtmlcode.streamtools;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author zc
 * ���ֽ���ת�����ַ����Ĺ�����
 */
public class StreamTools {
	public static String readInputStream(InputStream inputStream){
		ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
		byte []bytes=new byte[1024];
		int len=0;
		try {
			while((len=inputStream.read(bytes))!=-1){
				byteArrayOutputStream.write(bytes,0,len);
			}
			inputStream.close();
			byte[] result = byteArrayOutputStream.toByteArray();
			return new String(result);
		} 
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "��ȡʧ��";
		}
		
	}
}
