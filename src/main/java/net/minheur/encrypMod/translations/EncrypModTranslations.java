package net.minheur.encrypMod.translations;

import net.minheur.encrypMod.EncrypMod;
import net.minheur.potoflux.translations.AbstractTranslationsRegistry;

public class EncrypModTranslations extends AbstractTranslationsRegistry {
    public EncrypModTranslations() {
        super(EncrypMod.MOD_ID);
    }

    @Override
    protected void makeTranslation() {
        addEncrypTab("name")
                .en("EncrypMod");
    }

    // tabs helper
    private TranslationBuilder addEncrypTab(String... children) {
        return addTab("encryptab", children);
    }
}
