package dev.wand.historyx.api.extender;

import dev.wand.historyx.api.entry.CustomEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public interface BanPluginExtender<T> {

    List<CustomEntry> getHistory(UUID uuid);

    /**
     * Returns all punishments issued BY the given staff member.
     * Returns an empty list if the extender does not support staff history.
     *
     * @param staffUUID The UUID of the staff member.
     * @return Punishments issued by this staff member.
     * @since 2.0.0
     */
    default List<CustomEntry> getStaffHistory(UUID staffUUID) {
        return new ArrayList<>();
    }

    CustomEntry createEntry(T punishment, CustomEntry.EntrySource source);
}
