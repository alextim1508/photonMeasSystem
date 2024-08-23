package com.alextim.SFI.transfer;

import com.alextim.SFI.transfer.Handles.*;
import lombok.RequiredArgsConstructor;

import static com.alextim.SFI.transfer.BCCW.CHANNEL_A;
import static com.alextim.SFI.transfer.Command.*;
import static com.alextim.SFI.transfer.Error.msp_NOERROR;
import static com.alextim.SFI.transfer.Flag.*;
import static com.alextim.SFI.transfer.Handles.StkHandle;
import static com.alextim.SFI.transfer.MFlag.BC_TO_RT;
import static com.alextim.SFI.transfer.Mode.MODE_BC;
import static com.alextim.SFI.transfer.Mode.MODE_ENHANCED;

/*
DWORD is Double Word, each word is 2 bytes in length,
DWORD double word is 4 bytes, each byte is 8 bits, a total of 32 bits.
*/
@RequiredArgsConstructor
public class MMSPTransfer {

    private final MMSPLibrary library;

    public Error startup() {
        int err = library.msp_Startup();
        return Error.getErrorByCode(err);
    }

    public Error cleanup() {
        int err = library.msp_Cleanup();
        return Error.getErrorByCode(err);
    }

    public DevHandle open(int devIndex) {
        return library.msp_Open(devIndex);
    }

    public int getNumberOfDevices() {
        return library.msp_GetNumberOfDevices();
    }


    public Error close(DevHandle devhandle) {
        int err = library.msp_Close(devhandle);
        return Error.getErrorByCode(err);
    }

    private final int[] bcflags = new int[]{
            F_ENHANCED_MODE.value,
            F_256WORD_BOUNDARY_DISABLE.value,
            F_MESSAGE_GAP_TIMER_ENABLED.value,
            F_INTERNAL_TRIGGER_ENABLED.value,
            F_EXPANDED_BC_CONTROL_WORD.value,
            0
    };

    public Error configure(DevHandle devHandle) {
        int err = library.msp_Configure(
                devHandle,
                MODE_BC.value + MODE_ENHANCED.value,
                bcflags,
                null);
        return Error.getErrorByCode(err);
    }

    public FrmHandle createFrame(DevHandle devHandle, short frameTime, short msgCount) {
        return library.msp_CreateFrame(devHandle, frameTime, msgCount);
    }

    public Message.ByReference formatMessage(Message.ByReference format,
                                             byte mesType,
                                             byte devAddr,
                                             byte subAddr,
                                             short[] data,
                                             int bccw) {
        return library.msp_FormatMessage(
                format,
                mesType,
                devAddr,
                subAddr,
                (byte) 0,
                (short) 0,
                (byte) data.length,
                data,
                bccw
        );
    }

    public MsgHandle createMessage(DevHandle devHandle, Message.ByReference buffer) {
        return library.msp_CreateMessage(devHandle, buffer);
    }

    public Error addMessage(FrmHandle frame, MsgHandle message, short msggap) {
        int err = library.msp_AddMessage(frame, message, msggap);
        return Error.getErrorByCode(err);
    }

    public Error loadFrame(FrmHandle frame, Area area) {
        int err = library.msp_LoadFrame(frame, area.code);
        return Error.getErrorByCode(err);
    }

    public Error start(DevHandle devHandle) {
        int err = library.msp_Command(devHandle, START.code);
        return Error.getErrorByCode(err);
    }

    public Error stopOnMsg(DevHandle devHandle) {
        int err = library.msp_Command(devHandle, STOP_ON_MESSAGE.code);
        return Error.getErrorByCode(err);
    }

    public Error stopOnFrm(DevHandle devHandle) {
        int err = library.msp_Command(devHandle, STOP_ON_FRAME.code);
        return Error.getErrorByCode(err);
    }
    public Error reset(DevHandle devHandle) {
        int err = library.msp_Command(devHandle, RESET.code);
        return Error.getErrorByCode(err);
    }

    public Error retrieveMessage(StkHandle stkHandle, int entry, Message.ByReference buffer) {
        int err = library.msp_RetrieveMessage(stkHandle, entry, buffer);
        return Error.getErrorByCode(err);
    }

    public int readMessageData(MsgHandle message, short[] words) {
        return library.msp_ReadMessageData(message, words, words.length);
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

