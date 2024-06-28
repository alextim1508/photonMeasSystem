package com.alextim.SFI.transfer;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum Mode {
    MODE_NOT_DEFINED(0x0000),
    MODE_BC(0x0001),
    MODE_RT(0x0002),
    MODE_MM(0x0004),
    MODE_WM(0x0008),
    MODE_IDLE(0x0010),
    MODE_ENHANCED(0x8000);

    public int value;
}
