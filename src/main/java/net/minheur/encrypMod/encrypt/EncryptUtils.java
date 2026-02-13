package net.minheur.encrypMod.encrypt;

import net.minheur.encrypMod.tabs.Tabs;
import net.minheur.encrypMod.tabs.all.EncryptingTab;
import net.minheur.potoflux.PotoFlux;
import net.minheur.potoflux.logger.PtfLogger;

import javax.annotation.Nonnull;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
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
            byte[] header = buildCryptedHeader(selectedFile);

            JFileChooser saveChooser = new JFileChooser();
            saveChooser.setSelectedFile(new File("output.encrypmod"));
            saveChooser.setFileFilter(new FileNameExtensionFilter("EncrypMod files", "encrypmod"));
            saveChooser.setAcceptAllFileFilterUsed(false);

            if (saveChooser.showSaveDialog(panel) == JFileChooser.APPROVE_OPTION) {

                File output = checkOut(saveChooser.getSelectedFile(), ".encrypmod");

                ByteArrayOutputStream out = new ByteArrayOutputStream();
                out.write(header);
                out.write(encrypted);

                Files.write(output.toPath(), out.toByteArray());
                resetEnc();

            }
        } catch (Exception e) {
            e.printStackTrace();
            PtfLogger.error("Could not encrypt / write file !", EncryptLog.ENCRYPT);
            JOptionPane.showMessageDialog(panel, "Encryption failed: " + e.getMessage());
        }

    }

    @Nonnull
    private static File checkOut(File output, String suffix) {
        String outName = output.getName().toLowerCase();
        suffix = suffix.toLowerCase();

        if (!outName.endsWith(suffix))
            output = new File(output.getAbsolutePath() + suffix);
        return output;
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
            EncryptedMetadata data = EncryptedMetadata.readMetadata(fileBytes);

            byte[] decrypted = cipherCrypting(data.encryptedData(), key, Cipher.DECRYPT_MODE);

            JFileChooser saveChooser = new JFileChooser();
            saveChooser.setSelectedFile(new File(data.originalName()));

            if (saveChooser.showSaveDialog(panel) != JFileChooser.APPROVE_OPTION) return;

            File output = checkOut(saveChooser.getSelectedFile(), data.originalExtension());

            Files.write(output.toPath(), decrypted);
            resetEnc();

        } catch (Exception e) {
            e.printStackTrace();
            PtfLogger.error("Could not decrypt / write file !", EncryptLog.DECRYPT);
            JOptionPane.showMessageDialog(panel, "Decryption failed: " + e.getMessage());
        }

    }

    private static byte[] buildCryptedHeader(File originFile) throws IOException {
        String jsonMeta = """
                {
                "originalName": "%s",
                "originalExtension": "%s",
                "algorithm":"AES",
                "version":1
                }
                """
                .formatted(
                        originFile.getName(),
                        getExtension(originFile)
                );

        byte[] metaBytes = jsonMeta.getBytes(StandardCharsets.UTF_8);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        DataOutputStream data = new DataOutputStream(out);

        data.write("EMOD".getBytes(StandardCharsets.UTF_8)); // magic
        data.writeByte(1); // version
        data.writeShort(metaBytes.length);
        data.write(metaBytes);

        return out.toByteArray();
    }

    private static void resetEnc() {
        EncryptingTab t = ((EncryptingTab) PotoFlux.app.getTabMap().get(Tabs.INSTANCE.ENCRYPT_TAB));
        t.reset();
    }
    public static String getExtension(File file) {
        String name = file.getName();
        int lastDot = name.lastIndexOf('.');
        return lastDot == -1 ? "" : name.substring(lastDot).toLowerCase();
    }
}
