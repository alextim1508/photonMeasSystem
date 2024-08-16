package com.alextim.SFI.service;

import lombok.ToString;

import java.util.Map;

@ToString
public class MeasResult {

    public MeasResult() {
        timestamp = System.currentTimeMillis();
    }

    public MeasResult(long timestamp) {
        this.timestamp = timestamp;
    }

    public long frequency1;
    public long frequency2;
    public long frequency3;
    public long frequency4;
    public long height;
    public int packetID;
    public long statusValue;
    public Map<MsgStatus, Boolean> status;
    public final long timestamp;
}
