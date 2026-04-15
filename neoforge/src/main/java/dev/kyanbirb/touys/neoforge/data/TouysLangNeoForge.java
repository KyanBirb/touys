package dev.kyanbirb.touys.neoforge.data;

import dev.kyanbirb.touys.SableTouys;
import dev.kyanbirb.touys.data.TouysLang;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

public class TouysLangNeoForge extends LanguageProvider {
    public TouysLangNeoForge(PackOutput output) {
        super(output, SableTouys.MOD_ID, "en_us");
    }

    @Override
    protected void addTranslations() {
        TouysLang.provideLang(this::add);
    }
}
