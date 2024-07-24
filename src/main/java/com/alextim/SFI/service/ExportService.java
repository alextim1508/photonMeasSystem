package com.alextim.SFI.service;

import com.alextim.SFI.frontend.view.param.ParamController.Setting;
import com.alextim.SFI.frontend.view.statics.StaticController.StatMeasResult;
import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import static com.alextim.SFI.frontend.view.statics.StaticController.StaticParam;

@Slf4j
public class ExportService {

    private final SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss:SSS");

    @SneakyThrows
    public void exportToFile(Map<Long, MeasResult> result, File file, BiConsumer<Integer, Double> progress) {
        log.info("export to file");

        @Cleanup
        FileWriter fileWriter = new FileWriter(file);

        Iterator<MeasResult> iterator = result.values().iterator();

        for (int i = 0; iterator.hasNext(); i++) {
            progress.accept(i + 1, 1.0 * (i + 1) / result.size());

            MeasResult next = iterator.next();

            fileWriter
                    .append(format.format(new Date(next.timestamp))).append("\t")
                    .append(Long.toString(next.packetID)).append("\t")
                    .append(Long.toString(next.height)).append("\t")
                    .append(Long.toString(next.frequency1)).append("\t")
                    .append(Long.toString(next.frequency2)).append("\t")
                    .append(Long.toString(next.frequency3)).append("\t")
                    .append(Long.toString(next.frequency4)).append("\t")
                    .append(String.format("%x", next.statusValue)).append("\t");

            for (Map.Entry<MsgStatus, Boolean> entry : next.status.entrySet()) {
                fileWriter.append(entry.getKey().name()).append(" : ").append(entry.getValue() ? "1" : "0").append("\t");
            }

            fileWriter.append(System.lineSeparator());
        }

        fileWriter.flush();

        log.info("export to file OK");
    }

