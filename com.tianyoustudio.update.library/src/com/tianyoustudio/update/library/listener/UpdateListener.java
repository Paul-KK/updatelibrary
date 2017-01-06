package com.tianyoustudio.update.library.listener;

import com.tianyoustudio.update.library.bean.UpdateApkInfo;

/**
 * The update check callback
 */
public abstract class UpdateListener {

    /**
     * There are a new version of APK on network
     */
    public void hasUpdate(UpdateApkInfo update) {

    }

    /**
     * There are no new version for update
     */
    public abstract void noUpdate(String msg);

    /**
     * http check error,
     *
     * @param code     http code
     * @param errorMsg http error msg
     */
    public abstract void onCheckError(int code, String errorMsg);

    /**
     * to be invoked by user press cancel button.
     */
    public void onUserCancel() {

    }
}
