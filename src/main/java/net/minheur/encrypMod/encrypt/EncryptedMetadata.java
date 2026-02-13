package net.minheur.encrypMod.encrypt;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public record EncryptedMetadata(
        byte[] encryptedData,
        String originalName,
        String originalExtension
) {
    public static EncryptedMetadata readMetadata(byte[] fileBytes) throws IOException {
        DataInputStream in = new DataInputStream(new ByteArrayInputStream(fileBytes));

        byte[] magic = new byte[4];
        in.readFully(magic);

        if (!new String(magic, StandardCharsets.US_ASCII).equals("EMOD"))
            throw new IOException("Invalid EncrypMod file (bad magic)");

        int version = in.readUnsignedByte();
        if (version != 1)
            throw new IOException("Unsupported EncrypMod version: " + version);

        int metaLength = in.readUnsignedShort();
        byte[] metaBytes = new byte[metaLength];
        in.readFully(metaBytes);

        String metaJson = new String(metaBytes, StandardCharsets.UTF_8);

        // parse
        String originalName = extract(metaJson, "originalName");
        String originalExt = extract(metaJson, "originalExtension");

        byte[] encryptedData = in.readAllBytes();

        return new EncryptedMetadata(encryptedData, originalName, originalExt);
    }

    private static String extract(String json, String key) {
        JsonObject meta = JsonParser.parseString(json).getAsJsonObject();
        return meta.get(key).getAsString();
    }
}
