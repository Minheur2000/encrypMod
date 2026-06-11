package net.minheur.encrypMod.tabs.all;

import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import net.minheur.encrypMod.encrypt.EncryptLog;
import net.minheur.potoflux.PotoFlux;
import net.minheur.potoflux.logger.PtfLogger;
import net.minheur.potoflux.screen.tabs.BaseVTab;
import net.minheur.potoflux.translations.Translations;
import net.minheur.potoflux.ui.UiUtils;

import java.io.File;

import static net.minheur.encrypMod.encrypt.EncryptUtils.*;

public class EncryptingTab extends BaseVTab<StackPane> {

    private File selectedFile;
    private TextField keyField;
    private TextField fileName;

    private Button selectButton;
    private Button encryptButton;
    private Button decryptButton;

    @Override
    protected void instantiate() {
        boxPreset();
        PANEL = new StackPane(vContent);
    }

    @Override
    protected void setPanel() {
        selectButton = new Button("Select file"); // todo
        encryptButton = new Button("Encrypt & Save"); // todo
        decryptButton = new Button("Decrypt & Save"); // todo

        setupButton();
    }

    @Override
    public String getName() {
        return Translations.get("encrypmod:tabs.encryptab.name");
    }

    private void setupButton() {
        keyField = new TextField();

        keyField.setMaxSize(250, 60);
        keyField.setPrefSize(250, 30);
        keyField.setPromptText("12-char key"); // todo

        fileName = new TextField();

        fileName.setMaxSize(250, 60);
        fileName.setPrefSize(250, 30);
        fileName.setEditable(false);
        fileName.setText("No file selected"); // todo

        selectButton.setOnAction(e -> chooseFile());
        encryptButton.setOnAction(e -> performEncrypt());
        decryptButton.setOnAction(e -> performDecrypt());

        vContent.getChildren().addAll(
                selectButton,
                fileName, keyField,
                encryptButton, decryptButton
        );
    }

    private void performEncrypt() {
        if (selectedFile == null) return;

        if (getExtension(selectedFile).equals(".encrypmod")) {
            PtfLogger.error("Can't encrypt a file that is already !", EncryptLog.ENCRYPT);
            UiUtils.showErrorPane("Can't encrypt a file that already is!"); // todo
            return;
        }

        encryptAndSave(selectedFile, keyField.getText());
    }

    private void performDecrypt() {
        if (selectedFile == null) return;

        if (!getExtension(selectedFile).equals(".encrypmod")) {
            PtfLogger.error("Not a .encrypmod file!", EncryptLog.DECRYPT);
            UiUtils.showErrorPane("Not a .encrypmod file selected!"); // todo
            return;
        }

        decryptAndSave(selectedFile, keyField.getText());
    }

    private void chooseFile() {
        FileChooser chooser = new FileChooser();

        File file = chooser.showOpenDialog(PotoFlux.app.getStage());
        if (file == null) return;

        selectedFile = file;
        fileName.setText("Selected file: " + selectedFile.getName()); // todo
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
