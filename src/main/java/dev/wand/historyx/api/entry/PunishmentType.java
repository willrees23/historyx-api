package dev.wand.historyx.api.entry;

/**
 * Represents the type of a punishment entry.
 *
 * @since 2.0.0
 */
public enum PunishmentType {
    BAN("Ban"),
    MUTE("Mute"),
    KICK("Kick"),
    WARNING("Warning"),
    NOTE("Note");

    private final String displayName;

    PunishmentType(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Returns the human-readable display name for this punishment type.
     *
     * @return The capitalized display name (e.g. "Ban", "Warning").
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Parses a string to a PunishmentType, case-insensitive.
     * Handles "warn" as an alias for {@link #WARNING}.
     *
     * @param type The string to parse.
     * @return The matching PunishmentType.
     * @throws IllegalArgumentException if the string does not match any type.
     */
    public static PunishmentType fromString(String type) {
        if (type == null) {
            throw new IllegalArgumentException("Punishment type cannot be null");
        }
        String normalized = type.trim().toUpperCase();
        if (normalized.equals("WARN")) {
            return WARNING;
        }
        return valueOf(normalized);
    }
}
