package net.minheur.encrypMod.encrypt;

import net.minheur.encrypMod.tabs.Tabs;
import net.minheur.encrypMod.tabs.all.EncryptingTab;
import net.minheur.potoflux.PotoFlux;
import net.minheur.potoflux.logger.PtfLogger;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.MessageDigest;

public class EncryptUtils {

    public static boolean isValidKey(String key) {
        return key.matches("[a-zA-Z0-9]{12}");
    }

    public static byte[] cipherCrypting(byte[] data, String key, int cipherMod) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] keyBytes = digest.digest(key.getBytes(StandardCharsets.UTF_8));

        SecretKeySpec secretKey = new SecretKeySpec(keyBytes, "AES");

        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(cipherMod, secretKey);

        return cipher.doFinal(data);
    }

    public static void encryptAndSave(File selectedFile, String key, JPanel panel) {
        if (selectedFile == null) {
            PtfLogger.error("Could not encrypt null file!", EncryptLog.ENCRYPT);
            JOptionPane.showMessageDialog(panel, "No file selected!");
            return;
        }

        if (!isValidKey(key)) {
            PtfLogger.error("Could not encrypt with invalid key!", EncryptLog.ENCRYPT);
            JOptionPane.showMessageDialog(panel, "Key must have 12 alphanumeric characters!");
            return;
        }


        try {
            byte[] fileBytes = Files.readAllBytes(selectedFile.toPath());
            byte[] encrypted = cipherCrypting(fileBytes, key, Cipher.ENCRYPT_MODE);

            JFileChooser saveChooser = new JFileChooser();
            saveChooser.setSelectedFile(new File("output.encrypmod"));
            saveChooser.setFileFilter(new FileNameExtensionFilter("EncrypMod files", "encrypmod"));

            if (saveChooser.showSaveDialog(panel) == JFileChooser.APPROVE_OPTION) {

                File output = saveChooser.getSelectedFile();

                if (!output.getName().endsWith(".encrypmod"))
                    output = new File(output.getAbsolutePath() + ".encrypmod");

                Files.write(output.toPath(), encrypted);

                resetEnc();

            }
        } catch (Exception e) {
            e.printStackTrace();
            PtfLogger.error("Could not encrypt / write file !", EncryptLog.ENCRYPT);
            JOptionPane.showMessageDialog(panel, "Encryption failed: " + e.getMessage());
        }

    }

    public static void decryptAndSave(File selectedFile, String key, JPanel panel) {
        if (selectedFile == null) {
            PtfLogger.error("Could not decrypt null file!", EncryptLog.DECRYPT);
            JOptionPane.showMessageDialog(panel, "No file selected!");
            return;
        }

        if (!isValidKey(key)) {
            PtfLogger.error("Could not decrypt with invalid key!", EncryptLog.DECRYPT);
            JOptionPane.showMessageDialog(panel, "Key must have 12 alphanumeric characters!");
            return;
        }


        try {
            byte[] fileBytes = Files.readAllBytes(selectedFile.toPath());
            byte[] encrypted = cipherCrypting(fileBytes, key, Cipher.DECRYPT_MODE);

            JFileChooser saveChooser = new JFileChooser();
            saveChooser.setSelectedFile(new File("output.txt"));
            saveChooser.setFileFilter(new FileNameExtensionFilter("Text files", "txt"));

            if (saveChooser.showSaveDialog(panel) == JFileChooser.APPROVE_OPTION) {

                File output = saveChooser.getSelectedFile();

                if (!output.getName().endsWith(".txt"))
                    output = new File(output.getAbsolutePath() + ".txt");

                Files.write(output.toPath(), encrypted);

                resetEnc();

            }
        } catch (Exception e) {
            e.printStackTrace();
            PtfLogger.error("Could not decrypt / write file !", EncryptLog.DECRYPT);
            JOptionPane.showMessageDialog(panel, "Decryption failed: " + e.getMessage());
        }

    }

    private static void resetEnc() {
        EncryptingTab t = ((EncryptingTab) PotoFlux.app.getTabMap().get(Tabs.INSTANCE.ENCRYPT_TAB));
        t.reset();
    }
}
