package com.tianyoustudio.update.library.network;


public interface IUpdateExecutor {

    /**
     * check if is new version exist;
     */
    void check(UpdateWorker worker);

    /**
     * request download new version apk
     */
    void onlineCheck(OnlineCheckWorker worker);
}
