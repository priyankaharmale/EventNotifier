package com.hnweb.eventnotifier;

/**
 * Created by Priyanka on 26/09/2018.
 */
public class NotificationUpdateModel {

    private boolean mState;
    private OnCustomStateListener mListener;

    public boolean ismState() {
        return mState;
    }

    private NotificationUpdateModel() {}

    private static NotificationUpdateModel mInstance;

    public static NotificationUpdateModel getInstance() {
        if(mInstance == null) {
            mInstance = new NotificationUpdateModel();
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

    public void setListener(OnCustomStateListener listener) {
        mListener = listener;
    }

    public boolean getState() {
        return mState;
    }

    private void notifyStateChange() {
        mListener.notificationStateChanged();
    }

    public OnCustomStateListener getmListener() {
        return mListener;
    }

    public void setmListener(OnCustomStateListener mListener) {
        this.mListener = mListener;
    }

    public interface OnCustomStateListener {
        void notificationStateChanged();
    }
}
