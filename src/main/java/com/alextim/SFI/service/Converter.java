package com.alextim.SFI.service;

public class Converter {

    public static MeasResult convert(short[] data) {

        MeasResult measResult = new MeasResult();

        measResult.frequency1 = Short.toUnsignedInt(data[0]) + 0x10000L * Short.toUnsignedInt(data[1]);
        measResult.frequency2 = Short.toUnsignedInt(data[2]) + 0x10000L * Short.toUnsignedInt(data[3]);
        measResult.frequency3 = Short.toUnsignedInt(data[4]) + 0x10000L * Short.toUnsignedInt(data[5]);
        measResult.frequency4 = Short.toUnsignedInt(data[6]) + 0x10000L * Short.toUnsignedInt(data[7]);

        measResult.height = Short.toUnsignedInt(data[8]) + 0x10000L * Short.toUnsignedInt(data[9]);

        short statusL = data[10];
        short statusH = data[11];
        measResult.statusValue = Short.toUnsignedInt(statusL) + 0x10000L * Short.toUnsignedInt(statusH);
        measResult.status = MsgStatus.getByCode(statusH, statusL);

        measResult.packetID = Integer.toUnsignedLong(data[12]);

        return measResult;
    }

    public static void fullMeasResult(short[] data, MeasResult measResult) {
        measResult.frequency1 = Short.toUnsignedInt(data[0]) + 0x10000L * Short.toUnsignedInt(data[1]);
        measResult.frequency2 = Short.toUnsignedInt(data[2]) + 0x10000L * Short.toUnsignedInt(data[3]);
        measResult.frequency3 = Short.toUnsignedInt(data[4]) + 0x10000L * Short.toUnsignedInt(data[5]);
        measResult.frequency4 = Short.toUnsignedInt(data[6]) + 0x10000L * Short.toUnsignedInt(data[7]);

        measResult.height = Short.toUnsignedInt(data[8]) + 0x10000L * Short.toUnsignedInt(data[9]);

        short statusL = data[10];
        short statusH = data[11];
        measResult.statusValue = Short.toUnsignedInt(statusL) + 0x10000L * Short.toUnsignedInt(statusH);
        measResult.status = MsgStatus.getByCode(statusH, statusL);

        measResult.packetID = Integer.toUnsignedLong(data[12]);
    }
}
