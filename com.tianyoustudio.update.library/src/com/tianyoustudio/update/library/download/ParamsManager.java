package com.tianyoustudio.update.library.download;



public class ParamsManager {
	
	/** 一级打印tag */
	public final static String tag = "_down";
	//
	 /** 刷新时间间隔 ,建议在大于200，否则刷新太快会导致Nofication卡顿而不能点击*/
    public final static long UPDATE_TIME=500;
    //
    /** OK */
    public final static int ERROR_NONE = 0;
    /** SD卡容量不足 */
    public final static int ERROR_SD_NO_MEMORY = 1;
    /** 网络异常 */
    public final static int ERROR_BLOCK_INTERNET = 2;
    /** 文件完整性异常 */
    public final static int ERROR_SIZE = 3;
    /** 未知错误 */
    public final static int ERROR_UNKONW = 4;
   //
    /** 准备状态 */
    public final static int State_PRE = 10;
    /** 一般状态 */
    public final static int State_NORMAL = 11;
    /** 下载中状态 */
    public final static int State_DOWNLOAD = 12;
    /** 暂停状态 */
    public final static int State_PAUSE = 13;
    /** 完成状态 */
    public final static int State_FINISH = 14;
    

}
