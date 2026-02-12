package net.minheur.encrypMod.translations;

import net.minheur.encrypMod.EncrypMod;
import net.minheur.potoflux.translations.AbstractTranslationsRegistry;

public class EncrypModTranslations extends AbstractTranslationsRegistry {
    public EncrypModTranslations() {
        super(EncrypMod.MOD_ID);
    }

    @Override
    protected void makeTranslation() {
        addYourTab("name")
                .en("Your tab name");
        addYourTab("title")
                .en("Your tab title");
    }

    // tabs helper
    private TranslationBuilder addYourTab(String... children) {
        return addTab("yourTab", children);
    }
}
