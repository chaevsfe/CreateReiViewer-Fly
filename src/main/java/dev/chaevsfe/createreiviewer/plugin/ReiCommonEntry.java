package dev.chaevsfe.createreiviewer.plugin;

import java.util.Collection;
import java.util.List;

import dev.chaevsfe.createreiviewer.compat.ReiSupport;
import me.shedaniel.rei.api.common.plugins.REICommonPlugin;
import me.shedaniel.rei.api.common.plugins.REIPluginProvider;

public class ReiCommonEntry implements REIPluginProvider<REICommonPlugin> {
    private final List<REICommonPlugin> plugins = ReiSupport.enabled() ? List.of(new CreateReiCommonPlugin()) : List.of();

    @Override
    public Collection<REICommonPlugin> provide() {
        return plugins;
    }

    @Override
    public Class<REICommonPlugin> getPluginProviderClass() {
        return REICommonPlugin.class;
    }

    @Override
    public String getPluginProviderName() {
        return "CreateReiCommonPlugin";
    }
}
