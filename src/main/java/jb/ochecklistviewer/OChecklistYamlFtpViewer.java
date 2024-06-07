package jb.ochecklistviewer;

import jb.ochecklistviewer.config.AppConfig;
import jb.ochecklistviewer.config.CfgReader;
import jb.ochecklistviewer.file.FileManager;
import jb.ochecklistviewer.file.FileManagerImpl;
import jb.ochecklistviewer.ftp.FtpManager;
import jb.ochecklistviewer.ftp.FtpManagerImpl;
import jb.ochecklistviewer.ui.UserInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;


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


    public static void main(String[] args) {
        appConfig = CfgReader.readFile(args.length == 1 ? args[0] : null);

        SwingUtilities.invokeLater(OChecklistYamlFtpViewer::createAndShowGUI);
    }
}
