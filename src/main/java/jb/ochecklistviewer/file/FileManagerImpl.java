package jb.ochecklistviewer.file;

import com.google.common.base.Preconditions;
import jb.ochecklistviewer.data.StatisticUtils;
import jb.ochecklistviewer.ui.UIReportListener;
import jb.ochecklistviewer.ui.UIStatusListener;
import jb.ochecklistviewer.yaml.YamlReaderWriter;
import jb.ochecklistviewer.yaml.YamlReaderWriterException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.swing.*;
import java.io.File;


@Getter
@RequiredArgsConstructor
public class FileManagerImpl implements FileManager {

    private File lastFile;


    public void readFile(File file, UIReportListener UIReportListener, UIStatusListener UIStatusListener) {
        // It is called in AWT thread, it is not so good ... think about refactoring :)
        Preconditions.checkArgument(SwingUtilities.isEventDispatchThread(), "This method must be called in AWT thread");

        lastFile = file;
        try {
            var checklistReport = YamlReaderWriter.readReport(file);
            var statisctics = StatisticUtils.calculate(checklistReport);
            UIReportListener.update(checklistReport.getRunners(), statisctics);
            UIStatusListener.update("File '%s' was successfully loaded".formatted(file.getName()));
        } catch (YamlReaderWriterException e) {
            UIStatusListener.update("File '%s' was not loaded: %s".formatted(file.getName(), e.getMessage()));
        }
    }
}
