package com.alextim.SFI.transfer;

import com.sun.jna.Structure;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.Arrays;


@AllArgsConstructor
@NoArgsConstructor
@Structure.FieldOrder({"type", "dataWordCount", "bccw", "cmdWord1", "cmdWord2", "data", "statusWord1", "statusWord2", "loopback", "bsw", "timetag"})
public class Message extends Structure {
    public static class ByReference extends Message implements Structure.ByReference {
    }

    public short type;
    public short dataWordCount;
    public short bccw;
    /*-----*/
    public short cmdWord1;
    public short cmdWord2;
    public short[] data = new short[32];
    /*-----*/
    public short statusWord1; // => Когда считано msp_RetrieveMessage(), msp_ReadBCMessage
    public short statusWord2;
    public short loopback;
    /*-----*/
    public volatile short bsw; // Block Status Word // => Когда считано msp_RetrieveMessage()
    public short timetag; // => Когда считано msp_RetrieveMessage()

    @Override
    public String toString() {
        return "Message{" +
                "\n\ttype=" + type +
                "\n\tdataWordCount=" + dataWordCount +
                "\n\tbccw=" + bccw +
                "\n\tcmdWord1=" + cmdWord1 +
                "\n\tcmdWord2=" + cmdWord2 +
                "\n\tdata=" + Arrays.toString(data) +
                "\n\tstatusWord1=" + statusWord1 +
                "\n\tstatusWord2=" + statusWord2 +
                "\n\tloopback=" + loopback +
                "\n\tbsw=" + bsw +
                "\n\ttimetag=" + timetag +
                '}';
    }
}