package com.alextim.SFI.transfer;

import com.alextim.SFI.transfer.Handles.DevHandle;
import com.alextim.SFI.transfer.Handles.FrmHandle;
import com.alextim.SFI.transfer.Handles.MsgHandle;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.ptr.LongByReference;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static com.alextim.SFI.transfer.Command.RESET;
import static com.alextim.SFI.transfer.Command.START;
import static com.alextim.SFI.transfer.Flag.F_ENHANCED_MODE;
import static com.alextim.SFI.transfer.Handles.*;
import static com.alextim.SFI.transfer.Mode.MODE_BC;
import static com.alextim.SFI.transfer.Mode.MODE_ENHANCED;
import static com.sun.jna.platform.win32.WinDef.*;

/*
Namely: BYTE = unsigned char, WORD = unsigned short, DWORD = unsigned long
DWORD is Double Word, each word is 2 bytes in length, DWORD double word is 4 bytes, each byte is 8 bits, a total of 32 bits.
*/
@Slf4j
@RequiredArgsConstructor
public class MMSPTransfer {

    private final MMSPLibrary library;

    public Error startup() {
        log.debug("msp_Startup");
        int err = library.msp_Startup();
        log.debug("msp_Startup. err: {}", err);
        return Error.getErrorByCode(err);
    }

    public Error cleanup() {
        log.debug("msp_Cleanup");
        int err = library.msp_Cleanup();
        log.debug("msp_Cleanup. err: {}", err);
        return Error.getErrorByCode(err);
    }

    public int getNumberOfDevices() {
        log.debug("msp_GetNumberOfDevices");
        int number = library.msp_GetNumberOfDevices();
        log.debug("msp_GetNumberOfDevices. number: {}", number);
        return number;
    }

    public DevHandle open(int devIndex) {
        log.debug("msp_Open");
        DevHandle devHandle = library.msp_Open(devIndex);
        log.debug("msp_Open. devHandle: {}", devHandle);
        return devHandle;
    }

    public Error close(DevHandle devhandle) {
        log.debug("msp_Close");
        int err = library.msp_Close(devhandle);
        log.debug("msp_Close. err: {}", err);
        return Error.getErrorByCode(err);
    }

    public Error configure(DevHandle devHandle) {
        log.debug("msp_Configure");
        int err = library.msp_Configure(
                devHandle,
               MODE_BC.value + MODE_ENHANCED.value,
                new LongByReference(F_ENHANCED_MODE.value),
                null);
        log.debug("msp_Configure. err: {}", err);
        return Error.getErrorByCode(err);
    }

    public FrmHandle createFrame(DevHandle devHandle) {
        log.debug("msp_CreateFrame");
        FrmHandle frmHandle = library.msp_CreateFrame(
                devHandle,
                100,
                10);
        log.debug("msp_CreateFrame. frmHandle: {}",frmHandle);
        return frmHandle;
    }

    public Message.ByReference formatMessage(Message.ByReference buffer,
                                             byte mesType,
                                             byte devAddr,
                                             byte subAddr,
                                             short[] data,
                                             long bccw) {


        log.debug("msp_FormatMessage");
        Message.ByReference res = library.msp_FormatMessage(
                buffer,
                mesType,
                devAddr,
                subAddr,
                (byte)0,
                0,
                (byte)data.length,
                data,
                bccw
        );
        log.debug("msp_FormatMessage. res: {}", res);

        return res;
    }

    public MsgHandle createMessage(DevHandle devHandle, Message.ByReference buffer) {
        log.debug("msp_CreateMessage");
        MsgHandle msgHandle = library.msp_CreateMessage(devHandle, buffer);
        log.debug("msp_CreateMessage. msgHandle: {}", msgHandle);
        return msgHandle;
    }

    public Error addMessage(FrmHandle frame, MsgHandle message) {
        log.debug("msp_AddMessage");
        int err = library.msp_AddMessage(frame, message, (short) 10_000);
        log.debug("msp_AddMessage. err: {}", err);
        return Error.getErrorByCode(err);
    }

    public Error loadFrame(FrmHandle frame, int flags) {
        log.debug("msp_LoadFrame");
        int err = library.msp_LoadFrame(frame, flags);
        log.debug("msp_LoadFrame. err: {}", err);
        return Error.getErrorByCode(err);
    }

    public Error start(DevHandle devHandle) {
        log.debug("msp_Command");
        int err = library.msp_Command(devHandle, START.code);
        log.debug("msp_Command. err: {}", err);
        return Error.getErrorByCode(err);
    }

    public Error reset(DevHandle devHandle) {
        log.debug("msp_Command");
        int err = library.msp_Command(devHandle, RESET.code);
        log.debug("msp_Command. err: {}", err);
        return Error.getErrorByCode(err);
    }

    public Error retrieveMessage(StkHandle stkHandle, int entry, Message.ByReference buffer) {
        log.debug("msp_RetrieveMessage");
        int err = library.msp_RetrieveMessage(stkHandle, entry, buffer);
        log.debug("msp_RetrieveMessage. err: {}", err);
        return Error.getErrorByCode(err);
    }

    public int readMessageData(MsgHandle message, short[] words) {
        log.debug("msp_ReadMessageData");
        int i = library.msp_ReadMessageData(message, words, words.length);
        log.debug("msp_ReadMessageData. i: {}", i);

        return i;
    }

    public Error destroyMessage(MsgHandle msgHandle) {
        int err = library.msp_DestroyHandle(msgHandle);
        return Error.getErrorByCode(err);
    }

    public Error destroyFrame(FrmHandle frmHandle) {
        int err = library.msp_DestroyHandle(frmHandle);
        return Error.getErrorByCode(err);
    }
}
