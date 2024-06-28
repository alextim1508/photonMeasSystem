package com.alextim.SFI.transfer;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum Error {
    msp_NOERROR(0),
    msp_ERROR_INVALID_DEVICE_HANDLE(1),
    msp_ERROR_INVALID_PARAMETER(2),
    msp_ERROR_NOT_INITIALIZED(3),
    msp_ERROR_DEVICE_BUSY(5),
    msp_ERROR_SYSTEM_ERROR(4),
    msp_ERROR_INTERNAL_ACCESS_ERROR(6),
    msp_ERROR_NO_HOST_MEMORY(7),
    msp_ERROR_ADDRESS_OUT_OF_RAM(8),
    msp_ERROR_IRQ_BUSY(9),
    msp_ERROR_IRQ_CANCELLED(10),
    msp_ERROR_INTERNAL_INTEGRITY_ERROR(11),
    msp_ERROR_MISSING_DELAY_FUNC(12),
    msp_ERROR_UNSUPPORTED_FUNCTION(13),
    msp_ERROR_UNKNOWN_DEVICE_TYPE(14),
    msp_ERROR_UNSUPPORTED_DRIVER_TYPE(15),
    msp_ERROR_UNSUPPORTED_BUS_TYPE(16),
    msp_ERROR_DRIVER_NOT_FOUND(17),
    msp_ERROR_NO_FREE_RAM(20),
    msp_ERROR_INVALID_RAM_HANDLE(21),
    msp_ERROR_ILLEGAL_HANDLE_TYPE(22),
    msp_ERROR_HANDLE_OUT_OF_SCOPE(23),
    msp_ERROR_HANDLE_IN_USE(24),
    msp_ERROR_ADDRESS_OUT_OF_OBJECT(25),
    msp_ERROR_INVALID_FLAG(30),
    msp_ERROR_INVALID_VREG(31),
    msp_ERROR_UNSUPPORTED_MODE(32),
    msp_ERROR_MODE_NOT_SELECTED(33),
    msp_ERROR_INCOMPATIBLE_MODE(34),
    msp_ERROR_INCOMPATIBLE_CONFIGURATION(35),
    msp_ERROR_FRAM_NOT_RESERVED(36),
    msp_ERROR_INCOMPATIBLE_STACK(40),
    msp_ERROR_INCOMPATIBLE_FRAME(40),
    msp_ERROR_NOT_LOADED(41),
    msp_ERROR_INDEX_OUT_OF_RANGE(42),
    msp_ERROR_NO_NEXT_MESSAGE(43),
    msp_ERROR_MESSAGE_IN_PROGRESS(44),
    msp_ERROR_INVALID_MESSAGE_FORMAT(50),
    msp_ERROR_MESSAGE_FIELD_NOT_EXISTS(51),
    msp_ERROR_FRAME_EXCEEDS_SIZE_LIMIT(52),
    msp_ERROR_INCOMPATIBLE_BUFFER(60),
    msp_ERROR_BUFFER_NOT_CONNECTED(61),
    msp_ERROR_BUFFER_BUSY_SAFE(62),
    msp_ERROR_BUFFER_BUSY_UNSAFE(63),
    msp_ERROR_STREAM_DISABLED(64),
    msp_ERROR_STREAM_IN_USE(65),
    msp_ERROR_STREAM_CORRUPTED(66),
    msp_ERROR_ILLEGAL_COMMAND_WORD(67),
    msp_ERROR_NOT_MODE_CODE(68),

    ////////// Дополнения для ГСП ////////////////
    msp_ERROR_TOO_MANY_FRAMES(70),
    msp_ERROR_INVALID_MESSAGE_HANDLE(71),
    msp_ERROR_INVALID_FRAME_HANDLE(72),
    msp_ERROR_ANSWER_NOT_MATCH(80),
    msp_ERROR_ANSWER_MISSING_WORDS(81),
    msp_ERROR_ANSWER_EXTRA_WORDS(82),
    msp_ERROR_INVALID_MESSAGE(83),
    msp_ERROR_ANSWER_ERROR(84),
    msp_ERROR_ANSWER_ANOTHER_CHANNEL(85),
    msp_ERROR_ANSWER_RT_BUSY(86),
    msp_ERROR_INTERNAL_ALGORITHM(90),

    /////////// Дополнения для USB ////////////////
    msp_ERROR_EMPTY_FRAME(100),
    msp_ERROR_OVERLOAD_BUFFERS(101),
    msp_ERROR_INVALID_REQUEST(102),
    msp_ERROR_USB_EXCHANGE(200),
    msp_ERROR_USB_NOT_SUPPORT(201),
    msp_ERROR_USB_ACCESS(202),
    msp_ERROR_USB_FTDI_ERROR(203),
    msp_ERROR_USB_RESPONSE_TIMEOUT(204),
    msp_ERROR_USB_RESPONSE_FAILURE(205),
    msp_ERROR_DEVICES_NOT_FOUND(210),
    ///
    msp_ERROR_IMPOSING_IS_IMPOSSIBLE(220),

    ////////// Дополнения для модуля cash /////////////

    msp_ERROR_CASH_DEVICE_TABLE_OVERLOAD(301),
    msp_ERROR_DEVICE_NOT_INTO_CASH(302),
    msp_ERROR_CASH_INCORRECT_CALL_FUNCTION(303),
    msp_ERROR_VXI_ERROR(320);

    public final int code;

    @Override
    public String toString() {
        return name() + "/" + code;
    }

    public static Error getErrorByCode(int code) {
        for(Error i: Error.values()) {
            if(code == i.code) {
                return i;
            }
        }
        throw new RuntimeException("Unknown error code: " + code);
    }
}
