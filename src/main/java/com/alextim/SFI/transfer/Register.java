package com.alextim.SFI.transfer;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum Register {
    RR_INTERRUPT_MASK((byte) 0x00),
    RR_CONFIG1((byte)0x01),
    RR_CONFIG2((byte)0x02),
    RR_COMMAND((byte)0x03),
    RR_COMMAND_STACK_POINTER((byte)0x03),
    RR_CONTROL_WORD((byte)0x04),
    RR_TIME_TAG((byte)0x05),
    RR_INTERRUPT_STATUS((byte)0x06),
    RR_CONFIG3((byte)0x07),
    RR_CONFIG4((byte)0x08);

    public final byte value;
}