    @SneakyThrows
    public void exportToFile(List<StatMeasResult> result, StaticParam staticParam, String serialNumber, String machineNumber, File file, BiConsumer<Integer, Double> progress) {
        log.info("export to file");

        if (machineNumber != null) {
            file = addSuffix(file, "_" + machineNumber);
        }

        @Cleanup
        FileWriter fileWriter = new FileWriter(file);

        fileWriter.append("// файл задания параметров для БОИ СФИ N ").append(serialNumber != null ? serialNumber : "").append(System.lineSeparator());
        fileWriter.append("// для конкретного аппарата").append(System.lineSeparator());
        fileWriter.append("// data").append(System.lineSeparator());
        fileWriter.append("// author").append(System.lineSeparator());
        fileWriter.append(System.lineSeparator());


        fileWriter.append("#define STATIC").append(System.lineSeparator());
        fileWriter.append("#define C_STATIC_FREQ 33").append(System.lineSeparator());
        fileWriter.append(System.lineSeparator());

        fileWriter.append("//-------in mm --------------------------").append(System.lineSeparator());
        fileWriter.append("#define HEIGHT_MAX \t\t30000").append(System.lineSeparator());
        fileWriter.append("#define HEIGHT_CRASH\t560").append(System.lineSeparator());
        fileWriter.append("#define HEIGHT_NOM\t\t1500").append(System.lineSeparator());
        fileWriter.append("#define HEIGHT_VALID\t10000").append(System.lineSeparator());
        fileWriter.append(System.lineSeparator());
        fileWriter.append(System.lineSeparator());


        fileWriter.append("//-------Default Param for PRM1 channel 1---------------").append(System.lineSeparator());
        fileWriter.append("#define PRM1_CH1_MAX\t").append(Long.toString(staticParam.prm1ch1Max)).append(System.lineSeparator());
        fileWriter.append("#define PRM1_CH1_MIN_FON\t").append(Long.toString(staticParam.prm1ch1MinFon)).append(System.lineSeparator());
        fileWriter.append("#define PRM1_CH1_MAX_FON\t").append(Long.toString(staticParam.prm1ch1MaxFon)).append(System.lineSeparator());

        fileWriter.append("//-------Default Param for PRM1 channel 2---------------").append(System.lineSeparator());
        fileWriter.append("#define PRM1_CH2_MAX\t").append(Long.toString(staticParam.prm1ch2Max)).append(System.lineSeparator());
        fileWriter.append("#define PRM1_CH2_MIN_FON\t").append(Long.toString(staticParam.prm1ch2MinFon)).append(System.lineSeparator());
        fileWriter.append("#define PRM1_CH2_MAX_FON\t").append(Long.toString(staticParam.prm1ch2MaxFon)).append(System.lineSeparator());

        fileWriter.append("//-------Default Param for PRM1 channel 2---------------").append(System.lineSeparator());
        fileWriter.append("#define PRM2_CH1_MAX\t").append(Long.toString(staticParam.prm2ch1Max)).append(System.lineSeparator());
        fileWriter.append("#define PRM2_CH1_MIN_FON\t").append(Long.toString(staticParam.prm2ch1MinFon)).append(System.lineSeparator());
        fileWriter.append("#define PRM2_CH1_MAX_FON\t").append(Long.toString(staticParam.prm2ch1MaxFon)).append(System.lineSeparator());

        fileWriter.append("//-------Default Param for PRM1 channel 2---------------").append(System.lineSeparator());
        fileWriter.append("#define PRM2_CH2_MAX\t").append(Long.toString(staticParam.prm2ch2Max)).append(System.lineSeparator());
        fileWriter.append("#define PRM2_CH2_MIN_FON\t").append(Long.toString(staticParam.prm2ch2MinFon)).append(System.lineSeparator());
        fileWriter.append("#define PRM2_CH2_MAX_FON\t").append(Long.toString(staticParam.prm2ch2MaxFon)).append(System.lineSeparator());
        fileWriter.append(System.lineSeparator());
        fileWriter.append(System.lineSeparator());


        Iterator<StatMeasResult> iterator = result.iterator();

        for (int i = 0; iterator.hasNext(); i++) {
            progress.accept(i + 1, 1.0 * (i + 1) / result.size());

            StatMeasResult next = iterator.next();

            fileWriter.append("//-------------").append(String.valueOf(next.refHeight)).append("-mm------------------").append(System.lineSeparator());
            fileWriter.append("#define HP_").append(String.valueOf(i)).append("\t").append(String.valueOf(next.refHeight)).append(System.lineSeparator());
            fileWriter.append("#define HP_").append(String.valueOf(i)).append("_F1\t").append(String.valueOf((long) next.frequency1)).append(System.lineSeparator());
            fileWriter.append("#define HP_").append(String.valueOf(i)).append("_F2\t").append(String.valueOf((long) next.frequency2)).append(System.lineSeparator());
            fileWriter.append("#define HP_").append(String.valueOf(i)).append("_F3\t").append(String.valueOf((long) next.frequency3)).append(System.lineSeparator());
            fileWriter.append("#define HP_").append(String.valueOf(i)).append("_F4\t").append(String.valueOf((long) next.frequency4)).append(System.lineSeparator());
        }

        fileWriter.flush();

        log.info("export to file OK");
    }

    private File addSuffix(File original, String suffix) {
        String fileName = original.getAbsolutePath();
        int extIndex = fileName.lastIndexOf(".");
        if (extIndex != -1) {
            String baseName = fileName.substring(0, extIndex);
            String extension = fileName.substring(extIndex);
            return new File(baseName + suffix + extension);
        }
        return new File(fileName + suffix);
    }


