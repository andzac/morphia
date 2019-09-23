package dev.morphia.mapping.lazy;


import dev.morphia.TestBase;
import dev.morphia.annotations.Embedded;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Property;
import dev.morphia.annotations.Reference;
import dev.morphia.query.FindOptions;
import dev.morphia.query.Query;
import dev.morphia.testutil.TestEntity;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;


@Category(Reference.class)
public class LazyInEmbeddedTest extends TestBase {
    @Test
    public void testLoadingOfRefInField() {
        // TODO us: exclusion does not work properly with maven + junit4
        if (!LazyFeatureDependencies.testDependencyFullFilled()) {
            return;
        }

        getMapper().map(ContainerWithRefInField.class);
        getMapper().map(OtherEntity.class);

        OtherEntity otherEntity = new OtherEntity();
        ContainerWithRefInField containerWithRefInField = new ContainerWithRefInField();

        getDs().save(asList(otherEntity, containerWithRefInField));

        otherEntity = getDs().get(otherEntity);
        containerWithRefInField = getDs().get(containerWithRefInField);
        Assert.assertNotNull(otherEntity);
        Assert.assertNotNull(containerWithRefInField);

        final EmbedWithRef embedWithRef = new EmbedWithRef();
        embedWithRef.otherEntity = otherEntity;
        containerWithRefInField.embedWithRef = embedWithRef;

        getDs().save(containerWithRefInField);

        containerWithRefInField = getDs().get(containerWithRefInField);
        Assert.assertNotNull(containerWithRefInField);

    }

    @Test
    public void testLoadingOfRefInList() {
        // TODO us: exclusion does not work properly with maven + junit4
        if (!LazyFeatureDependencies.testDependencyFullFilled()) {
            return;
        }

        getMapper().map(ContainerWithRefList.class);
        getMapper().map(OtherEntity.class);

        OtherEntity otherEntity = new OtherEntity();
        ContainerWithRefList containerWithRefInList = new ContainerWithRefList();

        getDs().save(asList(otherEntity, containerWithRefInList));

        otherEntity = getDs().get(otherEntity);
        containerWithRefInList = getDs().get(containerWithRefInList);
        Assert.assertNotNull(otherEntity);
        Assert.assertNotNull(containerWithRefInList);

        final EmbedWithRef embedWithRef = new EmbedWithRef();
        embedWithRef.otherEntity = otherEntity;
        containerWithRefInList.embedWithRef.add(embedWithRef);

        getDs().save(asList(otherEntity, containerWithRefInList));

        containerWithRefInList = getDs().get(containerWithRefInList);
        Assert.assertNotNull(containerWithRefInList);

        final Query<ContainerWithRefList> createQuery = getDs().find(ContainerWithRefList.class);
        containerWithRefInList = createQuery.execute(new FindOptions().limit(1)).tryNext();
        Assert.assertNotNull(containerWithRefInList);

    }

    @Test
    public void testLoadingOfRefThroughInheritanceInField() {
        // TODO us: exclusion does not work properly with maven + junit4
        if (!LazyFeatureDependencies.testDependencyFullFilled()) {
            return;
        }

        getMapper().map(ContainerWithRefInField.class);
        getMapper().map(OtherEntityChild.class);

        OtherEntityChild otherEntity = new OtherEntityChild();
        ContainerWithRefInField containerWithRefInField = new ContainerWithRefInField();

        getDs().save(asList(otherEntity, containerWithRefInField));

        otherEntity = getDs().get(otherEntity);
        final ContainerWithRefInField reload = getDs().get(containerWithRefInField);
        Assert.assertNotNull(otherEntity);
        Assert.assertNotNull(reload);

        final EmbedWithRef embedWithRef = new EmbedWithRef();
        embedWithRef.otherEntity = otherEntity;
        reload.embedWithRef = embedWithRef;

        getDs().save(reload);
        getDs().get(reload);
        containerWithRefInField = getDs().get(containerWithRefInField);
        Assert.assertNotNull(containerWithRefInField);

    }

    @Test
    public void testLoadingOfRefThroughInheritanceInList() {
        // TODO us: exclusion does not work properly with maven + junit4
        if (!LazyFeatureDependencies.testDependencyFullFilled()) {
            return;
        }

        getMapper().map(ContainerWithRefList.class);
        getMapper().map(OtherEntityChild.class);

        OtherEntityChild otherEntity = new OtherEntityChild();
        ContainerWithRefList containerWithRefInList = new ContainerWithRefList();

        getDs().save(asList(otherEntity, containerWithRefInList));

        otherEntity = getDs().get(otherEntity);
        final ContainerWithRefList reload = getDs().get(containerWithRefInList);
        Assert.assertNotNull(otherEntity);
        Assert.assertNotNull(reload);

        final EmbedWithRef embedWithRef = new EmbedWithRef();
        embedWithRef.otherEntity = otherEntity;
        reload.embedWithRef.add(embedWithRef);

        getDs().save(asList(otherEntity, reload));

        getDs().get(reload);

        containerWithRefInList = getDs().get(reload);
        Assert.assertNotNull(containerWithRefInList);
        final Query<ContainerWithRefList> createQuery = getDs().find(ContainerWithRefList.class);
        containerWithRefInList = createQuery.execute(new FindOptions().limit(1)).tryNext();
        Assert.assertNotNull(containerWithRefInList);

    }

    public enum SomeEnum {
        B,
        A
    }

    @Entity
    public static class ContainerWithRefInField extends TestEntity {
        private EmbedWithRef embedWithRef;
    }

    @Entity
    public static class ContainerWithRefList extends TestEntity {
        private final List<EmbedWithRef> embedWithRef = new ArrayList<>();
    }

    @Entity("other")
    public static class OtherEntity extends TestEntity {
        @Property(value = "some")
        private SomeEnum someEnum;

        protected OtherEntity() {
        }

        public OtherEntity(final SomeEnum someEnum) {
            this.someEnum = someEnum;

        }
    }

    public static class OtherEntityChild extends OtherEntity {
        @Property
        private String name;

        public OtherEntityChild() {
            super(SomeEnum.A);
        }
    }

    @Embedded
    public static class EmbedWithRef implements Serializable {

        @Reference(lazy = true)
        private OtherEntity otherEntity;
    }
}
