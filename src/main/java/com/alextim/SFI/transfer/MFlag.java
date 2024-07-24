package com.alextim.SFI.transfer;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum MFlag {

    BC_TO_RT((byte) 1, "Передача слов данных от КШ к ОУ"),
    RT_TO_BC((byte) 2, "Передача слов данных от ОУ к КШ");

    public final byte value;
    public final String title;
}
