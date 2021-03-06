package dev.morphia.ext.guice;


import com.google.inject.Inject;
import com.google.inject.Injector;
import dev.morphia.ObjectFactory;
import dev.morphia.mapping.MappedField;
import dev.morphia.mapping.Mapper;
import dev.morphia.utils.Assert;
import org.bson.Document;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * @author us@thomas-daily.de
 */
public class GuiceObjectFactory implements ObjectFactory {

    // rather messy, i'd like ObjectFactory to be tackled to have a clean
    // separation of concern (choose impl class vs. instantiate & inject)

    private final ObjectFactory delegate;
    private final Injector injector;

    /**
     * Create a GuiceObjectFactory wrapper around an ObjectFactory
     *
     * @param delegate the ObjectFactory to wrap
     * @param injector the Guice Injector to use
     */
    public GuiceObjectFactory(final ObjectFactory delegate, final Injector injector) {
        this.delegate = delegate;
        this.injector = injector;
    }

    @Override
    public <T> T createInstance(final Class<T> clazz) {
        Assert.parameterNotNull("clazz", clazz);

        if (injectOnConstructor(clazz)) {
            return injector.getInstance(clazz);
        }

        return injectMembers(delegate.createInstance(clazz));
    }

    @SuppressWarnings("unchecked")
    private boolean injectOnConstructor(final Class clazz) {
        final Constructor[] cs = clazz.getDeclaredConstructors();
        for (final Constructor constructor : cs) {
            if (constructor.getAnnotation(Inject.class) != null) {
                return true;
            }
        }
        return false;
    }

    private <T> T injectMembers(final T o) {
        if (o != null) {
            injector.injectMembers(o);
        }
        return o;
    }

    @Override
    public <T> T createInstance(final Class<T> clazz, final Document document) {
        if (injectOnConstructor(clazz)) {
            return injector.getInstance(clazz);
        }

        return injectMembers(delegate.createInstance(clazz, document));
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object createInstance(final Mapper mapper, final MappedField mf, final Document document) {
        final Class clazz = mf.getType();
        if (clazz.isInterface() || Modifier.isAbstract(clazz.getModifiers())) {
            // there is no good way to find the clazz to use, yet, so delegate
            return injectMembers(delegate.createInstance(mapper, mf, document));
        }

        if (injectOnConstructor(clazz)) {
            return injector.getInstance(clazz);
        }

        return injectMembers(delegate.createInstance(mapper, mf, document));
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map createMap(final MappedField mf) {
        final Class clazz = mf.getType();
        if (clazz.isInterface() || Modifier.isAbstract(clazz.getModifiers())) {
            // there is no good way to find the clazz to use, yet, so delegate
            return injectMembers(delegate.createMap(mf));
        }

        if (injectOnConstructor(clazz)) {
            return (Map) injector.getInstance(clazz);
        }

        return injectMembers(delegate.createMap(mf));
    }

    @Override
    @SuppressWarnings("unchecked")
    public List createList(final MappedField mf) {
        final Class clazz = mf.getType();
        if (clazz.isInterface() || Modifier.isAbstract(clazz.getModifiers())) {
            // there is no good way to find the clazz to use, yet, so delegate
            return injectMembers(delegate.createList(mf));
        }

        if (injectOnConstructor(clazz)) {
            return (List) injector.getInstance(clazz);
        }

        return injectMembers(delegate.createList(mf));
    }

    @Override
    @SuppressWarnings("unchecked")
    public Set createSet(final MappedField mf) {
        final Class clazz = mf.getType();
        if (clazz.isInterface() || Modifier.isAbstract(clazz.getModifiers())) {
            // there is no good way to find the clazz to use, yet, so delegate
            return injectMembers(delegate.createSet(mf));
        }

        if (injectOnConstructor(clazz)) {
            return (Set) injector.getInstance(clazz);
        }

        return injectMembers(delegate.createSet(mf));
    }
}
