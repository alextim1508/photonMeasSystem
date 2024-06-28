package com.alextim.SFI.transfer;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum BlockStatusWord {

    BSW_EOM(1 << 15),
    BSW_SOM(1 << 14),
    BSW_CHANNEL_B(1 << 13),
    BSW_ERROR_FLAG(1 << 12),
    BSW_FORMAT_ERROR(1 << 10),
    BSW_NO_RESPONSE_TIMEOUT(1 << 9),

    BSW_BC_EOM(1 << 15),
    BSW_BC_SOM(1 << 14),
    BSW_BC_CHANNEL_B(1 << 13),
    BSW_BC_ERROR_FLAG(1 << 12),
    BSW_BC_STATUS_SET(1 << 11),
    BSW_BC_FORMAT_ERROR(1 << 10),
    BSW_BC_NO_RESPONSE_TIMEOUT(1 << 9),
    BSW_BC_LOOP_TEST_FAIL(1 << 8),
    BSW_BC_MASKED_STATUS_SET(1 << 7),
    BSW_BC_DOUBLE_RETRY(1 << 6),
    BSW_BC_SINGLE_RETRY(1 << 5),
    BSW_BC_GOOD_DATA_BLOCK_TRANSFER(1 << 4),
    BSW_BC_STATUS_RESPONSE_ERROR(1 << 3),
    BSW_BC_WORD_COUNT_ERROR(1 << 2),
    BSW_BC_INCORRECT_SYNC_TYPE(1 << 1),
    BSW_BC_INVALID_WORD(1 << 0);

    public final int code;
}
