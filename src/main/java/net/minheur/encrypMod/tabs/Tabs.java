package net.minheur.encrypMod.tabs;

import net.minheur.encrypMod.EncrypMod;
import net.minheur.potoflux.loader.mod.events.RegisterTabsEvent;
import net.minheur.potoflux.registry.RegistryList;
import net.minheur.potoflux.screen.tabs.Tab;
import net.minheur.potoflux.translations.Translations;
import net.minheur.potoflux.utils.SmartSupplier;
import net.minheur.potoflux.utils.ressourcelocation.ResourceLocation;
import net.minheur.encrypMod.tabs.all.EncryptingTab;

public class Tabs {
    private static final RegistryList<Tab> LIST = new RegistryList<>();

    // example tab
    public static final SmartSupplier<Tab> ENCRYPT_TAB = LIST.add(() -> new Tab(new ResourceLocation(EncrypMod.MOD_ID, "encryptab"), EncryptingTab.class));

    public static void register(RegisterTabsEvent event) {
        LIST.register(event.reg);
    }
}
