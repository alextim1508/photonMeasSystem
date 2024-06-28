package com.alextim.SFI.transfer;

import lombok.AllArgsConstructor;

import static com.alextim.SFI.transfer.Register.*;

@AllArgsConstructor
public enum Flag {
    F_256WORD_BOUNDARY_DISABLE((RR_CONFIG2.value << 16) | (1 << 10)),
    F_ENHANCED_MODE((RR_CONFIG3.value << 16) | (1 << 15)),
    F_MESSAGE_GAP_TIMER_ENABLED((RR_CONFIG1.value << 16) | (1 << 5)),
    F_INTERNAL_TRIGGER_ENABLED((RR_CONFIG1.value << 16) | (1 << 6)),
    F_EXPANDED_BC_CONTROL_WORD((RR_CONFIG4.value << 16) | (1 << 12));

    public final int value;
}
