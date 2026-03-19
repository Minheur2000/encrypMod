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
    private JTextField fileName;

    private JButton selectButton;
    private JButton encryptButton;
    private JButton decryptButton;

    @Override
    protected void setPanel() {
        PANEL.setLayout(new BoxLayout(PANEL, BoxLayout.Y_AXIS));

        selectButton = new JButton("Select file");
        encryptButton = new JButton("Encrypt & Save");
        decryptButton = new JButton("Decrypt & Save");

        setupButton();
    }

    private void setupButton() {
        keyField = new JTextField();

        keyField.setBorder(BorderFactory.createTitledBorder("12-char key"));
        keyField.setMaximumSize(new Dimension(250, 60));
        keyField.setPreferredSize(new Dimension(250, 30));

        fileName = new JTextField();

        fileName.setBorder(BorderFactory.createTitledBorder("Selected file"));
        fileName.setMaximumSize(new Dimension(250, 60));
        fileName.setPreferredSize(new Dimension(250, 30));
        fileName.setEditable(false);
        fileName.setText("None selected");

        selectButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        keyField.setAlignmentX(Component.CENTER_ALIGNMENT);
        fileName.setAlignmentX(Component.CENTER_ALIGNMENT);
        encryptButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        decryptButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        selectButton.addActionListener(e -> chooseFile());
        encryptButton.addActionListener(e -> performEncrypt());
        decryptButton.addActionListener(e -> performDecrypt());

        PANEL.add(Box.createVerticalStrut(20));
        PANEL.add(selectButton);
        PANEL.add(Box.createVerticalStrut(5));
        PANEL.add(fileName);
        PANEL.add(Box.createVerticalStrut(15));
        PANEL.add(keyField);
        PANEL.add(Box.createVerticalStrut(15));
        PANEL.add(encryptButton);
        PANEL.add(Box.createVerticalStrut(15));
        PANEL.add(decryptButton);
    }

    private void performEncrypt() {
        if (getExtension(selectedFile).equals(".encrypmod")) {
            PtfLogger.error("Can't encrypt a file that is already !", EncryptLog.ENCRYPT);
            JOptionPane.showMessageDialog(PANEL, "Can't encrypt a file that already is!");
            return;
        }

        encryptAndSave(selectedFile, keyField.getText(), PANEL);
    }

    private void performDecrypt() {
        if (!getExtension(selectedFile).equals(".encrypmod")) {
            PtfLogger.error("Not a .encrypmod file!", EncryptLog.DECRYPT);
            JOptionPane.showMessageDialog(PANEL, "Not a .encrypmod file selected!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        decryptAndSave(selectedFile, keyField.getText(), PANEL);
    }

    private void chooseFile() {
        JFileChooser chooser = new JFileChooser();

        if (chooser.showOpenDialog(PANEL) == JFileChooser.APPROVE_OPTION) {
            selectedFile = chooser.getSelectedFile();
            fileName.setText(selectedFile.getName());
        }
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
