package dev.wand.historyx.api.entry;

import lombok.Getter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
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

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss (MMM dd)");

    /**
     * The ID of this entry.
     */
    private final long id;

    private final PunishmentType type;

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
    private final String removedByUUID;
    /**
     * The display name of the executor who removed this entry.
     * Nullable. If it is null, this entry probably has not been removed.
     */
    @Nullable
    private final String removedByName;
    /**
     * The reason for this punishment's removal. Arbitrary string.
     */
    @Nullable
    private final String removalReason;

    public CustomEntry(long id, PunishmentType type, String uuid, String ip, String reason, String executorUUID, String executorName, String removedByUUID, String removedByName, String removalReason, long dateStart, long dateEnd, String serverScope, String serverOrigin, boolean silent, boolean ipban, boolean active, long duration, EntrySource source) {
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

    public boolean wasRemoved() {
        return removedByName != null && removedByUUID != null && !removedByUUID.equalsIgnoreCase("null");
    }

    public String getDateEndFormatted() {
        return DATE_FORMATTER.format(Instant.ofEpochMilli(dateEnd).atZone(ZoneId.systemDefault()));
    }

    /**
     * Formats start date. Example: 05/12/2020 12:34:56 (Dec 05)
     *
     * @return A formatted version of the start date of this entry.
     */
    public String getDateStartFormatted() {
        return DATE_FORMATTER.format(Instant.ofEpochMilli(dateStart).atZone(ZoneId.systemDefault()));
    }

    /**
     * Checks whether this entry is actually active, not just relying on the "active" field.
     * Uses the current time to check against the entry's expiration date.
     * Works correctly for all entry sources (LiteBans, AdvancedBan, LibertyBans).
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
     * Creates a new Builder with the required fields.
     *
     * @param id     The unique ID of this entry.
     * @param type   The punishment type.
     * @param source The source plugin of this entry.
     * @return A new Builder instance.
     * @since 2.0.0
     */
    public static Builder builder(long id, PunishmentType type, EntrySource source) {
        return new Builder(id, type, source);
    }

    /**
     * Fluent builder for {@link CustomEntry}.
     * <p>
     * Required fields (id, type, source) are set via the constructor.
     * All other fields have sensible defaults (null for strings, false for booleans).
     * </p>
     *
     * @since 2.0.0
     */
    public static class Builder {
        // Required
        private final long id;
        private final PunishmentType type;
        private final EntrySource source;

        // Optional — strings default to null
        private String uuid = null;
        private String ip = null;
        private String reason = null;
        private String executorUUID = null;
        private String executorName = null;
        private String removedByUUID = null;
        private String removedByName = null;
        private String removalReason = null;
        private String serverScope = null;
        private String serverOrigin = null;

        // Optional — longs
        private long dateStart = 0;
        private long dateEnd = -1;
        private long duration = -1;

        // Optional — booleans default to false
        private boolean silent = false;
        private boolean ipban = false;
        private boolean active = false;

        private Builder(long id, PunishmentType type, EntrySource source) {
            this.id = id;
            this.type = type;
            this.source = source;
        }

        public Builder uuid(String uuid) { this.uuid = uuid; return this; }
        public Builder ip(String ip) { this.ip = ip; return this; }
        public Builder reason(String reason) { this.reason = reason; return this; }
        public Builder executorUUID(String executorUUID) { this.executorUUID = executorUUID; return this; }
        public Builder executorName(String executorName) { this.executorName = executorName; return this; }
        public Builder removedByUUID(String removedByUUID) { this.removedByUUID = removedByUUID; return this; }
        public Builder removedByName(String removedByName) { this.removedByName = removedByName; return this; }
        public Builder removalReason(String removalReason) { this.removalReason = removalReason; return this; }
        public Builder serverScope(String serverScope) { this.serverScope = serverScope; return this; }
        public Builder serverOrigin(String serverOrigin) { this.serverOrigin = serverOrigin; return this; }
        public Builder dateStart(long dateStart) { this.dateStart = dateStart; return this; }
        public Builder dateEnd(long dateEnd) { this.dateEnd = dateEnd; return this; }
        public Builder duration(long duration) { this.duration = duration; return this; }
        public Builder silent(boolean silent) { this.silent = silent; return this; }
        public Builder ipban(boolean ipban) { this.ipban = ipban; return this; }
        public Builder active(boolean active) { this.active = active; return this; }

        /**
         * Builds the {@link CustomEntry} instance.
         *
         * @return A new CustomEntry with the configured values.
         */
        public CustomEntry build() {
            return new CustomEntry(id, type, uuid, ip, reason, executorUUID, executorName,
                    removedByUUID, removedByName, removalReason, dateStart, dateEnd,
                    serverScope, serverOrigin, silent, ipban, active, duration, source);
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
        BANMANAGER,
        CUSTOM
    }
}
