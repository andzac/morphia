package dev.morphia.mapping.primitives;


import dev.morphia.Datastore;
import dev.morphia.annotations.Entity;
import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Test;
import dev.morphia.TestBase;
import dev.morphia.annotations.Id;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class ShortMappingTest extends TestBase {
    @Test
    public void testMapping() {
        getMapper().map(Shorts.class);
        final Shorts ent = new Shorts();
        ent.listWrapperArray.add(new Short[]{1, 2});
        ent.listPrimitiveArray.add(new short[]{2, 3, 12});
        ent.listWrapper.addAll(Arrays.asList((short) 1, (short) 2));
        ent.singlePrimitive = 100;
        ent.singleWrapper = 47;
        ent.primitiveArray = new short[]{5, 93};
        ent.wrapperArray = new Short[]{55, 16, 99};
        ent.nestedPrimitiveArray = new short[][]{{5, 93}, {88}};
        ent.nestedWrapperArray = new Short[][]{{55, 16, 99}, {-47}};
        getDs().save(ent);
        final Shorts loaded = getDs().find(Shorts.class)
                                     .filter("_id", ent.id)
                                     .first();

        Assert.assertNotNull(loaded.id);

        Assert.assertArrayEquals(ent.listWrapperArray.get(0), loaded.listWrapperArray.get(0));
        Assert.assertArrayEquals(ent.listPrimitiveArray.get(0), loaded.listPrimitiveArray.get(0));

        Assert.assertEquals(ent.singlePrimitive, loaded.singlePrimitive, 0);
        Assert.assertEquals(ent.singleWrapper, loaded.singleWrapper, 0);

        Assert.assertArrayEquals(ent.primitiveArray, loaded.primitiveArray);
        Assert.assertArrayEquals(ent.wrapperArray, loaded.wrapperArray);
        Assert.assertArrayEquals(ent.nestedPrimitiveArray, loaded.nestedPrimitiveArray);
        Assert.assertArrayEquals(ent.nestedWrapperArray, loaded.nestedWrapperArray);
    }

    @Entity
    private static class Shorts {
        @Id
        private ObjectId id;
        private final List<Short[]> listWrapperArray = new ArrayList<Short[]>();
        private final List<short[]> listPrimitiveArray = new ArrayList<short[]>();
        private final List<Short> listWrapper = new ArrayList<Short>();
        private short singlePrimitive;
        private Short singleWrapper;
        private short[] primitiveArray;
        private Short[] wrapperArray;
        private short[][] nestedPrimitiveArray;
        private Short[][] nestedWrapperArray;
    }
}
