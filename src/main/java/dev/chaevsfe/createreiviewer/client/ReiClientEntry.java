package dev.chaevsfe.createreiviewer.client;

import java.util.Collection;
import java.util.List;

import dev.chaevsfe.createreiviewer.compat.ReiSupport;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.common.plugins.REIPluginProvider;

public class ReiClientEntry implements REIPluginProvider<REIClientPlugin> {
    private final List<REIClientPlugin> plugins = ReiSupport.enabled() ? List.of(new CreateReiClientPlugin()) : List.of();

    @Override
    public Collection<REIClientPlugin> provide() {
        return plugins;
    }

    @Override
    public Class<REIClientPlugin> getPluginProviderClass() {
        return REIClientPlugin.class;
    }

    @Override
    public String getPluginProviderName() {
        return "CreateReiClientPlugin";
    }
}
