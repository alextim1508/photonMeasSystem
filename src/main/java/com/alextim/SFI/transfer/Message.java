package com.alextim.SFI.transfer;

import com.sun.jna.Structure;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Structure.FieldOrder({ "type", "dataWordCount", "bccw", "cmdWord1", "cmdWord2", "data", "statusWord1", "statusWord2", "loopback", "bsw", "timetag" })
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
    public short bsw; // Block Status Word // => Когда считано msp_RetrieveMessage()
    volatile public short timetag; // => Когда считано msp_RetrieveMessage()
}