    @SneakyThrows
    public void exportToFile(StaticParam staticParam, File file) {
        log.info("export to file");

        @Cleanup
        FileWriter fileWriter = new FileWriter(file);

        fileWriter.append(Long.toString(staticParam.prm1ch1Max)).append(System.lineSeparator());
        fileWriter.append(Long.toString(staticParam.prm1ch1MaxFon)).append(System.lineSeparator());
        fileWriter.append(Long.toString(staticParam.prm1ch1MinFon)).append(System.lineSeparator());

        fileWriter.append(Long.toString(staticParam.prm1ch2Max)).append(System.lineSeparator());
        fileWriter.append(Long.toString(staticParam.prm1ch2MaxFon)).append(System.lineSeparator());
        fileWriter.append(Long.toString(staticParam.prm1ch2MinFon)).append(System.lineSeparator());

        fileWriter.append(Long.toString(staticParam.prm2ch1Max)).append(System.lineSeparator());
        fileWriter.append(Long.toString(staticParam.prm2ch1MaxFon)).append(System.lineSeparator());
        fileWriter.append(Long.toString(staticParam.prm2ch1MinFon)).append(System.lineSeparator());

        fileWriter.append(Long.toString(staticParam.prm2ch2Max)).append(System.lineSeparator());
        fileWriter.append(Long.toString(staticParam.prm2ch2MaxFon)).append(System.lineSeparator());
        fileWriter.append(Long.toString(staticParam.prm2ch2MinFon));

        fileWriter.append(System.lineSeparator());
        fileWriter.flush();

        log.info("export to file OK");
    }

    @SneakyThrows
    public StaticParam importStaticParamFromFile(File file) {
        @Cleanup
        BufferedReader br = new BufferedReader(new FileReader(file));

        long prm1ch1Max = Long.parseLong(br.readLine());
        long prm1ch1MaxFon = Long.parseLong(br.readLine());
        long prm1ch1MinFon = Long.parseLong(br.readLine());

        long prm1ch2Max = Long.parseLong(br.readLine());
        long prm1ch2MaxFon = Long.parseLong(br.readLine());
        long prm1ch2MinFon = Long.parseLong(br.readLine());

        long prm2ch1Max = Long.parseLong(br.readLine());
        long prm2ch1MaxFon = Long.parseLong(br.readLine());
        long prm2ch1MinFon = Long.parseLong(br.readLine());

        long prm2ch2Max = Long.parseLong(br.readLine());
        long prm2ch2MaxFon = Long.parseLong(br.readLine());
        long prm2ch2MinFon = Long.parseLong(br.readLine());

        return StaticParam.builder()
                .prm1ch1Max(prm1ch1Max)
                .prm1ch1MaxFon(prm1ch1MaxFon)
                .prm1ch1MinFon(prm1ch1MinFon)
                .prm1ch2Max(prm1ch2Max)
                .prm1ch2MaxFon(prm1ch2MaxFon)
                .prm1ch2MinFon(prm1ch2MinFon)
                .prm2ch1Max(prm2ch1Max)
                .prm2ch1MaxFon(prm2ch1MaxFon)
                .prm2ch1MinFon(prm2ch1MinFon)
                .prm2ch2Max(prm2ch2Max)
                .prm2ch2MaxFon(prm2ch2MaxFon)
                .prm2ch2MinFon(prm2ch2MinFon)
                .build();
    }


    @SneakyThrows
    public void exportToFile(Setting setting, File file) {
        log.info("export to file");

        @Cleanup
        FileWriter fileWriter = new FileWriter(file);

        fileWriter.append(Long.toString(setting.frameTime)).append(System.lineSeparator());
        fileWriter.append(Long.toString(setting.delayLoop)).append(System.lineSeparator());
        fileWriter.append(Long.toString(setting.fronBufferSize)).append(System.lineSeparator());

        fileWriter.append(System.lineSeparator());
        fileWriter.flush();

        log.info("export to file OK");
    }

    @SneakyThrows
    public Setting importSettingFromFile(File file) {
        @Cleanup
        BufferedReader br = new BufferedReader(new FileReader(file));

        int frameTime = Integer.parseInt(br.readLine());
        int delayLoop = Integer.parseInt(br.readLine());
        int fronBufferSize = Integer.parseInt(br.readLine());

        return new Setting(delayLoop, frameTime, fronBufferSize);
    }
}
