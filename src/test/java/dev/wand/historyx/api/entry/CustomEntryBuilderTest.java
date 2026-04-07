package dev.wand.historyx.api.entry;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

class CustomEntryBuilderTest {

    @Test
    @DisplayName("Builder with required fields only uses correct defaults")
    void requiredFieldsOnly() {
        CustomEntry entry = CustomEntry.builder(1, PunishmentType.BAN, CustomEntry.EntrySource.LITEBANS).build();

        assertEquals(1, entry.getId());
        assertEquals(PunishmentType.BAN, entry.getType());
        assertEquals(CustomEntry.EntrySource.LITEBANS, entry.getSource());

        // String defaults
        assertNull(entry.getUuid());
        assertNull(entry.getIp());
        assertNull(entry.getReason());
        assertNull(entry.getExecutorUUID());
        assertNull(entry.getExecutorName());
        assertNull(entry.getRemovedByUUID());
        assertNull(entry.getRemovedByName());
        assertNull(entry.getRemovalReason());
        assertNull(entry.getServerScope());
        assertNull(entry.getServerOrigin());

        // Long defaults
        assertEquals(0, entry.getDateStart());
        assertEquals(-1, entry.getDateEnd());
        assertEquals(-1, entry.getDuration());

        // Boolean defaults
        assertFalse(entry.isSilent());
        assertFalse(entry.isIpban());
        assertFalse(entry.isActive());
    }

    @Test
    @DisplayName("Builder with all fields set returns correct values")
    void allFieldsSet() {
        CustomEntry entry = CustomEntry.builder(42, PunishmentType.MUTE, CustomEntry.EntrySource.BANMANAGER)
                .uuid("uuid-123")
                .ip("192.168.1.%")
                .reason("Spamming")
                .executorUUID("exec-uuid")
                .executorName("Admin")
                .removedByUUID("remover-uuid")
                .removedByName("HeadAdmin")
                .removalReason("Appeal accepted")
                .serverScope("survival")
                .serverOrigin("lobby")
                .dateStart(1000L)
                .dateEnd(2000L)
                .duration(1000L)
                .silent(true)
                .ipban(true)
                .active(true)
                .build();

        assertEquals(42, entry.getId());
        assertEquals(PunishmentType.MUTE, entry.getType());
        assertEquals(CustomEntry.EntrySource.BANMANAGER, entry.getSource());
        assertEquals("uuid-123", entry.getUuid());
        assertEquals("192.168.1.%", entry.getIp());
        assertEquals("Spamming", entry.getReason());
        assertEquals("exec-uuid", entry.getExecutorUUID());
        assertEquals("Admin", entry.getExecutorName());
        assertEquals("remover-uuid", entry.getRemovedByUUID());
        assertEquals("HeadAdmin", entry.getRemovedByName());
        assertEquals("Appeal accepted", entry.getRemovalReason());
        assertEquals("survival", entry.getServerScope());
        assertEquals("lobby", entry.getServerOrigin());
        assertEquals(1000L, entry.getDateStart());
        assertEquals(2000L, entry.getDateEnd());
        assertEquals(1000L, entry.getDuration());
        assertTrue(entry.isSilent());
        assertTrue(entry.isIpban());
        assertTrue(entry.isActive());
    }

    @Test
    @DisplayName("Builder produces independent instances on multiple builds")
    void independentInstances() {
        CustomEntry.Builder builder = CustomEntry.builder(1, PunishmentType.BAN, CustomEntry.EntrySource.LITEBANS)
                .reason("First");
        CustomEntry first = builder.build();

        builder.reason("Second");
        CustomEntry second = builder.build();

        assertEquals("First", first.getReason());
        assertEquals("Second", second.getReason());
        assertNotSame(first, second);
    }

    @Test
    @DisplayName("Builder matches 19-param constructor output")
    void backwardsCompat_19param() {
        CustomEntry fromConstructor = new CustomEntry(
                1, PunishmentType.BAN, "uuid", "ip", "reason", "execUUID", "execName",
                "remUUID", "remName", "remReason", 100L, 200L,
                "scope", "origin", true, true, true, 100L,
                CustomEntry.EntrySource.LITEBANS);

        CustomEntry fromBuilder = CustomEntry.builder(1, PunishmentType.BAN, CustomEntry.EntrySource.LITEBANS)
                .uuid("uuid").ip("ip").reason("reason")
                .executorUUID("execUUID").executorName("execName")
                .removedByUUID("remUUID").removedByName("remName").removalReason("remReason")
                .dateStart(100L).dateEnd(200L)
                .serverScope("scope").serverOrigin("origin")
                .silent(true).ipban(true).active(true).duration(100L)
                .build();

        assertEquals(fromConstructor.getId(), fromBuilder.getId());
        assertEquals(fromConstructor.getType(), fromBuilder.getType());
        assertEquals(fromConstructor.getUuid(), fromBuilder.getUuid());
        assertEquals(fromConstructor.getIp(), fromBuilder.getIp());
        assertEquals(fromConstructor.getReason(), fromBuilder.getReason());
        assertEquals(fromConstructor.getExecutorUUID(), fromBuilder.getExecutorUUID());
        assertEquals(fromConstructor.getExecutorName(), fromBuilder.getExecutorName());
        assertEquals(fromConstructor.getRemovedByUUID(), fromBuilder.getRemovedByUUID());
        assertEquals(fromConstructor.getRemovedByName(), fromBuilder.getRemovedByName());
        assertEquals(fromConstructor.getRemovalReason(), fromBuilder.getRemovalReason());
        assertEquals(fromConstructor.getDateStart(), fromBuilder.getDateStart());
        assertEquals(fromConstructor.getDateEnd(), fromBuilder.getDateEnd());
        assertEquals(fromConstructor.getServerScope(), fromBuilder.getServerScope());
        assertEquals(fromConstructor.getServerOrigin(), fromBuilder.getServerOrigin());
        assertEquals(fromConstructor.isSilent(), fromBuilder.isSilent());
        assertEquals(fromConstructor.isIpban(), fromBuilder.isIpban());
        assertEquals(fromConstructor.isActive(), fromBuilder.isActive());
        assertEquals(fromConstructor.getDuration(), fromBuilder.getDuration());
        assertEquals(fromConstructor.getSource(), fromBuilder.getSource());
    }
}
