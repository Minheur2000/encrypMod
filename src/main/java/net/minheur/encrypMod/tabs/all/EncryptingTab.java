package net.minheur.encrypMod.tabs.all;

import net.minheur.encrypMod.encrypt.EncryptLog;
import net.minheur.potoflux.logger.PtfLogger;
import net.minheur.potoflux.screen.tabs.BaseTab;

import javax.annotation.Nonnull;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;

import static net.minheur.encrypMod.encrypt.EncryptUtils.*;

public class EncryptingTab extends BaseTab {

    private File selectedFile;
    private JTextField keyField;

    private JButton selectButton;
    private JButton encryptButton;
    private JButton decryptButton;

    @Override
    protected void setPanel() {
        PANEL.setLayout(new BoxLayout(PANEL, BoxLayout.Y_AXIS));

        selectButton = new JButton("Select .txt file");
        encryptButton = new JButton("Encrypt & Save");
        decryptButton = new JButton("Decrypt & Save");

        setupButton();
    }

    private void setupButton() {
        keyField = new JTextField();

        keyField.setBorder(BorderFactory.createTitledBorder("12-char key"));
        keyField.setMaximumSize(new Dimension(250, 60));
        keyField.setPreferredSize(new Dimension(250, 30));

        selectButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        keyField.setAlignmentX(Component.CENTER_ALIGNMENT);
        encryptButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        decryptButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        selectButton.addActionListener(e -> chooseFile());
        encryptButton.addActionListener(e -> performEncrypt());
        decryptButton.addActionListener(e -> performDecrypt());

        PANEL.add(Box.createVerticalStrut(20));
        PANEL.add(selectButton);
        PANEL.add(Box.createVerticalStrut(15));
        PANEL.add(keyField);
        PANEL.add(Box.createVerticalStrut(15));
        PANEL.add(encryptButton);
        PANEL.add(Box.createVerticalStrut(15));
        PANEL.add(decryptButton);
    }

    private void performEncrypt() {
        if (!getExtension(selectedFile).equals(".txt")) {
            PtfLogger.error("Not a .txt file!", EncryptLog.ENCRYPT);
            JOptionPane.showMessageDialog(PANEL, "Not a .txt file selected!");
            return;
        }

        encryptAndSave(selectedFile, keyField.getText(), PANEL);
    }

    private void performDecrypt() {
        if (!getExtension(selectedFile).equals(".encrypmod")) {
            PtfLogger.error("Not a .encrypmod file!", EncryptLog.ENCRYPT);
            JOptionPane.showMessageDialog(PANEL, "Not a .encrypmod file selected!");
            return;
        }

        decryptAndSave(selectedFile, keyField.getText(), PANEL);
    }

    private void chooseFile() {
        JFileChooser chooser = getChooser();

        if (chooser.showOpenDialog(PANEL) == JFileChooser.APPROVE_OPTION)
            selectedFile = chooser.getSelectedFile();
    }

    private String getExtension(File file) {
        String name = file.getName();
        int lastDot = name.lastIndexOf('.');
        return lastDot == -1 ? "" : name.substring(lastDot).toLowerCase();
    }

    @Nonnull
    private static JFileChooser getChooser() {
        JFileChooser chooser = new JFileChooser();

        FileNameExtensionFilter txtFilter = new FileNameExtensionFilter("Text files - encrypt", "txt");
        FileNameExtensionFilter encFilter = new FileNameExtensionFilter("EncrypMod files - decrypt", "encrypmod");

        chooser.addChoosableFileFilter(txtFilter);
        chooser.addChoosableFileFilter(encFilter);

        chooser.setAcceptAllFileFilterUsed(false);
        chooser.setFileFilter(txtFilter);

        return chooser;
    }

    public void reset() {
        selectedFile = null;
        keyField.setText("");
    }

    @Override
    protected boolean doPreset() {
        return false;
    }
}
