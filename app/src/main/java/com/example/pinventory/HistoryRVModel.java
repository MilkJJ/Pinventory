package com.example.pinventory;

import android.os.Parcel;
import android.os.Parcelable;

public class HistoryRVModel implements Parcelable {
    private String actionHistory;
    private String timestamp;

    public HistoryRVModel() {
        // Empty Constructor
    }

    public HistoryRVModel(String actionHistory, String timestamp) {
        this.actionHistory = actionHistory;
        this.timestamp = timestamp;
    }

    protected HistoryRVModel(Parcel in) {
        actionHistory = in.readString();
        timestamp = in.readString();
    }

    public static final Creator<HistoryRVModel> CREATOR = new Creator<HistoryRVModel>() {
        @Override
        public HistoryRVModel createFromParcel(Parcel in) {
            return new HistoryRVModel(in);
        }

        @Override
        public HistoryRVModel[] newArray(int size) {
            return new HistoryRVModel[size];
        }
    };

    public String getActionHistory() {
        return actionHistory;
    }

    public void setActionHistory(String actionHistory) {
        this.actionHistory = actionHistory;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(actionHistory);
        dest.writeString(timestamp);
    }
}
