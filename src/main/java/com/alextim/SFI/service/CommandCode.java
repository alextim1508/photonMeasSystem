package com.alextim.SFI.service;

import lombok.AllArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
public enum CommandCode {
    SET_UP_SFI(0x0002, "Приведение СФИ в исходное положение", new HashMap<>()),
//        LANDING_TYPE(0x0003, "Тип приземления", Map.of(1, "Есть раскрытие ПУ/Есть отстрел ЛТЭ",
//                                                                2, "Есть раскрытие ПУ/Нет отстрела ЛТЭ",
//                                                                3, "Нет раскрытия ПУ/Есть отстрел ЛТЭ",
//                                                                4, "Нет раскрытия ПУ/Нет отстрела ЛТЭ")),

    SETTING_FAULT_FLAGS_FOR_CHECKS(0x0005, "Задание флагов отказов для проверки CФИ на этапе изготовления", null);

    int code;

    String desc;

    Map<Integer, String> params;
}