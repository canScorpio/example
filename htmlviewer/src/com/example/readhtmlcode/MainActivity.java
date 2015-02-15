package com.example.readhtmlcode;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.readhtmlcode.streamtools.StreamTools;

public class MainActivity extends Activity {

	private EditText et_path;
	private TextView tv_content;
	private final static int ERROR=1;
	private final static int SHOW_TEXT=2;
	//定义一个消息处理器
	private Handler handler=new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case ERROR:
				Toast.makeText(MainActivity.this, "获取数据失败", Toast.LENGTH_SHORT).show();
				break;
			case SHOW_TEXT:
				String result=(String)msg.obj;
				tv_content.setText(result);
				break;

			
			}
		};
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initView();
	}
	//初始化控件
	private void initView() {
		et_path=(EditText) this.findViewById(R.id.et_path);
		tv_content=(TextView)this.findViewById(R.id.tv_content);
	}
	//button的点击事件
	public void click(View view){
		final String pathString=et_path.getText().toString().trim();
		if(TextUtils.isEmpty(pathString)){
			Toast.makeText(this, "路径不能为空", Toast.LENGTH_SHORT).show();
		}else{
			//在子线程中进行网络连接
			new Thread(){
				public void run() {
					try {
						URL url=new URL(pathString);
						HttpURLConnection connection=(HttpURLConnection) url.openConnection();
						connection.setRequestMethod("GET");
						connection.setConnectTimeout(5000);
						if(connection.getResponseCode()==200){
							InputStream inputStream=connection.getInputStream();
							String resultString=StreamTools.readInputStream(inputStream);
							//通过消息机制将传递消息到UI线程改变UI
							Message msg=new Message();
							msg.what=SHOW_TEXT;
							msg.obj=resultString;
							handler.sendMessage(msg);
							inputStream.close();
						}else {
							Message msg=new Message();
							msg.what=ERROR;
							handler.sendMessage(msg);
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						Message msg=new Message();
						msg.what=ERROR;
						handler.sendMessage(msg);
						e.printStackTrace();
					}
				};
			}.start();
		}
	}

}

