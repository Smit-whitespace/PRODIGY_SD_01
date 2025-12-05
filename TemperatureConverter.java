// TemperatureConverter.java
// Compile: javac TemperatureConverter.java
// Run:     java TemperatureConverter

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;

public class TemperatureConverter extends JFrame {
    private final JTextField inputField = new JTextField(12);
    private final JComboBox<String> unitBox = new JComboBox<>(new String[] {"Celsius (°C)", "Fahrenheit (°F)", "Kelvin (K)"});
    private final JLabel cLabel = new JLabel("—");
    private final JLabel fLabel = new JLabel("—");
    private final JLabel kLabel = new JLabel("—");
    private final DecimalFormat df = new DecimalFormat("#0.00");

    public TemperatureConverter() {
        super("Temperature Converter — C ⇄ F ⇄ K");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(420, 260);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(8, 8));
        initMenu();
        add(buildMainPanel(), BorderLayout.CENTER);
        add(buildFooter(), BorderLayout.SOUTH);

        // Keyboard: Enter = convert, ESC = clear
        getRootPane().setDefaultButton(findConvertButton());
        getRootPane().registerKeyboardAction(e -> clearAll(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW);
    }

    private void initMenu() {
        JMenuBar menuBar = new JMenuBar();
        JMenu file = new JMenu("File");
        JMenuItem exit = new JMenuItem("Exit");
        exit.addActionListener(e -> System.exit(0));
        file.add(exit);
        menuBar.add(file);

        JMenu help = new JMenu("Help");
        JMenuItem about = new JMenuItem("About");
        about.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "Temperature Converter\nCelsius, Fahrenheit, Kelvin\nMade with Java Swing",
                "About", JOptionPane.INFORMATION_MESSAGE));
        help.add(about);
        menuBar.add(help);

        setJMenuBar(menuBar);
    }

    private JPanel buildMainPanel() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(new EmptyBorder(12, 12, 12, 12));
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(6, 6, 6, 6);
        gc.anchor = GridBagConstraints.WEST;

        // Row 0: input + unit
        gc.gridx = 0; gc.gridy = 0;
        p.add(new JLabel("Temperature:"), gc);

        gc.gridx = 1;
        p.add(inputField, gc);
        inputField.setToolTipText("Enter numeric value (e.g. 25 or -3.2)");

        gc.gridx = 2;
        p.add(new JLabel("Unit:"), gc);

        gc.gridx = 3;
        p.add(unitBox, gc);
        unitBox.setToolTipText("Select the unit of the value you entered");

        // Row 1: Buttons
        gc.gridx = 1; gc.gridy = 1;
        JButton convertBtn = new JButton("Convert");
        convertBtn.setMnemonic(KeyEvent.VK_C);
        convertBtn.addActionListener(e -> performConversion());
        p.add(convertBtn, gc);

        gc.gridx = 2;
        JButton clearBtn = new JButton("Clear");
        clearBtn.setMnemonic(KeyEvent.VK_L);
        clearBtn.addActionListener(e -> clearAll());
        p.add(clearBtn, gc);

        gc.gridx = 3;
        JButton exitBtn = new JButton("Exit");
        exitBtn.addActionListener(e -> System.exit(0));
        p.add(exitBtn, gc);

        // Bind Enter on inputField to convert
        inputField.addActionListener(e -> performConversion());

        // Row 2: separator
        gc.gridx = 0; gc.gridy = 2; gc.gridwidth = 4;
        JSeparator sep = new JSeparator(SwingConstants.HORIZONTAL);
        sep.setPreferredSize(new Dimension(380, 1));
        p.add(sep, gc);
        gc.gridwidth = 1;

        // Row 3..5: Results
        gc.gridy = 3; gc.gridx = 0;
        p.add(new JLabel("Celsius:"), gc);
        gc.gridx = 1;
        p.add(cLabel, gc);

        gc.gridy = 4; gc.gridx = 0;
        p.add(new JLabel("Fahrenheit:"), gc);
        gc.gridx = 1;
        p.add(fLabel, gc);

        gc.gridy = 5; gc.gridx = 0;
        p.add(new JLabel("Kelvin:"), gc);
        gc.gridx = 1;
        p.add(kLabel, gc);

        // Tooltip for results
        cLabel.setToolTipText("Converted Celsius value");
        fLabel.setToolTipText("Converted Fahrenheit value");
        kLabel.setToolTipText("Converted Kelvin value");

        return p;
    }

    // Helper to find the default convert button (we set it earlier)
    private JButton findConvertButton() {
        // This is a small helper that creates a temporary button to set as default.
        // The convert button in the UI already exists and is the rootPane default; return a dummy.
        return new JButton(); // not used directly
    }

    private JPanel buildFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel help = new JLabel("<html><small>Enter value → choose unit → press <b>Convert</b> (Enter). Esc to Clear.</small></html>");
        footer.add(help);
        return footer;
    }

    private void clearAll() {
        inputField.setText("");
        unitBox.setSelectedIndex(0);
        cLabel.setText("—");
        fLabel.setText("—");
        kLabel.setText("—");
        inputField.requestFocusInWindow();
    }

    private void performConversion() {
        String raw = inputField.getText().trim();
        if (raw.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a temperature value.", "Input required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double value;
        try {
            value = Double.parseDouble(raw);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid number format. Use e.g. 25 or -3.2", "Invalid input", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String selected = (String) unitBox.getSelectedItem();
        if (selected == null) selected = "Celsius (°C)";

        double c = 0, f = 0, k = 0;
        if (selected.startsWith("Celsius")) {
            c = value;
            f = cToF(c);
            k = cToK(c);
        } else if (selected.startsWith("Fahrenheit")) {
            f = value;
            c = fToC(f);
            k = cToK(c);
        } else { // Kelvin
            k = value;
            if (k < 0) {
                JOptionPane.showMessageDialog(this, "Kelvin cannot be negative. Kelvin is absolute temperature (>= 0).", "Invalid Kelvin", JOptionPane.ERROR_MESSAGE);
                return;
            }
            c = kToC(k);
            f = cToF(c);
        }

        // Display with two decimals
        cLabel.setText(df.format(c) + " °C");
        fLabel.setText(df.format(f) + " °F");
        kLabel.setText(df.format(k) + " K");
    }

    // Conversion formulas
    private double cToF(double c) { return (c * 9.0 / 5.0) + 32.0; }
    private double fToC(double f) { return (f - 32.0) * 5.0 / 9.0; }
    private double cToK(double c) { return c + 273.15; }
    private double kToC(double k) { return k - 273.15; }

    public static void main(String[] args) {
        // Use system look & feel if available
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> {
            TemperatureConverter app = new TemperatureConverter();
            app.setVisible(true);
        });
    }
}
