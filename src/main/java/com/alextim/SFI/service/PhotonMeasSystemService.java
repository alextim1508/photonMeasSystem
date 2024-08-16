package com.alextim.SFI.service;

import com.alextim.SFI.service.MkoService.MKOMessage;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import static com.alextim.SFI.frontend.view.param.ParamController.Setting;
import static com.alextim.SFI.service.MkoService.MKOBus.MKO_BUS_A;
import static com.alextim.SFI.service.MkoService.MKODirection.MKO_DIR_READ;
import static com.alextim.SFI.service.MkoService.MKODirection.MKO_DIR_WRITE;
import static com.alextim.SFI.service.PhotonMeasSystemService.Address.ADDR_MAIN_CHANNEL;
import static com.alextim.SFI.service.PhotonMeasSystemService.CommandParams.*;
import static com.alextim.SFI.service.PhotonMeasSystemService.SubAddress.*;

@Slf4j
public class PhotonMeasSystemService {

    private MkoService mkoService;

    @AllArgsConstructor
    public enum Address {
        ADDR_MAIN_CHANNEL((byte) 9);

        public final byte addr;
    }

    @AllArgsConstructor
    public enum SubAddress {
        SUB_ADDR_READ_MEAS_DATA((byte) 1, "Передача результатов измерения"),
        SUB_ADDR_SET_COMMAND((byte) 2, "Задание команды управления"),
        SUB_ADDR_SET_TECHNOLOGY_COMMAND((byte) 4, "Задание технологической команды");

        public final byte subAddr;
        public final String title;
    }

    @AllArgsConstructor
    public enum TechnologyCommands {
        CLOSE_TRANSMITTER((short) 1, "Закрыть передатчик"),
        OPEN_TRANSMITTER((short) 2, "Открыть передатчик");
        public final short code;
        public final String title;
    }

    @AllArgsConstructor
    public enum Commands {
        SET_UP((short) 2, "Приведение СФИ в исходное положение", new ArrayList<>(), new ArrayList<>()),
        SET_LENDING_TYPE((short) 3, "Тип приземления", List.of(LENDING_OK, LENDING_FAIL), new ArrayList<>()),
        SET_STATE((short) 4, "Задание состояния ПУ и ЛТЭ", List.of(OPENING_PU_OK, OPENING_PU_FAIL), List.of(SHOOTING_LTE_OK, SHOOTING_LTE_FAIL)),
        SET_FAIL_FLAG((short) 5, "Задание флагов отказа", new ArrayList<>(), new ArrayList<>());

        public final short code;
        public final String title;
        public final List<CommandParams> params1;
        public final List<CommandParams> params2;
    }

    @AllArgsConstructor
    public enum CommandParams {
        LENDING_OK((byte) 1, "Посадка на полигон"),
        LENDING_FAIL((byte) 0, "Посадка аварийная за пределы полигона"),
        OPENING_PU_OK((byte) 1, "Есть раскрытие ПУ"),
        OPENING_PU_FAIL((byte) 0, "Нет раскрытия ПУ"),
        SHOOTING_LTE_OK((byte) 1, "Есть отстрел ЛТЭ"),
        SHOOTING_LTE_FAIL((byte) 0, "Нет отстрела ЛТЭ");

        public final byte code;
        public final String title;
    }

    public PhotonMeasSystemService(MkoService mkoService) {
        this.mkoService = mkoService;
    }

    @SneakyThrows
    public void readMeasResults(Setting setting, Consumer<short[]> consumer, AtomicBoolean isStop, long amount) {
        MKOMessage mkoMessage = new MKOMessage(
                MKO_BUS_A,
                MKO_DIR_READ,
                ADDR_MAIN_CHANNEL.addr,
                SUB_ADDR_READ_MEAS_DATA.subAddr,
                new short[32]);

        mkoService.transfer(mkoMessage, setting, consumer, isStop, amount);
    }

    public void sendTechnologyCommand(Setting setting, short command, short param) {
        MKOMessage mkoMessage = new MKOMessage(
                MKO_BUS_A,
                MKO_DIR_WRITE,
                ADDR_MAIN_CHANNEL.addr,
                SUB_ADDR_SET_TECHNOLOGY_COMMAND.subAddr,
                new short[2]);

        mkoMessage.data[0] = command;
        mkoMessage.data[1] = param;

        mkoService.transfer(mkoMessage, setting, data -> {
        }, new AtomicBoolean(false), 1);
    }

    public void command(Setting setting, short command, short param) {
        MKOMessage mkoMessage = new MKOMessage(
                MKO_BUS_A,
                MKO_DIR_WRITE,
                ADDR_MAIN_CHANNEL.addr,
                SUB_ADDR_SET_COMMAND.subAddr,
                new short[2]);

        mkoMessage.data[0] = command;
        mkoMessage.data[1] = param;

        mkoService.transfer(mkoMessage, setting, data -> {
        }, new AtomicBoolean(false), 1);
    }
}
