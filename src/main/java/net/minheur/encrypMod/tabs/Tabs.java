package net.minheur.encrypMod.tabs;

import net.minheur.encrypMod.EncrypMod;
import net.minheur.potoflux.loader.mod.events.RegisterTabsEvent;
import net.minheur.potoflux.registry.RegistryList;
import net.minheur.potoflux.screen.tabs.Tab;
import net.minheur.potoflux.translations.Translations;
import net.minheur.potoflux.utils.ressourcelocation.ResourceLocation;
import net.minheur.encrypMod.tabs.all.EncryptingTab;

public class Tabs {
    private final RegistryList<Tab> LIST = new RegistryList<>();
    private static boolean hasGenerated = false;

    public static Tabs INSTANCE;

    public Tabs() {
        if (hasGenerated) throw new IllegalStateException("Can't create the registry 2 times !");
        hasGenerated = true;
    }

    // example tab
    public final Tab ENCRYPT_TAB = LIST.add(new Tab(new ResourceLocation(EncrypMod.MOD_ID, "your_tab_id"), Translations.get("yourmodid:tabs.yourTab.name"), EncryptingTab.class));

    public static void register(RegisterTabsEvent event) {
        INSTANCE = new Tabs();
        INSTANCE.LIST.register(event.reg);
    }
}
