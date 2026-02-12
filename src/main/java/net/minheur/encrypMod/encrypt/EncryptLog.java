package net.minheur.encrypMod.encrypt;

import net.minheur.potoflux.logger.ILogCategory;

public enum EncryptLog implements ILogCategory {
    ENCRYPT("encrypt"),
    DECRYPT("decrypt");

    private final String code;

    EncryptLog(String code) {
        this.code = code;
    }

    @Override
    public String code() {
        return code;
    }
}
