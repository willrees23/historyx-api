package dev.wand.historyx.api.entry;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

class CustomEntryTest {

    private CustomEntry activeTemporary() {
        long now = System.currentTimeMillis();
        return CustomEntry.builder(1, "ban", CustomEntry.EntrySource.LITEBANS)
                .active(true)
                .dateStart(now - 60_000)
                .dateEnd(now + 60_000)
                .duration(120_000)
                .build();
    }

    private CustomEntry activePermanent() {
        return CustomEntry.builder(2, "ban", CustomEntry.EntrySource.LITEBANS)
                .active(true)
                .dateStart(System.currentTimeMillis() - 60_000)
                .dateEnd(-1)
                .duration(-1)
                .build();
    }

    private CustomEntry expiredEntry() {
        long now = System.currentTimeMillis();
        return CustomEntry.builder(3, "mute", CustomEntry.EntrySource.LITEBANS)
                .active(true)
                .dateStart(now - 120_000)
                .dateEnd(now - 60_000)
                .duration(60_000)
                .build();
    }

    // --- isActuallyActive ---

    @Test
    @DisplayName("isActuallyActive: active with future dateEnd returns true")
    void isActuallyActive_activeWithFutureEnd() {
        assertTrue(activeTemporary().isActuallyActive());
    }

    @Test
    @DisplayName("isActuallyActive: active with past dateEnd returns false")
    void isActuallyActive_activeWithPastEnd() {
        assertFalse(expiredEntry().isActuallyActive());
    }

    @Test
    @DisplayName("isActuallyActive: active with permanent dateEnd returns true")
    void isActuallyActive_permanent() {
        assertTrue(activePermanent().isActuallyActive());
    }

    @Test
    @DisplayName("isActuallyActive: inactive entry with future dateEnd returns false")
    void isActuallyActive_inactiveWithFutureEnd() {
        CustomEntry entry = CustomEntry.builder(4, "ban", CustomEntry.EntrySource.LITEBANS)
                .active(false)
                .dateEnd(System.currentTimeMillis() + 60_000)
                .build();
        assertFalse(entry.isActuallyActive());
    }

    // --- isPermanent ---

    @Test
    @DisplayName("isPermanent: dateEnd <= 0 returns true")
    void isPermanent_negativeEnd() {
        assertTrue(activePermanent().isPermanent());
    }

    @Test
    @DisplayName("isPermanent: dateEnd == 0 returns true")
    void isPermanent_zeroEnd() {
        CustomEntry entry = CustomEntry.builder(5, "ban", CustomEntry.EntrySource.LITEBANS)
                .dateEnd(0)
                .build();
        assertTrue(entry.isPermanent());
    }

    @Test
    @DisplayName("isPermanent: dateEnd > 0 returns false")
    void isPermanent_positiveEnd() {
        assertFalse(activeTemporary().isPermanent());
    }

    // --- wasRemoved ---

    @Test
    @DisplayName("wasRemoved: AdvancedBan inactive and not expired means removed")
    void wasRemoved_advancedBan_removed() {
        CustomEntry entry = CustomEntry.builder(6, "ban", CustomEntry.EntrySource.ADVANCEDBANS)
                .active(false)
                .abExpired(false)
                .build();
        assertTrue(entry.wasRemoved());
    }

    @Test
    @DisplayName("wasRemoved: AdvancedBan inactive and expired means not removed")
    void wasRemoved_advancedBan_expired() {
        CustomEntry entry = CustomEntry.builder(7, "ban", CustomEntry.EntrySource.ADVANCEDBANS)
                .active(false)
                .abExpired(true)
                .build();
        assertFalse(entry.wasRemoved());
    }

    @Test
    @DisplayName("wasRemoved: AdvancedBan active means not removed")
    void wasRemoved_advancedBan_active() {
        CustomEntry entry = CustomEntry.builder(8, "ban", CustomEntry.EntrySource.ADVANCEDBANS)
                .active(true)
                .abExpired(false)
                .build();
        assertFalse(entry.wasRemoved());
    }

    @Test
    @DisplayName("wasRemoved: LiteBans with real removedBy values means removed")
    void wasRemoved_litebans_removed() {
        CustomEntry entry = CustomEntry.builder(9, "ban", CustomEntry.EntrySource.LITEBANS)
                .removedByUUID("abc-123")
                .removedByName("Admin")
                .build();
        assertTrue(entry.wasRemoved());
    }

