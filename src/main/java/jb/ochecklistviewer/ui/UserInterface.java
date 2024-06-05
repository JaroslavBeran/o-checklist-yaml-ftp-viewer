package jb.ochecklistviewer.ui;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.matchers.MatcherEditor;
import ca.odell.glazedlists.swing.AdvancedTableModel;
import ca.odell.glazedlists.swing.GlazedListsSwing;
import ca.odell.glazedlists.swing.TableComparatorChooser;
import ca.odell.glazedlists.swing.TextComponentMatcherEditor;
import jb.ochecklistviewer.config.AppConfig;
import jb.ochecklistviewer.config.FtpConfig;
import jb.ochecklistviewer.data.*;
import jb.ochecklistviewer.file.FileManager;
import jb.ochecklistviewer.ftp.FtpManager;
import lombok.AllArgsConstructor;
import net.miginfocom.swing.MigLayout;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serial;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.NumberFormat;
import java.util.List;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static jb.ochecklistviewer.ui.UIConstants.TABLE_COLUMN_DEFAULT_WIDTH;


public class UserInterface extends JPanel {

    @Serial
    private static final long serialVersionUID = 8778656409466134442L;
    private final JTable table;
    private final EventList<RunnerData> runners = new BasicEventList<>();


    public UserInterface(AppConfig appConfig, FtpManager ftpManager, FileManager fileManager) {

        /////////////
        // Status bar
        JPanel statusPanel = new JPanel(new MigLayout(""));
        statusPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));

        JLabel statusBarLabel = new JLabel("FTP connection ", SwingConstants.LEFT);
        statusPanel.add(statusBarLabel, "");

        URL iconResource = UserInterface.class.getResource("/jb/ochecklistviewer/ui/info.png");
        Image image = Toolkit.getDefaultToolkit().getImage(iconResource);
        ImageIcon icon = new ImageIcon(image);
        JButton btnInfo = new JButton(icon);

        btnInfo.addActionListener(e ->
                JOptionPane.showConfirmDialog(null, infoPanel(), "Info",
                        JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE)
        );


        UIStatusListener uiStatusListener = status -> {
            if (SwingUtilities.isEventDispatchThread()) {
                statusBarLabel.setText(status);
            } else {
                SwingUtilities.invokeLater(() -> statusBarLabel.setText(status));
            }
        };


        /////////////
        // Statistics
        JPanel statisticsPanel = new JPanel(new MigLayout("insets -1", "[]0[30]10[]0[30]10[]0[30]", "[]0[]"));
        statisticsPanel.setBorder(BorderFactory.createTitledBorder("Statistics"));

        statisticsPanel.add(new JLabel("YAML itms:"), "");
        JLabel yamlItems = new JLabel("0", SwingConstants.RIGHT);
        statisticsPanel.add(yamlItems, "w 100%");

        statisticsPanel.add(new JLabel("Cards:"), "");
        JLabel cardChanges = new JLabel("0", SwingConstants.RIGHT);
        statisticsPanel.add(cardChanges, "w 100%");

        statisticsPanel.add(new JLabel("Comments:"), "");
        JLabel comments = new JLabel("0", SwingConstants.RIGHT);
        statisticsPanel.add(comments, "w 100%, wrap");

        statisticsPanel.add(new JLabel("Start OKs:"), "");
        JLabel starts = new JLabel("0", SwingConstants.RIGHT);
        statisticsPanel.add(starts, "w 100%");

        statisticsPanel.add(new JLabel("DNS:"), "");
        JLabel dnses = new JLabel("0", SwingConstants.RIGHT);
        statisticsPanel.add(dnses, "w 100%");

        statisticsPanel.add(new JLabel("Late starts:"), "");
        JLabel lateStarts = new JLabel("0", SwingConstants.RIGHT);
        statisticsPanel.add(lateStarts, "w 100%");

        @AllArgsConstructor
        class ReportUpdate implements Runnable {

            List<RunnerData> newRunners;
            Statistics statistics;

            private int calculateHashCode(List<RunnerData> runners) {
                var hashCode = 1;
                for (RunnerData runner : runners) {
                    hashCode = 31 * hashCode + (runner.isDisappearedFromYaml() ? 1 : 0);
                }
                return hashCode;
            }

            private boolean updateData() {
                int hashCodeBeforeUpdate = calculateHashCode(runners);

                newRunners.forEach(newRunner -> {
                    if (runners.contains(newRunner)) {
                        int runnerDataCurrentIndex = runners.indexOf(newRunner);
                        RunnerData runnerDataCurrent = runners.get(runnerDataCurrentIndex);
                        runnerDataCurrent.setDisappearedFromYaml(false);
                        int hashCodeCurrentRunnerData = HashCodeBuilder.reflectionHashCode(runnerDataCurrent);
                        int hashCodeNewRunnerData = HashCodeBuilder.reflectionHashCode(newRunner);
                        if (hashCodeCurrentRunnerData != hashCodeNewRunnerData) {
                            runners.set(runnerDataCurrentIndex, newRunner);
                        }
                    } else {
                        runners.add(newRunner);
                    }
                });

                Set<RunnerData> newRunnersSet = new HashSet<>(newRunners);
                Set<RunnerData> runnersSet = new HashSet<>(runners);
                if (!newRunnersSet.containsAll(runnersSet)) {
                    // New YAML has less runners than tableModel (runners) currently has,
                    // it means some runners from the YAML were "removed" -> mark them as "disappearedFromYaml"
                    runnersSet.removeAll(newRunnersSet);
                    runners.stream().filter(runnersSet::contains).forEach(runner -> runner.setDisappearedFromYaml(true));
                }

                int hashCodeAfterUpdate = calculateHashCode(runners);
                return hashCodeBeforeUpdate != hashCodeAfterUpdate;
            }

            @Override
            public void run() {
                if (updateData()) {
                    table.repaint();
                }

                yamlItems.setText(String.valueOf(statistics.getYamlItems()));
                starts.setText(String.valueOf(statistics.getStarts()));
                dnses.setText(String.valueOf(statistics.getDnses()));
                lateStarts.setText(String.valueOf(statistics.getLateStarts()));
                cardChanges.setText(String.valueOf(statistics.getCardChanges()));
                comments.setText(String.valueOf(statistics.getComments()));
            }
        }

        UIReportListener uiReportListener = (newRunners, statistics) -> {
            var newReportUpdate = new ReportUpdate(newRunners, statistics);
            if (SwingUtilities.isEventDispatchThread()) {
                newReportUpdate.run();
            } else {
                SwingUtilities.invokeLater(newReportUpdate);
            }
        };

        ManualFileHandler manualFileHandler = file -> {
            runners.clear();
            fileManager.readFile(file, uiReportListener, uiStatusListener);
        };

        /////////////
        // File Panel
        JPanel filePanel = new JPanel(new MigLayout(""));
        DropHandler dropHandler = new DropHandler(manualFileHandler);
        new DropTarget(filePanel, DnDConstants.ACTION_COPY_OR_MOVE, dropHandler, true);

        filePanel.setBorder(BorderFactory.createTitledBorder("Local file"));

        JLabel filePath = new JLabel("Drop YAML file here or click to choose a file");
        filePanel.add(filePath);

        filePanel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (!filePath.isEnabled()) {
                    return;
                }

                JFileChooser fc = new JFileChooser();
                fc.setDialogTitle("Select O-Checklist YAML file ... ");
                fc.setFileFilter(new FileFilter() {
                    final Predicate<File> isYaml = file -> file.getName().endsWith(".yaml");
                    final Predicate<File> isDir = File::isDirectory;


                    @Override
                    public boolean accept(File f) {
                        return isYaml.or(isDir).test(f);
                    }


                    @Override
                    public String getDescription() {
                        return "*.yaml";
                    }
                });
                fc.setSelectedFile(fileManager.getLastFile());
                int retVal = fc.showOpenDialog(null);

                if (retVal == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fc.getSelectedFile();
                    manualFileHandler.handleFile(selectedFile);
                }
            }

        });

        ///////////////
        // FTP Settings Panel
        JPanel ftpSettingPanel = new JPanel(new MigLayout("", "[][250!]", "[][][][]20[][]"));

        ftpSettingPanel.add(new JLabel("User:"), "");
        JTextField ftpUser = new JTextField(appConfig.getFtpConfig().user());
        ftpSettingPanel.add(ftpUser, "growx, wrap");

        ftpSettingPanel.add(new JLabel("Pass:"), "");
        JPasswordField ftpPass = new JPasswordField(appConfig.getFtpConfig().pass());
        ftpSettingPanel.add(ftpPass, "growx, wrap");

        ftpSettingPanel.add(new JLabel("Server:"), "");
        JTextField ftpServerUrl = new JTextField(appConfig.getFtpConfig().server());
        ftpSettingPanel.add(ftpServerUrl, "growx, wrap");

        ftpSettingPanel.add(new JLabel("Port:"), "");
        NumberFormat numberFormat = NumberFormat.getNumberInstance();
        NumberFormatter ftpPortNumFormatter = new NumberFormatter(numberFormat);
        ftpPortNumFormatter.setValueClass(Integer.class);
        ftpPortNumFormatter.setMinimum(FtpConfig.FTP_PORT_MIN);
        ftpPortNumFormatter.setMaximum(FtpConfig.FTP_PORT_MAX);
        ftpPortNumFormatter.setAllowsInvalid(false);
        JFormattedTextField ftpServerPort = new JFormattedTextField(ftpPortNumFormatter);
        ftpServerPort.setText(String.valueOf(appConfig.getFtpConfig().port()));
        ftpServerPort.setToolTipText("FTP port number: %d - %d".formatted(
                FtpConfig.FTP_PORT_MIN, FtpConfig.FTP_PORT_MAX));
        ftpSettingPanel.add(ftpServerPort, "growx, wrap");

        ftpSettingPanel.add(new JLabel("File:"), "");
        JTextField ftpServerFile = new JTextField(appConfig.getFtpConfig().file());
        ftpSettingPanel.add(ftpServerFile, "growx, wrap");

        ftpSettingPanel.add(new JLabel("Refresh:"), "");
        NumberFormatter ftpFileRefreshFormatter = new NumberFormatter(numberFormat);
        ftpFileRefreshFormatter.setValueClass(Integer.class);
        ftpFileRefreshFormatter.setMinimum(FtpConfig.REFRESH_MIN);
        ftpFileRefreshFormatter.setMaximum(FtpConfig.REFRESH_MAX);
        ftpFileRefreshFormatter.setAllowsInvalid(false);
        JFormattedTextField ftpFileRefresh = new JFormattedTextField(ftpFileRefreshFormatter);
        ftpFileRefresh.setText(String.valueOf(appConfig.getFtpConfig().refresh()));
        ftpFileRefresh.setToolTipText("Refresh FTP file each number of seconds: %d - %d"
                .formatted(FtpConfig.REFRESH_MIN, FtpConfig.REFRESH_MAX));
        ftpSettingPanel.add(ftpFileRefresh, "growx, wrap");

        JButton ftpSettingsBtn = new JButton("Settings");
        ftpSettingsBtn.addActionListener(e -> {
            int result = JOptionPane.showConfirmDialog(null, ftpSettingPanel, "FTP Settings",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (result == JOptionPane.OK_OPTION) {
                FtpConfig ftpConfig = new FtpConfig(ftpServerUrl.getText(), Integer.parseInt(ftpServerPort.getText()),
                        ftpUser.getText(), ftpPass.getText(), ftpServerFile.getText(),
                        Integer.parseInt(ftpFileRefresh.getText()));
                appConfig.setFtpConfig(ftpConfig);
            }
        });

        JToggleButton btnConnect = new JToggleButton("Connect");
        ActionListener toggleBtnActionListener = e -> {
            if (!appConfig.getFtpConfig().isValid()) {
                JOptionPane.showMessageDialog(null,
                        "FTP config is not valid. Open FTP settings and enter valid params.",
                        "Warning",
                        JOptionPane.WARNING_MESSAGE);
                btnConnect.getModel().setSelected(false);
                return;
            }

            if (btnConnect.getModel().isSelected()) {
                btnConnect.setText("Disconnect");
                ftpSettingsBtn.setEnabled(false);
                filePanel.setEnabled(false);
                filePath.setEnabled(false);
                dropHandler.setEnabled(false);
                runners.clear();
                ftpManager.startPolling(appConfig.getFtpConfig(), uiReportListener, uiStatusListener);
            } else {
                filePanel.setEnabled(true);
                filePath.setEnabled(true);
                dropHandler.setEnabled(true);
                ftpSettingsBtn.setEnabled(true);
                ftpManager.stopPolling();
                btnConnect.setText("Connect");
            }
        };
        btnConnect.addActionListener(toggleBtnActionListener);

        JPanel ftpPanel = new JPanel(new MigLayout("insets 1"));
        ftpPanel.setBorder(BorderFactory.createTitledBorder("FTP"));
        ftpPanel.add(ftpSettingsBtn, "width pref!");
        ftpPanel.add(btnConnect, "width 100!");

        /////////////
        // Filter panel
        JPanel filterPanel = new JPanel(new MigLayout("insets 1, fillx", "[][grow]"));
        filterPanel.setBorder(BorderFactory.createTitledBorder("Filter"));
        filterPanel.add(new JLabel("Text to filter:"));

        JTextField filterTextField = new JTextField();
        filterTextField.registerKeyboardAction(e -> filterTextField.setText(""),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false), WHEN_FOCUSED);
        filterPanel.add(filterTextField, "w 80::, growx");

        /////////////
        // Table
        SortedList<RunnerData> sortedEntries = new SortedList<>(runners);

        RunnerFilterator runnerFilterator = new RunnerFilterator();
        MatcherEditor<RunnerData> textMatcherEditor = new TextComponentMatcherEditor<>(filterTextField,
                runnerFilterator);
        FilterList<RunnerData> textFilteredIssues = new FilterList<>(sortedEntries, textMatcherEditor);

        AdvancedTableModel<RunnerData> tableModel = GlazedListsSwing
                .eventTableModelWithThreadProxyList(textFilteredIssues, new ChecklistEntryTableFormat());

        table = new JTable(tableModel);
        table.setRowHeight(20);

        table.setSelectionModel(GlazedListsSwing.eventSelectionModel(textFilteredIssues));

        final String menuItemLabelPrefix = "Copy value: ";
        JMenuItem menuItem = new JMenuItem(menuItemLabelPrefix);
        menuItem.addActionListener(e -> {
            String string = menuItem.getText().substring(menuItemLabelPrefix.length());
            StringSelection stringSelection = new StringSelection(string);
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(stringSelection, stringSelection);
        });

        var popup = new JPopupMenu();
        popup.add(menuItem);
        table.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    int row = table.rowAtPoint(e.getPoint());
                    int column = table.columnAtPoint(e.getPoint());

                    Object string = table.getValueAt(row, column);
                    menuItem.setText(menuItemLabelPrefix + string);
                    popup.show(table, e.getX(), e.getY());
                }
            }


            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    int row = table.rowAtPoint(e.getPoint());
                    int column = table.columnAtPoint(e.getPoint());

                    Object string = table.getValueAt(row, column);
                    menuItem.setText(menuItemLabelPrefix + string);
                    popup.show(table, e.getX(), e.getY());
                }
            }

        });

        var tableCellRenderer = new TableCellRenderer(tableModel);
        var statusCellRenderer = new StartStatusCellRenderer(tableModel);
        for (int columnIdx = 0; columnIdx < tableModel.getColumnCount(); columnIdx++) {
            if (columnIdx == 6) {
                table.getColumnModel().getColumn(columnIdx).setCellRenderer(statusCellRenderer);
            } else {
                table.getColumnModel().getColumn(columnIdx).setCellRenderer(tableCellRenderer);
            }
        }

        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        TableComparatorChooser<RunnerData> tableComparatorChooser =
                TableComparatorChooser.install(
                        table,
                        sortedEntries,
                        TableComparatorChooser.MULTIPLE_COLUMN_MOUSE);

        setTabColumns(appConfig, tableComparatorChooser);

        var scrollPane = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        /////////////
        // Top panel - group FTP, File, Filter and Statistic panels into one
        // panel with auto-wrap layout possibility
        JPanel topPanel = new JPanel(new MigLayout("insets 0, h :pref:, wrap 4", "", ""));
        topPanel.add(ftpPanel, "shrink 0");
        topPanel.add(filePanel, "shrink 0");
        topPanel.add(statisticsPanel, "shrink 0");
        topPanel.add(filterPanel, "w 120::, shrink 0, pushx, growx");


        /////////////
        // Bottom panel
        JPanel bottomPanel = new JPanel(new MigLayout("insets 0"));
        bottomPanel.add(statusPanel, "growx, pushx");
        bottomPanel.add(btnInfo, "");


        /////////////
        // Main panel
        setLayout(new MigLayout("wrap", "[grow]", "[top][center, grow][bottom]"));
        add(topPanel, "growx");
        add(scrollPane, "grow");
        add(bottomPanel, "growx");
    }


    private JPanel infoPanel() {
        JPanel infoPanel = new JPanel(new MigLayout("", "[][]", "[][][][]20[][]"));
        JLabel appNameLabel = new JLabel("O-Checklist YAML FTP Viewer");
        appNameLabel.setFont(appNameLabel.getFont().deriveFont(Font.BOLD, 15));
        infoPanel.add(appNameLabel, "span 2, wrap");

        infoPanel.add(new JLabel("Version:"));
        infoPanel.add(new JLabel(getVersion()), "wrap");

        infoPanel.add(new JLabel("Author:"));
        infoPanel.add(new JLabel("Jaroslav Beran"), "wrap");

        var visitGitHub = "https://github.com/JaroslavBeran/o-checklist-yaml-ftp-viewer";
        infoPanel.add(new JLabel("GIT:"));
        JLabel gitLink = new JLabel(visitGitHub);
        gitLink.setBackground(Color.BLUE);
        gitLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        gitLink.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    Desktop.getDesktop().browse(new URI("https://github.com/JaroslavBeran/o-checklist-yaml-ftp-viewer"));
                } catch (IOException | URISyntaxException e1) {
                    // Exception doesn't matter
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                gitLink.setText("<html><a href='https://github.com/JaroslavBeran/o-checklist-yaml-ftp-viewer'>" + visitGitHub + "</a></html>");
            }

            @Override
            public void mouseExited(MouseEvent e) {
                gitLink.setText(visitGitHub);
            }
        });
        infoPanel.add(gitLink, "wrap");

        infoPanel.add(new JLabel("Licence:"));
        infoPanel.add(new JLabel("MIT Licence"), "wrap");

        return infoPanel;
    }

    private String getVersion() {
        try {
            final Properties properties = new Properties();
            InputStream resourceAsStream = UserInterface.class.getClassLoader().getResourceAsStream("project.properties");
            if (resourceAsStream != null) {
                properties.load(resourceAsStream);
                return properties.getProperty("version");
            }
        } catch (IOException e) {
            // Exception doesn't matter
        }
        return "";
    }

    private void setTabColumns(AppConfig appConfig, TableComparatorChooser<RunnerData> tableComparatorChooser) {
        List<UIConstants.TabColumn> columnOrder = appConfig.getTable().columnPlacement();
        TableColumnModel columnModel = table.getColumnModel();

        EnumMap<UIConstants.TabColumn, Integer> columnWidths = appConfig.getTable().columnWidth();
        EnumMap<UIConstants.TabColumn, AppConfig.Order> columnSorting = appConfig.getTable().columnSorting();

        Stream.of(UIConstants.TabColumn.values()).forEach(tableColumnEnum -> {
            int columnIdx = columnModel.getColumnIndex(tableColumnEnum.getLabel());

            if (columnSorting.containsKey(tableColumnEnum)) {
                tableComparatorChooser.appendComparator(columnIdx, 0, columnSorting.get(tableColumnEnum).isOrder());
            }
        });

        Stream.of(UIConstants.TabColumn.values()).forEach(tableColumnEnum -> {
            int columnIdx = columnModel.getColumnIndex(tableColumnEnum.getLabel());
            TableColumn column = columnModel.getColumn(columnIdx);

            int width = columnWidths.getOrDefault(tableColumnEnum, TABLE_COLUMN_DEFAULT_WIDTH);
            column.setPreferredWidth(width);

            var requiredColumnIdx = columnOrder.indexOf(tableColumnEnum);
            if (requiredColumnIdx >= 0) {
                // Move the column to the required position
                columnModel.moveColumn(columnIdx, requiredColumnIdx);
            } else {
                // Hide the column
                columnModel.removeColumn(column);
            }
        });
    }
}
