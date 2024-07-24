package com.alextim.SFI.frontend.widget.graphs;


import de.gsi.dataset.spi.DoubleDataSet;
import javafx.beans.property.SimpleStringProperty;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SimpleGraph extends AbstractGraph {

    public DoubleDataSet scoresDataSet;

    public SimpleGraph(SimpleStringProperty title,
                       SimpleStringProperty progressTitle) {
        super(title, null, progressTitle, null, null, null);
        scoresDataSet = new DoubleDataSet(title.get());
    }

    private final SimpleDateFormat format = new SimpleDateFormat("H:mm:ss:SSS");

    private final String promptFormat = "Номер: %d Высота: %d Время: %s";

    public void addPoint(int i, long x, long y) {
        scoresDataSet.add(x, y);
        scoresDataSet.addDataLabel(i, String.format(promptFormat, i, y, format.format(new Date(x))));
    }

    public void setAutoNotification(boolean res) {
        scoresDataSet.autoNotification().set(res);
        if(res)
            scoresDataSet.fireInvalidated(null);
    }


    public void clear() {
        scoresDataSet.clearData();
    }

    @Override
    public List<DoubleDataSet> getDataSetList() {
        List<DoubleDataSet> list = new ArrayList<>();
        list.add(scoresDataSet);
        return list;
    }

    @Override
    public void reset() {
    }

    @Override
    public int size() {
        return 0;
    }
}