    @Test
    @DisplayName("wasRemoved: LiteBans with removedByUUID as string 'null' means not removed")
    void wasRemoved_litebans_nullString() {
        CustomEntry entry = CustomEntry.builder(10, "ban", CustomEntry.EntrySource.LITEBANS)
                .removedByUUID("null")
                .removedByName("Console")
                .build();
        assertFalse(entry.wasRemoved());
    }

    @Test
    @DisplayName("wasRemoved: LiteBans with null removedByName means not removed")
    void wasRemoved_litebans_nullName() {
        CustomEntry entry = CustomEntry.builder(11, "ban", CustomEntry.EntrySource.LITEBANS)
                .removedByUUID("abc-123")
                .removedByName(null)
                .build();
        assertFalse(entry.wasRemoved());
    }

    // --- getDurationString ---

    @Test
    @DisplayName("getDurationString: permanent returns 'Permanent'")
    void getDurationString_permanent() {
        assertEquals("Permanent", activePermanent().getDurationString());
    }

    @Test
    @DisplayName("getDurationString: days and hours")
    void getDurationString_daysAndHours() {
        CustomEntry entry = CustomEntry.builder(12, "ban", CustomEntry.EntrySource.LITEBANS)
                .duration(90_000_000L) // 1d 1h
                .build();
        assertEquals("1d 1h", entry.getDurationString());
    }

    @Test
    @DisplayName("getDurationString: hours and minutes")
    void getDurationString_hoursAndMinutes() {
        CustomEntry entry = CustomEntry.builder(13, "ban", CustomEntry.EntrySource.LITEBANS)
                .duration(3_660_000L) // 1h 1m
                .build();
        assertEquals("1h 1m", entry.getDurationString());
    }

    @Test
    @DisplayName("getDurationString: minutes and seconds")
    void getDurationString_minutesAndSeconds() {
        CustomEntry entry = CustomEntry.builder(14, "ban", CustomEntry.EntrySource.LITEBANS)
                .duration(61_000L) // 1m 1s
                .build();
        assertEquals("1m 1s", entry.getDurationString());
    }

    @Test
    @DisplayName("getDurationString: seconds only")
    void getDurationString_secondsOnly() {
        CustomEntry entry = CustomEntry.builder(15, "ban", CustomEntry.EntrySource.LITEBANS)
                .duration(5_000L)
                .build();
        assertEquals("5s", entry.getDurationString());
    }

    // --- getDateStartFormatted / getDateEndFormatted ---

    @Test
    @DisplayName("getDateStartFormatted: produces expected format pattern")
    void getDateStartFormatted_format() {
        CustomEntry entry = CustomEntry.builder(16, "ban", CustomEntry.EntrySource.LITEBANS)
                .dateStart(1607167200000L) // 05 Dec 2020 12:00:00 UTC (varies by timezone)
                .build();
        String formatted = entry.getDateStartFormatted();
        // Verify format pattern: dd/MM/yyyy HH:mm:ss (MMM dd)
        assertTrue(formatted.matches("\\d{2}/\\d{2}/\\d{4} \\d{2}:\\d{2}:\\d{2} \\(.+\\)"),
                "Formatted date should match pattern, got: " + formatted);
    }

    // --- getRemainingStringDigital ---

    @Test
    @DisplayName("getRemainingStringDigital: formats as DD:HH:MM:SS")
    void getRemainingStringDigital_format() {
        long now = System.currentTimeMillis();
        CustomEntry entry = CustomEntry.builder(17, "ban", CustomEntry.EntrySource.LITEBANS)
                .dateEnd(now + 90_061_000L) // ~1d 1h 1m 1s
                .build();
        String digital = entry.getRemainingStringDigital();
        assertTrue(digital.matches("\\d{2}:\\d{2}:\\d{2}:\\d{2}"),
                "Should match DD:HH:MM:SS format, got: " + digital);
    }

    // --- getRemainingString ---

    @Test
    @DisplayName("getRemainingString: entry with future dateEnd returns non-empty string")
    void getRemainingString_futureEnd() {
        long now = System.currentTimeMillis();
        CustomEntry entry = CustomEntry.builder(18, "ban", CustomEntry.EntrySource.LITEBANS)
                .dateEnd(now + 86_400_000L + 3_600_000L) // ~1d 1h
                .build();
        String remaining = entry.getRemainingString();
        assertTrue(remaining.startsWith("1d"), "Should start with '1d', got: " + remaining);
    }
}
