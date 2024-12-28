package dev.wand.historyx.api.entry;

import lombok.Getter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * Represents a punishment entry in a punishment history.
 * </p>
 *
 * @see CustomEntry.EntrySource EntrySource
 * @since 1.0.0
 */
@Getter
@ToString
public class CustomEntry {

    /**
     * The ID of this entry.
     */
    private final long id;

    private final String type;

    /**
     * The UUID of the player affected by this entry.
     * Nullable. If it is null, only the IP address is affected.
     */
    @Nullable
    private final String uuid;

    /**
     * The IP address of the player affected by this entry. This can be a wildcard specification, e.g. "127.0.0.%"
     * Nullable. If it is null, only the UUID is affected.
     */
    @Nullable
    private final String ip;
    /**
     * The reason for this punishment. Arbitrary string.
     */
    private final String reason;

    /**
     * The UUID of the executor (creator) of this entry.
     */
    @Nullable
    private final String executorUUID;
    /**
     * The display name of the executor (creator) of this entry.
     */
    @Nullable
    private final String executorName;
    /**
     * Date of entry creation (milliseconds unixtime)
     */
    private final long dateStart;
    /**
     * Date of entry expiration (milliseconds unixtime)
     * If dateEnd <= 0, this entry will never expire (use Entry#isPermanent() to check that).
     */
    private final long dateEnd;
    /**
     * The server scope of this entry. (The network, server, or subserver which this entry takes effect on)
     */
    private final String serverScope;
    /**
     * The server origin of this entry. (The server or subserver on which this entry was created)
     */
    private final String serverOrigin;
    /**
     * Whether this entry was created using either the "-s" or "-S" command flags.
     * True if silent, false if not silent.
     */
    private final boolean silent;
    /**
     * True if this entry is an IP ban or an IP mute, false if this entry only affects a UUID.
     */
    private final boolean ipban;
    /**
     * True if this entry is active, has not been deactivated or removed and has not expired yet. False otherwise.
     * Unreliable for LiteBans entries, use Entry#isActuallyActive() instead.
     * Other plugins, use of this field is recommended.
     */
    private final boolean active;
    private final long duration;
    private final EntrySource source;
    /**
     * The UUID of the executor who removed this entry.
     * Nullable. If it is null, this entry probably has not been removed.
     */
    @Nullable
    private String removedByUUID;
    /**
     * The display name of the executor who removed this entry.
     * Nullable. If it is null, this entry probably has not been removed.
     */
    @Nullable
    private String removedByName;
    /**
     * The reason for this punishment's removal. Arbitrary string.
     */
    @Nullable
    private String removalReason;

    private boolean abExpired;

    public CustomEntry(long id, String type, String uuid, String ip, String reason, String executorUUID, String executorName, String removedByUUID, String removedByName, String removalReason, long dateStart, long dateEnd, String serverScope, String serverOrigin, boolean silent, boolean ipban, boolean active, long duration, EntrySource source) {
        this.id = id;
        this.type = type;
        this.uuid = uuid;
        this.ip = ip;
        this.reason = reason;
        this.executorUUID = executorUUID;
        this.executorName = executorName;
        this.removedByUUID = removedByUUID;
        this.removedByName = removedByName;
        this.removalReason = removalReason;
        this.dateStart = dateStart;
        this.dateEnd = dateEnd;
        this.serverScope = serverScope;
        this.serverOrigin = serverOrigin;
        this.silent = silent;
        this.ipban = ipban;
        this.active = active;
        this.duration = duration;
        this.source = source;
    }

