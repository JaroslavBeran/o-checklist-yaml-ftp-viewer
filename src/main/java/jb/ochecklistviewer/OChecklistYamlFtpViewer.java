package jb.ochecklistviewer;

import jb.ochecklistviewer.config.AppConfig;
import jb.ochecklistviewer.config.AppConfigException;
import jb.ochecklistviewer.file.FileManager;
import jb.ochecklistviewer.file.FileManagerImpl;
import jb.ochecklistviewer.ftp.FtpManager;
import jb.ochecklistviewer.ftp.FtpManagerImpl;
import jb.ochecklistviewer.ui.UserInterface;
import jb.ochecklistviewer.yaml.YamlReaderWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;


public class OChecklistYamlFtpViewer {

    static Logger log = LoggerFactory.getLogger(OChecklistYamlFtpViewer.class);

    static AppConfig appConfig;
    static FtpManager ftpManager = new FtpManagerImpl();
    static FileManager fileManager = new FileManagerImpl();


    private static void createAndShowGUI() {
        try {
            UIManager.setLookAndFeel(new NimbusLookAndFeel());
            JFrame.setDefaultLookAndFeelDecorated(true);
        } catch (UnsupportedLookAndFeelException e) {
            log.warn("Cannot setup Nimbus L&F, using default.");
        }

        UserInterface ui = new UserInterface(appConfig, ftpManager, fileManager);

        JFrame frame = new JFrame("O-Checklist YAML FTP Viewer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                ftpManager.stopPolling();
                super.windowClosing(e);
            }
        });

        frame.getContentPane().add(ui);

        frame.setPreferredSize(new Dimension(appConfig.getAppWidth(), appConfig.getAppHeight()));
        Point centerPoint = GraphicsEnvironment.getLocalGraphicsEnvironment().getCenterPoint();
        frame.setLocation(centerPoint.x - appConfig.getAppWidth() / 2, centerPoint.y - appConfig.getAppHeight() / 2);
        frame.pack();
        frame.setVisible(true);
    }


    private static void readConfig(String userCfgFile) {
        if (userCfgFile != null) {
            Path userConfigFilePath = Paths.get(userCfgFile);
            try (FileInputStream userCfgFileInputStream = new FileInputStream(userConfigFilePath.toFile())) {
                appConfig = YamlReaderWriter.read(userCfgFileInputStream, AppConfig.class);
                log.info("Configuration read from user config file: {}", userCfgFile);
                return;
            } catch (InvalidPathException | IOException ex) {
                log.warn("Cannot read user config file: {} ... reading default from resource.", userCfgFile);
            }
        }

        try (InputStream is = OChecklistYamlFtpViewer.class
                .getResourceAsStream("/OChecklistYamlFtpViewer.config.yaml")) {
            log.info("... reading configuration from default resource");
            appConfig = YamlReaderWriter.read(is, AppConfig.class);
        } catch (IOException ex) {
            throw new AppConfigException("Cannot read application configuration from resource.", ex);
        }
    }


    public static void main(String[] args) {
        readConfig(args.length == 1 ? args[0] : null);

        SwingUtilities.invokeLater(OChecklistYamlFtpViewer::createAndShowGUI);
    }
}
