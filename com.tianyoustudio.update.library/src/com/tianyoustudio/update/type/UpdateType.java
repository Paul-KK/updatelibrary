/******************************************************************
 *
 *
 *    CopyRight © 2015-2017 TianYouStudio.All Rights Reserved.    
 *     
 *
 *****************************************************************/
package com.tianyoustudio.update.type;

/**
 * @ClassName UpdateType
 * @author Fsk E-mail:1473985844@qq.com
 * @Date 2017年1月1日 下午7:44:57
 * @version 1.0.0
 * @Description TODO(这里用一句话描述这个类的作用)
 */
public enum UpdateType {
	/** 手动检测更新 */
	forcecheck,
	/** 3g/4g网络也自动检测更新 */
	autocheck,
	/** （只有）WiFi情况下检测更新 */
	wifiautocheck,
	/** 3g/4g网络也自动下载 */
	autodown,
	/** （只有）WiFi情况下自动下载 */
	wifiautodown
}
