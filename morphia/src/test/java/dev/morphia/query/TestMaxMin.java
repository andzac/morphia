package dev.morphia.query;

import com.mongodb.MongoException;
import dev.morphia.Datastore;
import dev.morphia.TestBase;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Field;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Index;
import dev.morphia.annotations.IndexOptions;
import dev.morphia.annotations.Indexes;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static dev.morphia.query.Sort.ascending;
import static dev.morphia.query.Sort.descending;

public class TestMaxMin extends TestBase {

    @Override
    @Before
    public void setUp() {
        super.setUp();
        getMapper().map(IndexedEntity.class);
        getDs().ensureIndexes();
    }

    @Test(expected = MongoException.class)
    public void testExceptionForIndexMismatch() {
        getDs().find(IndexedEntity.class).execute(new FindOptions()
                                                      .limit(1)
                                                      .min(new Document("doesNotExist", 1)))
               .next();
    }

    @Test
    public void testMax() {
        final IndexedEntity a = new IndexedEntity("a");
        final IndexedEntity b = new IndexedEntity("b");
        final IndexedEntity c = new IndexedEntity("c");

        Datastore ds = getDs();

        ds.save(a);
        ds.save(b);
        ds.save(c);

        ds.ensureIndexes();
        Assert.assertEquals("last", b.id, ds.find(IndexedEntity.class)
                                            .first(new FindOptions()
                                                       .sort(descending("id"))
                                                       .hint("testField")
                                                       .max(new Document("testField", "c")))
                                              .id);
        Assert.assertEquals("last", b.id, ds.find(IndexedEntity.class)
                                            .first(new FindOptions()
                                                       .sort(descending("id"))
                                                       .hint("testField")
                                                       .max(new Document("testField", "c")))
                                              .id);
    }

    @Test
    public void testMaxCompoundIndex() {
        final IndexedEntity a1 = new IndexedEntity("a");
        final IndexedEntity a2 = new IndexedEntity("a");
        final IndexedEntity b1 = new IndexedEntity("b");
        final IndexedEntity b2 = new IndexedEntity("b");
        final IndexedEntity c1 = new IndexedEntity("c");
        final IndexedEntity c2 = new IndexedEntity("c");

        Datastore ds = getDs();

        ds.save(a1);
        ds.save(a2);
        ds.save(b1);
        ds.save(b2);
        ds.save(c1);
        ds.save(c2);

        List<IndexedEntity> l = ds.find(IndexedEntity.class)
                                  .execute(new FindOptions()
                                               .sort(ascending("testField"), ascending("id")).hint(new Document("testField", 1)
                                                                                                       .append("_id", 1))
                                               .max(new Document("testField", "b").append("_id", b2.id)))
                                  .toList();

        Assert.assertEquals("size", 3, l.size());
        Assert.assertEquals("item", b1.id, l.get(2).id);

        l = ds.find(IndexedEntity.class)
              .execute(new FindOptions()
                           .sort(ascending("testField"), ascending("id"))
                           .hint(new Document("testField", 1)
                                     .append("_id", 1))
                           .max(new Document("testField", "b").append("_id", b2.id)))
              .toList();

        Assert.assertEquals("size", 3, l.size());
        Assert.assertEquals("item", b1.id, l.get(2).id);
    }

    @Test
    public void testMin() {
        final IndexedEntity a = new IndexedEntity("a");
        final IndexedEntity b = new IndexedEntity("b");
        final IndexedEntity c = new IndexedEntity("c");

        Datastore ds = getDs();

        ds.save(a);
        ds.save(b);
        ds.save(c);

        Assert.assertEquals("last", b.id, ds.find(IndexedEntity.class)
                                            .first(new FindOptions()
                                                       .sort(ascending("id"))
                                                       .hint("testField")
                                                       .min(new Document("testField", "b")))
                                              .id);

        Assert.assertEquals("last", b.id, ds.find(IndexedEntity.class)
                                            .first(new FindOptions()
                                                       .sort(ascending("id"))
                                                       .hint("testField")
                                                       .min(new Document("testField", "b")))
                                              .id);
    }

    @Test
    public void testMinCompoundIndex() {
        final IndexedEntity a1 = new IndexedEntity("a");
        final IndexedEntity a2 = new IndexedEntity("a");
        final IndexedEntity b1 = new IndexedEntity("b");
        final IndexedEntity b2 = new IndexedEntity("b");
        final IndexedEntity c1 = new IndexedEntity("c");
        final IndexedEntity c2 = new IndexedEntity("c");

        Datastore ds = getDs();

        ds.save(a1);
        ds.save(a2);
        ds.save(b1);
        ds.save(b2);
        ds.save(c1);
        ds.save(c2);

        List<IndexedEntity> l = ds.find(IndexedEntity.class)
                                  .execute(new FindOptions()
                                               .sort(ascending("testField"), ascending("id"))
                                               .hint(new Document("testField", 1)
                                                         .append("_id", 1))
                                               .min(new Document("testField", "b").append("_id", b1.id))
                                          )
                                  .toList();

        Assert.assertEquals("size", 4, l.size());
        Assert.assertEquals("item", b1.id, l.get(0).id);

        l = ds.find(IndexedEntity.class)
              .execute(new FindOptions()
                           .sort(ascending("testField"), ascending("id"))
                           .hint(new Document("testField", 1)
                                     .append("_id", 1))
                           .min(new Document("testField", "b").append("_id", b1.id)))
              .toList();

        Assert.assertEquals("size", 4, l.size());
        Assert.assertEquals("item", b1.id, l.get(0).id);
    }

    @Entity("IndexedEntity")
    @Indexes({
        @Index(fields = @Field("testField"),
            options = @IndexOptions(name = "testField")),
        @Index(fields = {@Field("testField"), @Field("_id")})})
    private static final class IndexedEntity {

        @Id
        private ObjectId id;
        private String testField;

        private IndexedEntity(final String testField) {
            this.testField = testField;
        }

        private IndexedEntity() {
        }
    }
}
