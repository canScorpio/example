package com.example.multidownload;

import java.io.File;

import com.example.multidownload.factory.UpdateUIThread;
import com.example.multidownload.tools.FileUtil;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener {
	private Button bt_download;
	private ProgressBar pb_download;
	private UpdateUIThread updateUIThread;
	private final static int DOWNLOAD_ERROR=1;
	private final static int DOWNLOAD_SUCCESS=2;
	private final static int SERVER_ERROR=3;
	private final static int END_DOWNLOAD=4;
	private String urlStr="http://gdown.baidu.com/data/wisegame/3c00add7144d3915/kugouyinle.apk";
	private Handler handler=new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case DOWNLOAD_ERROR:
				Toast.makeText(MainActivity.this, "shibai", Toast.LENGTH_SHORT).show();
				break;
			case DOWNLOAD_SUCCESS:
				Toast.makeText(MainActivity.this, "下载成功", Toast.LENGTH_SHORT).show();
				
				break;
			case SERVER_ERROR:
				Toast.makeText(MainActivity.this, "服务器错误", Toast.LENGTH_SHORT).show();
				
				break;

			
			}
		};
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		bt_download=(Button) this.findViewById(R.id.bt_download);
		pb_download=(ProgressBar) this.findViewById(R.id.pb_download);
		bt_download.setOnClickListener(this);
		updateUIThread=new UpdateUIThread(urlStr, FileUtil.getFileName(urlStr), FileUtil.setMkdir(this)+File.separator, handler);
	}
	public void onClick(View v){
		updateUIThread.start();
	}
}

	