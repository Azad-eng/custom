package com.ryl.customControls.other.directoryListView;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author: EFL-ryl
 */
public class DirExplorer extends Application {
    private final DirectoryListView dirListView;

    public DirExplorer() {
        this.dirListView = new DirectoryListView();
    }

    @Override
    public void start(final Stage stage) {
        final Path startDir = Paths.get("E:\\TJL\\IDEA\\PotatoOSp-8601-Client");
        dirListView.setCurrentDirectory(startDir);

        final Scene scene = new Scene(dirListView);
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop() {
        dirListView.unwatchDirectory();
    }
}