    public CustomEntry(long id, String type, String uuid, String ip, String reason, String executorUUID, String executorName, String removedByUUID, String removedByName, String removalReason, long dateStart, long dateEnd, String serverScope, String serverOrigin, boolean silent, boolean ipban, boolean active, long duration, EntrySource source, boolean abExpired) {
        this.id = id;
        this.type = type;
        this.uuid = uuid;
        this.ip = ip;
        this.reason = reason;
        this.executorUUID = executorUUID;
        this.executorName = executorName;
        this.removedByUUID = removedByUUID;
        this.removedByName = removedByName;
        this.removalReason = removalReason;
        this.dateStart = dateStart;
        this.dateEnd = dateEnd;
        this.serverScope = serverScope;
        this.serverOrigin = serverOrigin;
        this.silent = silent;
        this.ipban = ipban;
        this.active = active;
        this.duration = duration;
        this.source = source;
        this.abExpired = abExpired;
    }

    public boolean wasRemoved() {
        // removedByName or removedByUUID is null == expired
        // removedByUUId can also be a string "null" which also means expired
        // if source is advancedbans, we can trust the active field

        // TLDR; litebans is weird
        if (source == EntrySource.ADVANCEDBANS) {
            return !active && (!isAbExpired());
        }
        return removedByName != null && removedByUUID != null && !removedByUUID.equalsIgnoreCase("null");
    }

    public String getDateEndFormatted() {
        return new SimpleDateFormat("dd/MM/yyyy HH:mm:ss (MMM dd)").format(new Date(dateEnd));
    }

    /**
     * Formats start date. Example: 05/12/2020 12:34:56 (Dec 05)
     *
     * @return A formatted version of the start date of this entry.
     */
    public String getDateStartFormatted() {
        return new SimpleDateFormat("dd/MM/yyyy HH:mm:ss (MMM dd)").format(new Date(dateStart));
    }

    /**
     * Checks whether this entry is actually active, not just relying on the "active" field.
     * Uses the current time to check against the entry's expiration date.
     * ONLY APPLICABLE TO LITEBANS ENTRIES.
     *
     * @return True if this entry is actually active, false otherwise.
     */
    public boolean isActuallyActive() {
        return active && (dateEnd <= 0 || dateEnd > System.currentTimeMillis());
    }

    public boolean isPermanent() {
        return dateEnd <= 0;
    }

    public String getDurationString() {
        //convert millis into days, hours, minutes, seconds
        long millis = duration;
        if (millis <= 0) {
            return "Permanent";
        }
        return getString(millis);
    }

    public String getRemainingStringDigital() {
        long millis = dateEnd - System.currentTimeMillis();
        String hms = String.format("%02d:%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toDays(millis),
                TimeUnit.MILLISECONDS.toHours(millis) % TimeUnit.DAYS.toHours(1),
                TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1));

        return hms;
    }

    public String getRemainingString() {
        long millis = dateEnd - System.currentTimeMillis();
        return getString(millis);
    }

    public long getRemainingMillis() {
        return dateEnd - System.currentTimeMillis();
    }

    @NotNull
    private String getString(long millis) {
        long days = millis / 86400000;
        millis = millis % 86400000;
        long hours = millis / 3600000;
        millis = millis % 3600000;
        long minutes = millis / 60000;
        millis = millis % 60000;
        long seconds = millis / 1000;
        millis = millis % 1000;

        if (days > 0) {
            if (hours > 0) {
                return days + "d " + hours + "h";
            } else {
                return days + "d";
            }
        } else if (hours > 0) {
            if (minutes > 0) {
                return hours + "h " + minutes + "m";
            } else {
                return hours + "h";
            }
        } else if (minutes > 0) {
            if (seconds > 0) {
                return minutes + "m " + seconds + "s";
            } else {
                return minutes + "m";
            }
        } else {
            return seconds + "s";
        }
    }

    /**
     * Represents the source of a punishment entry.
     * <ul>
     *     <li>LITEBANS: LiteBans plugin</li>
     *     <li>ADVANCEDBANS: AdvancedBans plugin</li>
     *     <li>LIBERTYBANS: LibertyBans plugin</li>
     *     <li>CUSTOM: Custom implementation.</li>
     * </ul>
     */
    public enum EntrySource {
        LITEBANS,
        ADVANCEDBANS,
        LIBERTYBANS,
        CUSTOM
    }
}
