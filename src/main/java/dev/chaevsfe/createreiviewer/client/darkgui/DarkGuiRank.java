package dev.chaevsfe.createreiviewer.client.darkgui;

import java.util.List;

final class DarkGuiRank {
    private final List<String> bottomToTop;
    private final String ours;
    private final String create;

    DarkGuiRank(List<String> bottomToTop, String ours, String create) {
        this.bottomToTop = List.copyOf(bottomToTop);
        this.ours = ours;
        this.create = create;
    }

    boolean higherPackWins(String provider) {
        if (provider == null || provider.equals(ours) || provider.equals(create)) {
            return false;
        }
        int theirs = bottomToTop.indexOf(provider);
        int mine = bottomToTop.indexOf(ours);
        return theirs >= 0 && mine >= 0 && theirs > mine;
    }

    int indexOfOurs() {
        return bottomToTop.indexOf(ours);
    }

    int size() {
        return bottomToTop.size();
    }

    String top() {
        return bottomToTop.isEmpty() ? "" : bottomToTop.get(bottomToTop.size() - 1);
    }
}
