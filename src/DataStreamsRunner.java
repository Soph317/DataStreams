import javax.swing.*;

public class DataStreamsRunner {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            DataStreamsFrame frame = new DataStreamsFrame();
            frame.setVisible(true);
        });
    }
}
