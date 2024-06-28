package com.alextim.SFI.transfer;

import com.sun.jna.ptr.LongByReference;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Handles {

    public static class Handle extends LongByReference {
    }

    public static class DevHandle extends Handle {
    }
    public static class RamHandle extends Handle {
    }

    public static class MsgHandle extends RamHandle {
    }

    public static class StkHandle extends RamHandle {
    }

    public static class FrmHandle extends StkHandle {
    }
}
