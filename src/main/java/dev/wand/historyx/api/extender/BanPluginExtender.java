package dev.wand.historyx.api.extender;

import dev.wand.historyx.api.entry.CustomEntry;

import java.util.List;
import java.util.UUID;

public interface BanPluginExtender<T> {

    List<CustomEntry> getHistory(UUID uuid);

    CustomEntry createEntry(T punishment, CustomEntry.EntrySource source);
}
