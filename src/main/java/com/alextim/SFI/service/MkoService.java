package com.alextim.SFI.service;


import com.alextim.SFI.frontend.view.param.ParamController;
import com.alextim.SFI.transfer.Error;
import com.alextim.SFI.transfer.Handles.DevHandle;
import com.alextim.SFI.transfer.Handles.FrmHandle;
import com.alextim.SFI.transfer.Handles.MsgHandle;
import com.alextim.SFI.transfer.MMSPTransfer;
import com.alextim.SFI.transfer.Message;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import static com.alextim.SFI.frontend.view.param.ParamController.*;
import static com.alextim.SFI.transfer.Area.AUTO_REPEAT;
import static com.alextim.SFI.transfer.BCCW.CHANNEL_A;
import static com.alextim.SFI.transfer.BCCW.CHANNEL_B;
import static com.alextim.SFI.transfer.Error.msp_NOERROR;
import static com.alextim.SFI.transfer.MFlag.BC_TO_RT;
import static com.alextim.SFI.transfer.MFlag.RT_TO_BC;

@Slf4j
@RequiredArgsConstructor
public class MkoService {

    private final MMSPTransfer service;

    @AllArgsConstructor
    public enum MKOBus {
        MKO_BUS_A(CHANNEL_A.value),
        MKO_BUS_B(CHANNEL_B.value);

        public final byte code;
    }

    @AllArgsConstructor
    public enum MKODirection {
        MKO_DIR_READ(RT_TO_BC.value),
        MKO_DIR_WRITE(BC_TO_RT.value);

        public final byte code;
    }

    @RequiredArgsConstructor
    public static class MKOMessage {
        public final MKOBus bus;
        public final MKODirection dir;
        public final byte devAddr;
        public final byte subAddr;
        public final short[] data;
    }

    private List<Object> trash = new ArrayList<>(); //bug JNA or my crooked hands

    public void transfer(MKOMessage message, Setting setting, Consumer<short[]> consumer, AtomicBoolean isStop, long amount) {
        log.info("Start transfer");

        Error err = service.startup();
        if (err != msp_NOERROR) {
            log.error("startup: {}", err);
            throw new RuntimeException("startup: " + err);
        }

        try {
            DevHandle devHandle = service.open(0);
            if (devHandle == null) {
                log.error("open er: devHandle == null");
                throw new RuntimeException("open err: devHandle == null");
            }
            trash.add(devHandle);

            err = service.configure(devHandle);
            if (err != msp_NOERROR) {
                log.error("configure: {}", err);
                throw new RuntimeException("configure " + err);
            }

            FrmHandle frameHandle = service.createFrame(devHandle, (short) setting.frameTime, (short) 1);
            if (frameHandle == null) {
                log.error("createFrame er: frameHandle == null");
                throw new RuntimeException("createFrame er: frameHandle == null");
            }
            trash.add(frameHandle);

            Message.ByReference buffer = new Message.ByReference();
            trash.add(buffer);

            Message.ByReference formatMessage = service.formatMessage(
                    buffer,
                    message.dir.code,
                    message.devAddr,
                    message.subAddr,
                    message.data,
                    message.bus.code);
            if (formatMessage == null) {
                log.error("formatMessage er: formatMessage==null");
                throw new RuntimeException("formatMessage er: formatMessage==null");
            }
            trash.add(formatMessage);

            MsgHandle msgHandle = service.createMessage(devHandle, formatMessage);
            if (msgHandle == null) {
                log.error("createMessage er: msgHandle == null");
                throw new RuntimeException("createMessage er: msgHandle == null");
            }
            trash.add(message);

            err = service.addMessage(frameHandle, msgHandle, (short) 0);
            if (err != msp_NOERROR) {
                log.error("addMessage: {}", err);
                throw new RuntimeException("addMessage " + err);
            }

            err = service.loadFrame(frameHandle, AUTO_REPEAT);
            if (err != msp_NOERROR) {
                log.error("loadFrame: {}", err);
                throw new RuntimeException("loadFrame " + err);
            }

            err = service.start(devHandle);
            if (err != msp_NOERROR) {
                log.error("start: {}", err);
                throw new RuntimeException("start " + err);
            }


            short[] data = new short[32];
            for (long i = 0; i <= amount && !isStop.get(); i++) {
                err = service.retrieveMessage(frameHandle, 0, formatMessage);
                if (err != msp_NOERROR) {
                    log.error("retrieveMessage: {}", err);
                    throw new RuntimeException("retrieveMessage " + err);
                }

                if (formatMessage.type == 2 && formatMessage.bsw == 16400) {
                    service.readMessageData(msgHandle, data);
                    consumer.accept(data);
                }

                try {
                    Thread.sleep(setting.delayLoop);
                } catch (Exception e) {
                    log.error("", e);
                }
            }

            err = service.reset(devHandle);
            if (err != msp_NOERROR) {
                log.error("reset: {}", err);
                throw new RuntimeException("reset " + err);
            }

            err = service.close(devHandle);
            if (err != msp_NOERROR) {
                log.error("close: {}", err);
                throw new RuntimeException("close " + err);
            }

        } finally {
            err = service.cleanup();
            if (err != msp_NOERROR) {
                log.error("cleanup: {}", err);
            }
        }

        log.info("Transfer successfully");
    }
}
