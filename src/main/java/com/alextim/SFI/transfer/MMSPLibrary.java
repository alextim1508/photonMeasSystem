package com.alextim.SFI.transfer;

import com.alextim.SFI.transfer.Handles.*;
import com.sun.jna.Library;

/*
https://github.com/java-native-access/jna/blob/master/www/Mappings.md


*/
public interface MMSPLibrary extends Library {

    /* Инициализация RTL */
    int msp_Startup();

    /* Завершение работы RTL */
    int msp_Cleanup();

    /* Возвращает количество опознанных устройств (-1 в случае ошибки) */
    int msp_GetNumberOfDevices();

    /* Открывает устройство для данного приложения */
    DevHandle msp_Open(int devIndex);

    /* Закрывает устройство для данного приложения */
    int msp_Close(DevHandle devHandle);

    /* Комплексное конфигурирование устройства */
    int msp_Configure(
            DevHandle devHandle,        // устройство
            int mode,                   // множество (битовая маска) включаемых режимов
            int[] flags,                // список флажков, устанавливаемых в “1”. м.б. NULL
            int[] regvalues             // regvalues – список числовых параметров и их значений, м.б. NULL
    );

    /* Создает кадр на заданное число сообщений, исходно пустой */
    FrmHandle msp_CreateFrame(
            DevHandle devHandle,    // устройство
            short frameTime,          // длительность кадра в единицах по 100 мкс
            short msgCount            // число сообщений в кадре
    );


    /* Форматирование заготовки сообщения и размещение его в структуре msp_Message */
    Message.ByReference msp_FormatMessage(
            Message.ByReference buffer,     // буфер для заготовки сообщения
            byte mesType,                   // тип сообщения (одна из констант mspM_XXXX)
            byte RT,                        // адрес (0-31)
            byte SA,                        // подадрес (0-31)
            byte RTR_MC,                    // адрес ОУ получателя для mspM_RTtoRT
            short SAR_MCD,                  // код и данные для mspM_MODECODE_XXXX
            byte dataWordCount,             // число слов данных
            short[] data,                   // данные. Может быть NULL, в этом случае поле buffer->data не инициализируется
            int bccw                       // управляющее слово КШ (bitmapped). 3 мл. разряда игнорируются
    );

    // Создание сообщения в ОЗУ МСП
    MsgHandle msp_CreateMessage(
            DevHandle devHandle,            // идентификатор устройства
            Message.ByReference buffer      // заготовка сообщения
    );

    //Добавление сообщений в кадр
    int msp_AddMessage(
            FrmHandle frame,    // кадр
            MsgHandle message,  // сообщение
            short msggap        // интервал между началом данного и началом следующего сообщения, мкс
    );

    //Загрузка данного кадра в указатели стека А или B.
    int msp_LoadFrame(
            FrmHandle frame,    // кадр
            int flags           // комбинация значений
    );


    //Выдача команды. (Запись в командный регистр)
    int msp_Command(
            DevHandle devHandle,    // устройство
            int command             // код команды
    );

    //Считывание сообщения из ОЗУ МСП в ОЗУ хоста, совместно со словами состояния из дескриптора сообщения в данном стеке/кадре
    int msp_RetrieveMessage(StkHandle stkHandle,       // стек; допускается RT-стек, MT-стек или BC-фрейм
                            int entry,                 // номер дескриптора сообщения в кадре
                            Message.ByReference buffer // буфер для считывания сообщения. В буфере заполняются дополнительные поля StatusWord1/2, loopback, BSW, timetag.

    );

    //Копирование слов данных сообщения из ОЗУ МСП
    int msp_ReadMessageData(MsgHandle message,
                            short[] buffer, //буфер для приема слов данных;
                            int wordcount   //число слов, которые надо скопировать.
    );

    //Этой функцией можно удалить любой объект из ОЗУ МСП
    int msp_DestroyHandle(RamHandle handle);
}
