package dev.morphia;

import dev.morphia.mapping.Mapper;
import org.bson.types.ObjectId;
import org.junit.Test;
import dev.morphia.annotations.CappedAt;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.query.FindOptions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestCapped extends TestBase {
    @Test
    public void testCappedEntity() {
        // given
        getMapper().map(CurrentStatus.class);
        getDs().ensureCaps();

        // when-then
        final CurrentStatus cs = new CurrentStatus("All Good");
        getDs().save(cs);
        assertEquals(1, getDs().find(CurrentStatus.class).count());
        getDs().save(new CurrentStatus("Kinda Bad"));
        assertEquals(1, getDs().find(CurrentStatus.class).count());
        assertTrue(getDs().find(CurrentStatus.class)
                          .execute(new FindOptions().limit(1))
                          .next()
                       .message.contains("Bad"));
        getDs().save(new CurrentStatus("Kinda Bad2"));
        assertEquals(1, getDs().find(CurrentStatus.class).count());
        getDs().save(new CurrentStatus("Kinda Bad3"));
        assertEquals(1, getDs().find(CurrentStatus.class).count());
        getDs().save(new CurrentStatus("Kinda Bad4"));
        assertEquals(1, getDs().find(CurrentStatus.class).count());
    }

    @Entity(cap = @CappedAt(count = 1))
    private static class CurrentStatus {
        @Id
        private ObjectId id;
        private String message;

        private CurrentStatus() {
        }

        CurrentStatus(final String msg) {
            message = msg;
        }
    }

}
