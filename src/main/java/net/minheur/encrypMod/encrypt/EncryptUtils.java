package net.minheur.encrypMod.encrypt;

import javafx.stage.FileChooser;
import net.minheur.encrypMod.tabs.Tabs;
import net.minheur.encrypMod.tabs.all.EncryptingTab;
import net.minheur.potoflux.PotoFlux;
import net.minheur.potoflux.logger.PtfLogger;

import net.minheur.potoflux.ui.UiUtils;
import org.jetbrains.annotations.NotNull;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
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

    public static void encryptAndSave(File selectedFile, String key) {
        if (selectedFile == null) {
            PtfLogger.error("Could not encrypt null file!", EncryptLog.ENCRYPT);
            UiUtils.showErrorPane("No file selected !"); // todo
            return;
        }

        if (!isValidKey(key)) {
            PtfLogger.error("Could not encrypt with invalid key!", EncryptLog.ENCRYPT);
            UiUtils.showErrorPane("Key must have 12 alphanumeric characters!"); // todo
            return;
        }


        try {
            byte[] fileBytes = Files.readAllBytes(selectedFile.toPath());
            byte[] encrypted = cipherCrypting(fileBytes, key, Cipher.ENCRYPT_MODE);
            byte[] header = buildCryptedHeader(selectedFile);

            FileChooser saveChooser = new FileChooser();
            saveChooser.setInitialFileName("output.encrypmod");
            saveChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter(
                            "EncrypMod files (*.encrypmod)",
                            "*.encrypmod"
                    )
            );

            File file = saveChooser.showSaveDialog(PotoFlux.app.getStage());
            if (file == null) return;

            File output = checkOut(file, ".encrypmod");

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            out.write(header);
            out.write(encrypted);

            Files.write(output.toPath(), out.toByteArray());
            resetEnc();

        } catch (Exception e) {
            e.printStackTrace();
            PtfLogger.error("Could not encrypt / write file !", EncryptLog.ENCRYPT);
            UiUtils.showErrorPane("Encryption failed: " + e.getMessage()); // todo
        }

    }

    @NotNull
    private static File checkOut(File output, String suffix) {
        String outName = output.getName().toLowerCase();
        suffix = suffix.toLowerCase();

        if (!outName.endsWith(suffix))
            output = new File(output.getAbsolutePath() + suffix);
        return output;
    }

    public static void decryptAndSave(File selectedFile, String key) {
        if (selectedFile == null) {
            PtfLogger.error("Could not decrypt null file!", EncryptLog.DECRYPT);
            UiUtils.showErrorPane("No file selected!"); // todo
            return;
        }

        if (!isValidKey(key)) {
            PtfLogger.error("Could not decrypt with invalid key!", EncryptLog.DECRYPT);
            UiUtils.showErrorPane("Key must have 12 alphanumeric characters!"); // todo
            return;
        }


        try {
            byte[] fileBytes = Files.readAllBytes(selectedFile.toPath());
            EncryptedMetadata data = EncryptedMetadata.readMetadata(fileBytes);

            byte[] decrypted = cipherCrypting(data.encryptedData(), key, Cipher.DECRYPT_MODE);

            FileChooser saveChooser = new FileChooser();
            saveChooser.setInitialFileName(data.originalName());

            File file = saveChooser.showSaveDialog(PotoFlux.app.getStage());
            if (file == null) return;

            File output = checkOut(file, data.originalExtension());

            Files.write(output.toPath(), decrypted);
            resetEnc();

        } catch (Exception e) {
            e.printStackTrace();
            PtfLogger.error("Could not decrypt / write file !", EncryptLog.DECRYPT);
            UiUtils.showErrorPane("Decryption failed: " + e.getMessage()); // todo
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
        EncryptingTab t = ((EncryptingTab) PotoFlux.app.getTabMap().get(Tabs.ENCRYPT_TAB.get()));
        t.reset();
    }
    public static String getExtension(File file) {
        String name = file.getName();
        int lastDot = name.lastIndexOf('.');
        return lastDot == -1 ? "" : name.substring(lastDot).toLowerCase();
    }
}
