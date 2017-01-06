package com.tianyoustudio.update.library.download;

/**
 * ========================================
 * <p>
 * 版 权：深圳市晶网电子科技有限公司 版权所有 （C） 2015
 * <p>
 * 作 者：陈冠明
 * <p>
 * 个人网站：http://www.dou361.com
 * <p>
 * 版 本：1.0
 * <p>
 * 创建日期：2015-10-6 下午3:29:29
 * <p>
 * 描 述：下载状态监听
 * <p>
 * <p>
 * 修订历史：
 * <p>
 * ========================================
 */
public interface DownloadTaskListener {
	
	/** 更新进度 */
    void updateProcess(DownloadTask mgr);

    /** 完成下载 */
    void finishDownload(DownloadTask mgr);

    /** 准备下载 */
    void preDownload(DownloadTask mgr);
    
    /** 下载错误 */
    void errorDownload(DownloadTask mgr, int error);

    /** 暂停 */
    void cancelDownload(DownloadTask mgr);

}
