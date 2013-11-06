package br.com.redefood.hibernate;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.collection.internal.PersistentMap;
import org.hibernate.collection.spi.PersistentCollection;
import org.hibernate.proxy.HibernateProxy;

import br.com.redefood.hibernate.Hibernate4Module.Feature;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.Serializers;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.databind.type.TypeFactory;


public class HibernateSerializers extends Serializers.Base
{
    protected final int _moduleFeatures;
    
    public HibernateSerializers(int features)
    {
        _moduleFeatures = features;
    }

    @Override
    public JsonSerializer<?> findSerializer(SerializationConfig config,
            JavaType type, BeanDescription beanDesc)
    {
        Class<?> raw = type.getRawClass();

        /* 13-Jul-2012, tatu: There's a bug in Jackson 2.0 which will call this
         *    method in some cases for Collections (and Maps); let's skip
         *    those cases and wait for the "real" call
         */
        if (Collection.class.isAssignableFrom(raw)
                || Map.class.isAssignableFrom(raw)) {
            return null;
        }
        
        /* Note: PersistentCollection does not implement Collection, so we
         * may get some types here; most do implement Collection too however
         */
        if (PersistentCollection.class.isAssignableFrom(raw)) {

            JavaType elementType = _figureFallbackType(config, type);
            return new PersistentCollectionSerializer(elementType,
                    isEnabled(Feature.FORCE_LAZY_LOADING));
        }
        if (HibernateProxy.class.isAssignableFrom(raw)) {
            return new HibernateProxySerializer(isEnabled(Feature.FORCE_LAZY_LOADING));
        }
        return null;
    }

    @Override
    public JsonSerializer<?> findCollectionSerializer(SerializationConfig config,
            CollectionType type, BeanDescription beanDesc,
            TypeSerializer elementTypeSerializer, JsonSerializer<Object> elementValueSerializer)
    {
        Class<?> raw = type.getRawClass();
        // only handle PersistentCollection style collections...
        if (PersistentCollection.class.isAssignableFrom(raw)) {
            /* And for those, figure out "fallback type"; we MUST have some idea of
             * type to deserialize, aside from nominal PersistentXxx type.
             */
            return new PersistentCollectionSerializer(_figureFallbackType(config, type),
                    isEnabled(Feature.FORCE_LAZY_LOADING));
        }
        return null;
    }

    @Override
    public JsonSerializer<?> findMapSerializer(SerializationConfig config,
            MapType type, BeanDescription beanDesc,
            JsonSerializer<Object> keySerializer,
            TypeSerializer elementTypeSerializer, JsonSerializer<Object> elementValueSerializer)
    {
        Class<?> raw = type.getRawClass();
        // 05-Jun-2012, tatu: PersistentMap DOES implement java.util.Map...
        if (PersistentMap.class.isAssignableFrom(raw)) {
            return new PersistentCollectionSerializer(_figureFallbackType(config, type),
                    isEnabled(Feature.FORCE_LAZY_LOADING));
        }
        return null;
    }
    
    public final boolean isEnabled(Hibernate4Module.Feature f) {
        return (_moduleFeatures & f.getMask()) != 0;
    }

    protected JavaType _figureFallbackType(SerializationConfig config,
            JavaType persistentType)
    {
        // Alas, PersistentTypes are NOT generics-aware... meaning can't specify parameterization
        Class<?> raw = persistentType.getRawClass();
        TypeFactory tf = config.getTypeFactory();
        if (Map.class.isAssignableFrom(raw)) {
            return tf.constructMapType(Map.class, Object.class, Object.class);
        }
        if (List.class.isAssignableFrom(raw)) {
            return tf.constructCollectionType(List.class, Object.class);
        }
        if (Set.class.isAssignableFrom(raw)) {
            return tf.constructCollectionType(Set.class, Object.class);
        }
        // ok, just Collection of some kind
        return tf.constructCollectionType(Collection.class, Object.class);
    }
}
