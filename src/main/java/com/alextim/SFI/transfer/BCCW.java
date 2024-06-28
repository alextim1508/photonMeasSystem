package com.alextim.SFI.transfer;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum BCCW {
    CHANNEL_A(0x0080),
    CHANNEL_B(0x0000);

    public final int value;
}
