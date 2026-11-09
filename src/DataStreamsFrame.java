import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DataStreamsFrame extends JFrame {
    JPanel searchPnl, filePnl, textAreaPnl, controlPnl;
    JTextArea originalTxt, searchTxt;
    JTextField searchFld;
    JScrollPane originalScroll, searchScroll;
    JButton searchBtn, loadBtn,quitBtn;
    JLabel currentLbl, searchPromptLbl;

    Path loadedFilePath;

    public DataStreamsFrame() {
        setTitle("Data Streams");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 600);
        setLayout(new BorderLayout());

        createSearchPnl();
        createFilePnl();
        createControlPnl();

        add(searchPnl, BorderLayout.NORTH);
        add(filePnl, BorderLayout.CENTER);
        add(controlPnl, BorderLayout.SOUTH);
    }

    private void createSearchPnl() {
        searchPnl = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        searchPromptLbl = new JLabel("Search String:");
        searchFld = new JTextField(25);
        searchBtn = new JButton("Search");

        searchBtn.setEnabled(false);
        searchBtn.addActionListener(e -> searchFile());

        searchPnl.add(searchPromptLbl);
        searchPnl.add(searchFld);
        searchPnl.add(searchBtn);
    }

    private void createFilePnl() {
        filePnl = new JPanel(new BorderLayout(0, 10));
        textAreaPnl = new JPanel(new GridLayout(1, 2, 10, 0));
        currentLbl = new JLabel("Current Search: None", SwingConstants.CENTER);
        originalTxt = new JTextArea();
        searchTxt = new JTextArea();
        originalScroll = new JScrollPane(originalTxt);
        searchScroll = new JScrollPane(searchTxt);

        originalTxt.setEditable(false);
        searchTxt.setEditable(false);
        originalTxt.setLineWrap(true);
        searchTxt.setLineWrap(true);
        originalTxt.setWrapStyleWord(true);
        searchTxt.setWrapStyleWord(true);

        originalScroll.setBorder(BorderFactory.createTitledBorder("Original File"));
        searchScroll.setBorder(BorderFactory.createTitledBorder("Search Results"));
        filePnl.setBorder(BorderFactory.createEmptyBorder(5, 15, 10, 15));

        textAreaPnl.add(originalScroll);
        textAreaPnl.add(searchScroll);
        filePnl.add(currentLbl, BorderLayout.NORTH);
        filePnl.add(textAreaPnl, BorderLayout.CENTER);

    }

    private void createControlPnl() {
        controlPnl = new JPanel(new FlowLayout());
        loadBtn = new JButton("Load File");
        quitBtn = new JButton("Quit");

        loadBtn.addActionListener(e -> loadFile());
        quitBtn.addActionListener(e -> askToQuit());

        controlPnl.add(loadBtn);
        controlPnl.add(quitBtn);
    }

    private void loadFile() {
        JFileChooser chooser = new JFileChooser();
        int result = chooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            loadedFilePath = chooser.getSelectedFile().toPath();
            originalTxt.setText("");
            searchTxt.setText("");
            currentLbl.setText("Current Search: None");

            try (Stream<String> lines = Files.lines(loadedFilePath)) {
                lines.forEach(line -> originalTxt.append(line + "\n"));

                searchBtn.setEnabled(true);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(
                        this,
                        "Failed to read file: " + ex.getMessage(),
                        "File Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    private void searchFile() {
        String searchTarget = searchFld.getText().trim();
        if (searchTarget.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please enter a search string.",
                    "Search Warning",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        currentLbl.setText("Current Search: \"" + searchTarget + "\"");
        searchTxt.setText("");

        try (Stream<String> lines = Files.lines(loadedFilePath)) {
            String results = lines
                    .filter(line -> line.contains(searchTarget))
                    .collect(Collectors.joining("\n"));

            searchTxt.setText(results);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Error processing stream: " + ex.getMessage(),
                    "Stream Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void askToQuit() {
        int choice = JOptionPane.showConfirmDialog(this, "Are you sure you want to quit?", "Quit", JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }
}