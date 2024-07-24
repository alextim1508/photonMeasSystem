package com.alextim.SFI.service;

import lombok.AllArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
public enum MsgStatus {
    SFI_OK(0, 15, "СФИ готов", "СФИ не готов"),
    PWRSELFTEST(0, 14, "Самотестирование при включении питания пройдено", "Самотестирование при включении питания не пройдено"),
    SELFTEST(0, 13, "Тест пройден", "Тест не пройден"),
    SELFTEST_STATUS(0, 12, "Самотестирование в процессе", "Самотестирование закончено"),
    PRM_2_K_2_OK(0, 11, "PRM2-K2 готов", "PRM2-K2 не готов"),
    PRM_2_K_1_OK(0, 10, "PRM2-K1 готов", "PRM2-K1 не готов"),
    PRM_1_K_2_OK(0, 9, "PRM1-K2 готов", "PRM1-K2 не готов"),
    PRM_1_K_1_OK(0, 8, "PRM1-K1 готов", "PRM1-K1 не готов"),
    PRD_ON(0, 7, "Передатчик открыт", ""),
    PRD_OFF(0, 6, "Передатчик закрыт", ""),
    PRD_STATUS(0, 5, "Передатчик в процессе выполнения команды", ""),
    H_OK(0, 4, "Данные достоверны", "Данные недостоверны"),
    POWEROFF_READY(0, 3, "Готов к отключения питания", "Не готов к отключению питания"),
    LANDING(0, 2, "Штатная посадка", "Нештатная посадка"),
    LTE(0, 0, "Отстрел ЛТЭ", ""),
    LTE_REJECT(1, 15, "Отказ ЛТЭ", ""),
    PU(1, 14, "Раскрытие ПУ", ""),
    PU_REJECT(1, 13, "Отказ ПУ", ""),
    LANDED(1, 12, "Приземлились", ""),
    DONT_LEND(1, 11, "Мы не сели, в полете", ""),
    PRD_CONN(1, 10, "ПРД подключен", "ПРД не подключен");

    public final int cd;
    public final int bit;
    public final String comment1;
    public final String comment0;

    public static Map<MsgStatus, Boolean> getByCode(short codeH, short codeL) {
        Map<MsgStatus, Boolean> statuses = new HashMap<>();
        for (MsgStatus s : MsgStatus.values()) {
            statuses.put(s, ( (s.cd == 0 ? codeL : codeH) & (1 << s.bit) ) != 0);
        }
        return statuses;
    }
}