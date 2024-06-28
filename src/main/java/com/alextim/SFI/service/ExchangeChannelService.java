package com.alextim.SFI.service;


import com.alextim.SFI.transfer.Error;
import com.alextim.SFI.transfer.Handles.DevHandle;
import com.alextim.SFI.transfer.Handles.FrmHandle;
import com.alextim.SFI.transfer.Handles.MsgHandle;
import com.alextim.SFI.transfer.MMSPTransfer;
import com.alextim.SFI.transfer.Message;
import com.sun.jna.platform.win32.WinDef;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

import static com.alextim.SFI.service.ExchangeChannelService.MKOBus.MKO_BUS_A;
import static com.alextim.SFI.service.ExchangeChannelService.MKODirection.MKO_DIR_READ;
import static com.alextim.SFI.service.ExchangeChannelService.MKODirection.MKO_DIR_WRITE;
import static com.alextim.SFI.transfer.BCCW.CHANNEL_A;
import static com.alextim.SFI.transfer.BCCW.CHANNEL_B;
import static com.alextim.SFI.transfer.BlockStatusWord.BSW_EOM;
import static com.alextim.SFI.transfer.BlockStatusWord.BSW_ERROR_FLAG;
import static com.alextim.SFI.transfer.Error.msp_NOERROR;
import static com.alextim.SFI.transfer.MFlag.MSP_M_BC_TO_RT;
import static com.alextim.SFI.transfer.MFlag.MSP_M_RT_TO_BC;

@Slf4j
@RequiredArgsConstructor
public class ExchangeChannelService {

    private final MMSPTransfer service;
    private List<DevHandle> devs;

    public enum MKOBus {
        MKO_BUS_A,
        MKO_BUS_B
    }

    public enum MKODirection {
        MKO_DIR_READ,
        MKO_DIR_WRITE
    }

    @RequiredArgsConstructor
    public static class MKOMessage {
        public final MKOBus bus;
        public final MKODirection dir;
        public final byte devAddr;
        public final byte subAddr;
        public final short[] words;

        public Message mes;
        public MsgHandle mesHandle;

        public int getLastError() {
            return 0; //todo!!!
        }
    }

    public void create() {
        devs = new ArrayList<>();
        Error err = service.startup();
        if (err != msp_NOERROR)
            throw new RuntimeException("startup " + err);
        log.info("Startup successfully");

        int numberOfDevices = service.getNumberOfDevices();
        log.info("NumberOfDevices: {}", numberOfDevices);

        for (int i = 0; i < numberOfDevices; i++) {
            devs.add(service.open(i));
            log.info("Device with number {} is opened", i);
        }
    }

    public void free() {
        for (DevHandle dev : devs) {
            service.close(dev);
            log.info("Device with handle {} is closed", dev);
        }

        service.cleanup();
        log.info("Cleanup successfully");

        devs.clear();
    }

    public int devCount() {
        return devs.size();
    }

    public DevHandle getDevHandleByIndex(int i) {
        return devs.get(i);
    }

    public FrmHandle transferInit(DevHandle devHandle) {
        Error err = service.configure(devHandle);
        if (err != msp_NOERROR)
            throw new RuntimeException("configure " + err);
        log.info("Configure successfully");

        FrmHandle frame = service.createFrame(devHandle);
        if (frame == null)
            throw new RuntimeException("Frame is not created");
        log.info("Creating of frame successfully");

        return frame;
    }

    public void transferLoadMes(DevHandle devHandle,
                                FrmHandle frameHandle,
                                Message.ByReference buffer,
                                MKOMessage message) {

        log.debug("formatMessage");
        long bccw = (message.bus == MKO_BUS_A ? CHANNEL_A.value : CHANNEL_B.value);
        log.debug("bccw:{}", bccw);

        byte mesType = (byte) (message.dir == MKO_DIR_WRITE ? MSP_M_BC_TO_RT.value : MSP_M_RT_TO_BC.value);
        log.debug("mesType:{}", mesType);


        Message.ByReference format = service.formatMessage(
                buffer,
                mesType,
                message.devAddr,
                message.subAddr,
                message.words,
                bccw);

        MsgHandle mesHandle = service.createMessage(devHandle, format);
        message.mesHandle = mesHandle;

        Error err = service.addMessage(frameHandle, mesHandle);
        if (err != msp_NOERROR)
            throw new RuntimeException("addMessage " + err);
    }


    public void transferStart(DevHandle devHandle, FrmHandle frameHandle) {
        Error err = service.loadFrame(frameHandle, 0);
        if (err != msp_NOERROR)
            throw new RuntimeException("loadFrame " + err);

        err = service.start(devHandle);
        if (err != msp_NOERROR)
            throw new RuntimeException("start " + err);
    }

    @SneakyThrows
    public void transferProccessMes(FrmHandle frameHandle, int frameMesIndex, Message.ByReference buffer, MKOMessage message) {
        Error err = service.retrieveMessage(frameHandle, frameMesIndex, buffer);
        if (err != msp_NOERROR) {
            throw new RuntimeException("retrieveMessage " + err);
        }
        log.info("Retrieve successfully");

        /*
        Дождаться, пока КШ не закончит ((M.bsw & msp_BSW_EOM) != 0)) обработку нового (M.timetag != pTT) сообщения
        */
        System.out.println("buffer = " + buffer);


        while (!( (buffer.timetag != 0) /*&& ((buffer.bsw & BSW_EOM.code) != 0)*/     )) {
            System.out.println("buffer = " + buffer);

            Thread.sleep(10_000);

            err = service.retrieveMessage(frameHandle, frameMesIndex, buffer);
            if (err != msp_NOERROR)
                throw new RuntimeException("retrieveMessage " + err);
        }

        log.info("Mes.StatusWord1 = {}", buffer.statusWord1);
        log.info("Mes.bsw = {}", buffer.bsw);

        if (((buffer.bsw & BSW_ERROR_FLAG.code) == 0) &&
                (message.dir == MKO_DIR_READ) &&
                (message.words.length == buffer.dataWordCount)) {
            service.readMessageData(message.mesHandle, message.words);
        }
    }

    public void transfer(int devIndex, Message.ByReference buffer, MKOMessage message) {
        DevHandle devHandle = getDevHandleByIndex(devIndex);

        FrmHandle frameHandle = transferInit(devHandle);

        transferLoadMes(devHandle, frameHandle, buffer, message);

        transferStart(devHandle, frameHandle);

        try {
            transferProccessMes(frameHandle, 0, buffer, message);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            transferFreeMes(message.mesHandle);
            transferDeInit(devHandle, frameHandle);
        }
    }

    public void transferDeInit(DevHandle devHandle, FrmHandle frmHandle) {
        service.reset(devHandle);

        Error err = service.destroyFrame(frmHandle);
        if (err != msp_NOERROR)
            throw new RuntimeException("destroyFrame " + err);
    }

    public void transferFreeMes(MsgHandle mesHandle) {
        Error err = service.destroyMessage(mesHandle);
        if (err != msp_NOERROR)
            throw new RuntimeException("destroyMessage " + err);
    }
}
