package com.alextim.SFI.transfer;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum Command {
    RESET(0x0001),
    START(0x0002),
    INTERRUPT_RESET(0x0004),
    TIME_TAG_RESET(0x0008),
    TIME_TAG_TEST_CLOCK(0x0010),
    STOP_ON_FRAME(0x0020),
    STOP_ON_MESSAGE(0x0040);

    public final int code;
}
