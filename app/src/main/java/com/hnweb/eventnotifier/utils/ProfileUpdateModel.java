package com.hnweb.eventnotifier.utils;
/* * Created by Priyanka H on 24/09/2018.
 */
public class ProfileUpdateModel {

    private boolean mState;
    private OnCustomStateListener mListener;

    public boolean ismState() {
        return mState;
    }

    private ProfileUpdateModel() {}

    private static ProfileUpdateModel mInstance;

    public static ProfileUpdateModel getInstance() {
        if(mInstance == null) {
            mInstance = new ProfileUpdateModel();
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
        mListener.stateChanged();
    }

    public OnCustomStateListener getmListener() {
        return mListener;
    }

    public void setmListener(OnCustomStateListener mListener) {
        this.mListener = mListener;
    }

    public interface OnCustomStateListener {
        void stateChanged();
    }
}
