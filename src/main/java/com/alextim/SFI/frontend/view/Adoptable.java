package com.alextim.SFI.frontend.view;


import com.alextim.SFI.RootController;
import com.alextim.SFI.frontend.MainWindow;

public interface Adoptable {
    void adopt(RootController parent, MainWindow mainWindow, String name, NodeController controller);
}
