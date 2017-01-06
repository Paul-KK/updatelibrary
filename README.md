# updatelibrary
安卓自动更新实现库

部分代码参考自 https://github.com/jjdxmashl/jjdxm_update 并做了适当修改，如果侵犯到了作者权益，请联系删除

## 一、特点:    
	1.支持强制检测更新，（3g/4g）自动检测更新、WiFi下自动检测更新、（3g/4g）自动下载更新、WiFi下自动下载更新以及忽略更新检；
	2.支持通知栏提醒;
	3.支持通知栏（暂停/继续）断点续传；
	4.自定义更新提醒对话框；
	5.更新下载完成后在当前应用前台activity中弹出对话框（app全局提醒）。 
    
## 二、使用方法，参考demo
	1.需要服务器返回数据类型如下
    
	{"code":0,"msg":"success","data":  	{"app_key":"xxx","versionCode":1,"versionName":"1.0","down_name":"xxx","md5":"xxx","describe":"xxx","size":1111,"download_url":"xxx","time":123}}

	2.配置UpdateConfig，具体配置方法参照demo，
	3.自定义一个继承自BaseUpdateApplication的Application，在onCreate方法中初始化UpdateConfig，然后在AndroidManifest中添加自定义的Application即可。
		
	@Override
	public void onCreate() {
		super.onCreate();
		UpdateConfig.init(this);
	}


	4.在需要更新的activity调用更新，具体调用方法参照demo
