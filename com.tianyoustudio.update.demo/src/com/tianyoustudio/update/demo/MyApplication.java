package com.tianyoustudio.update.demo;

import com.tianyoustudio.update.library.BaseUpdateApplication;

public class MyApplication extends BaseUpdateApplication {

	@Override
	public void onCreate() {
		super.onCreate();
		UpdateConfig.init(this);
	}

	@Override
	public String UpdateProcessName() {
		return "com.tianyoustudio.update.demo";
	}

}
