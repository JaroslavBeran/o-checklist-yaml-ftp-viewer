package jb.ochecklistviewer.config;

import jb.ochecklistviewer.OChecklistYamlFtpViewer;
import jb.ochecklistviewer.yaml.YamlReaderWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CfgReader {
    private static final Logger log = LoggerFactory.getLogger(CfgReader.class);

    static final String CFG_RESOURCE = "/OChecklistYamlFtpViewer.config.yaml";


    public static AppConfig readFile(String fileName) {
        if (fileName != null) {
            Path userConfigFilePath = Paths.get(fileName);
            try (FileInputStream userCfgFileInputStream = new FileInputStream(userConfigFilePath.toFile())) {
                log.info("... reading configuration from file: {}", fileName);
                var appConfig = YamlReaderWriter.read(userCfgFileInputStream, AppConfig.class);
                log.info("Configuration was read from user specified config file: {}", fileName);
                return appConfig;
            } catch (InvalidPathException | IOException ex) {
                log.warn("Cannot read user config file: {} ... reading default from resource.", fileName);
            }
        }

        try (InputStream is = OChecklistYamlFtpViewer.class.getResourceAsStream(CFG_RESOURCE)) {
            log.info("... reading configuration from default resource");
            var appConfig = YamlReaderWriter.read(is, AppConfig.class);
            log.info("Configuration was read from default resource.");
            return appConfig;
        } catch (IOException ex) {
            throw new AppConfigException("Cannot read application configuration from resource.", ex);
        }
    }
}
