package com.alextim.SFI.service;

import com.alextim.SFI.service.ExchangeChannelService.MKOMessage;
import com.alextim.SFI.transfer.Message;
import lombok.extern.slf4j.Slf4j;

import static com.alextim.SFI.service.ExchangeChannelService.MKOBus.MKO_BUS_A;
import static com.alextim.SFI.service.ExchangeChannelService.MKODirection.MKO_DIR_READ;
import static com.alextim.SFI.service.ExchangeChannelService.MKODirection.MKO_DIR_WRITE;

@Slf4j
public class PhotonMeasSystemService {

    private ExchangeChannelService exchangeChannelService;

    public static byte DEVICE = 0; // устройство МКО
    public static byte da_MAIN_CHANNEL = 9; // адрес оконечного устройства (ОУ)

    public static  byte pa_MEAS_DATA = 1;  // подадрес
    public static byte pa_TECHN_CMD = 4;

    public static byte tc_OPEN_TRANSMITTER = 0x0002; // технологические команды
    public static byte tc_CLOSE_TRANSMITTER = 0x0001;


    public PhotonMeasSystemService(ExchangeChannelService exchangeChannelService) {
        this.exchangeChannelService = exchangeChannelService;
        exchangeChannelService.create();
    }

    public MeasResult readMeasResults() {
        MKOMessage mkoMessage = new MKOMessage(
                MKO_BUS_A,
                MKO_DIR_READ,
                da_MAIN_CHANNEL,
                pa_MEAS_DATA,
                new short[13]);

        exchangeChannelService.transfer(DEVICE, new Message.ByReference(), mkoMessage);

        if (mkoMessage.getLastError() != 0)
            throw new RuntimeException("1111");

        MeasResult measResult = new MeasResult();

        measResult.frequency1 = mkoMessage.words[0] + 0x10000 * mkoMessage.words[1];
        measResult.frequency2 = mkoMessage.words[2] + 0x10000 * mkoMessage.words[3];
        measResult.frequency3 = mkoMessage.words[4] + 0x10000 * mkoMessage.words[5];
        measResult.frequency4 = mkoMessage.words[6] + 0x10000 * mkoMessage.words[7];
        measResult.height = mkoMessage.words[8] + 0x10000 * mkoMessage.words[9];
        measResult.status = MsgStatus.getByCode(mkoMessage.words[10] + 0x10000 * mkoMessage.words[11]);
        measResult.packetID = mkoMessage.words[12];

        return measResult;
    }

    public void openTransmitter() {
        MKOMessage mkoMessage = new MKOMessage(
                MKO_BUS_A,
                MKO_DIR_WRITE,
                da_MAIN_CHANNEL,
                pa_TECHN_CMD,
                new short[2]);

        mkoMessage.words[0] = tc_OPEN_TRANSMITTER;
        mkoMessage.words[1] = 0x0000;

        exchangeChannelService.transfer(DEVICE, new Message.ByReference(), mkoMessage);
        if (mkoMessage.getLastError() != 0)
            throw new RuntimeException("1111");
    }

    public void closeTransmitter() {
        MKOMessage mkoMessage = new MKOMessage(
                MKO_BUS_A,
                MKO_DIR_WRITE,
                da_MAIN_CHANNEL,
                pa_TECHN_CMD,
                new short[2]);

        mkoMessage.words[0] = tc_CLOSE_TRANSMITTER;
        mkoMessage.words[1] = 0x0000;

        exchangeChannelService.transfer(DEVICE, new Message.ByReference(), mkoMessage);
        if (mkoMessage.getLastError() != 0)
            throw new RuntimeException("1111");
    }
}
