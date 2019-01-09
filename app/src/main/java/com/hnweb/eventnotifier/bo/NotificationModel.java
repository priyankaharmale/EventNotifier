package com.hnweb.eventnotifier.bo;

import java.io.Serializable;

public class NotificationModel implements Serializable {

    private String notifi_id;
    private String notifi_user_id;
    private String notifi_type;
    private String notifi_msg;
    private String notifimy_task_id;
    private String notifi_from_id;
    private String notifi_created_date;
    private String notifi_status;

    private boolean mState;
    public NotificationModel.OnCustomStateListener mListener;

    public boolean ismState() {
        return mState;
    }

    public NotificationModel() {}

    public static NotificationModel mInstance;

    public static NotificationModel getInstance() {
        if(mInstance == null) {
            mInstance = new NotificationModel();
        }
        return mInstance;
    }

    public void setmState(boolean mState) {
        this.mState = mState;
    }
    public void changeState(boolean state) {
        if(mListener != null) {
            mState = state;
            notifyStateChange();
        }
    }

    public void setListener(NotificationModel.OnCustomStateListener listener) {
        mListener = listener;
    }

    public boolean getState() {
        return mState;
    }

    private void notifyStateChange() {
        mListener.stateChangedNotifi();
    }

    public NotificationModel.OnCustomStateListener getmListener() {
        return mListener;
    }

    public void setmListener(NotificationModel.OnCustomStateListener mListener) {
        this.mListener = mListener;
    }

    public interface OnCustomStateListener {
        void stateChangedNotifi();
    }

    public String getNotifi_id() {
        return notifi_id;
    }

    public void setNotifi_id(String notifi_id) {
        this.notifi_id = notifi_id;
    }

    public String getNotifi_user_id() {
        return notifi_user_id;
    }

    public void setNotifi_user_id(String notifi_user_id) {
        this.notifi_user_id = notifi_user_id;
    }

    public String getNotifi_type() {
        return notifi_type;
    }

    public void setNotifi_type(String notifi_type) {
        this.notifi_type = notifi_type;
    }

    public String getNotifi_msg() {
        return notifi_msg;
    }

    public void setNotifi_msg(String notifi_msg) {
        this.notifi_msg = notifi_msg;
    }

    public String getNotifimy_task_id() {
        return notifimy_task_id;
    }

    public void setNotifimy_task_id(String notifimy_task_id) {
        this.notifimy_task_id = notifimy_task_id;
    }

    public String getNotifi_from_id() {
        return notifi_from_id;
    }

    public void setNotifi_from_id(String notifi_from_id) {
        this.notifi_from_id = notifi_from_id;
    }

    public String getNotifi_created_date() {
        return notifi_created_date;
    }

    public void setNotifi_created_date(String notifi_created_date) {
        this.notifi_created_date = notifi_created_date;
    }

    public String getNotifi_status() {
        return notifi_status;
    }

    public void setNotifi_status(String notifi_status) {
        this.notifi_status = notifi_status;
    }
}
