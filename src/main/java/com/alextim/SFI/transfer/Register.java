package com.alextim.SFI.transfer;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum Register {
    RR_INTERRUPT_MASK(0x00),
    RR_CONFIG1(0x01),
    RR_CONFIG2(0x02),
    RR_COMMAND(0x03),
    RR_COMMAND_STACK_POINTER(0x03),
    RR_CONTROL_WORD(0x04),
    RR_TIME_TAG(0x05),
    RR_INTERRUPT_STATUS(0x06),
    RR_CONFIG3(0x07),
    RR_CONFIG4(0x08);

    public final int value;
}
