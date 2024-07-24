package com.alextim.SFI.transfer;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum BCCW {
    CHANNEL_A((byte) 0x0080),
    CHANNEL_B((byte) 0x0000);

    public final byte value;
}
