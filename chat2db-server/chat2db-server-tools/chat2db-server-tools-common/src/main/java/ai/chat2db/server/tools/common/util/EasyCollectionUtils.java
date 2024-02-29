package ai.chat2db.server.tools.common.util;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import cn.hutool.db.meta.Table;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.boot.autoconfigure.security.SecurityProperties;

/**
 * Collection tool class
 *
 * @author Jiaju Zhuang
 */
public class EasyCollectionUtils {

    /**
     * Collection stream
     *
     * @param collection collection
     * @param <T> Return type
     * @return the stream of the collection
     */
    public static <T> Stream<T> stream(Collection<T> collection) {
        return collection != null ? collection.stream() : Stream.empty();
    }

    /**
     * Return the first element. If there is none, return empty.
     *
     * @param collection collection
     * @param <T> data type
     * @return Returns the first element, which may be empty
     */
    public static <T> T findFirst(Collection<T> collection) {
        return stream(collection)
            .findFirst()
            .orElse(null);
    }

    /**
     * Convert a collection into a list
     * <p>
     * Will filter the empty data before and after conversion in the collection, so the input and output parameters will be inconsistent.
     *
     * @param collection collection
     * @param function conversion function
     * @param <T> Data type before conversion
     * @param <R> Data type after conversion
     * @return list If the input parameter is empty, an empty array will be returned and cannot be modified.
     */
    public static <T, R> List<R> toList(Collection<T> collection, Function<T, R> function) {
        return stream(collection)
            .filter(Objects::nonNull)
            .map(function)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }

    /**
     * Convert a collection into a set
     * <p>
     * Will filter out empty data before and after conversion in the collection
     *
     * @param collection collection
     * @param function conversion function
     * @param <T> Data type before conversion
     * @param <R> Data type after conversion
     * @return list If the input parameter is empty, an empty array will be returned and cannot be modified.
     */
    public static <T, R> Set<R> toSet(Collection<T> collection, Function<T, R> function) {
        return stream(collection)
            .filter(Objects::nonNull)
            .map(function)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());
    }

    /**
     * Convert a set into a map. If there is a key conflict, the second one will prevail.
     *
     * @param collection collection
     * @param keyFunction keyFunction
     * @param valueFunction valueFunction
     * @param <K> key data type
     * @param <V> value data type
     * @param <T> Data type before conversion
     * @return Convert to future map
     */
    public static <K, V, T> Map<K, V> toMap(Collection<T> collection, Function<? super T, K> keyFunction,
        Function<? super T, V> valueFunction) {
        return stream(collection)
            .filter(Objects::nonNull)
            .collect(Collectors.toMap(keyFunction, valueFunction, (oldValue, newValue) -> newValue));
    }

    /**
     * Convert a set into a map. The value of the map is the value of the set. If there is a key conflict, the second one shall prevail.
     *
     * @param collection collection
     * @param keyFunction keyFunction
     * @param <K> key data type
     * @param <T> Data type before conversion
     * @return Convert to future map
     */
    public static <K, T> Map<K, T> toIdentityMap(Collection<T> collection, Function<? super T, K> keyFunction) {
        return toMap(collection, keyFunction, Function.identity());
    }

    /**
     * Add another set to a set
     *
     * @param collection original collection
     * @param collectionAdd The collection to be added
     * @param <C>
     * @return whether data has been added
     */
    public static <C> boolean addAll(final Collection<C> collection, final Collection<C> collectionAdd) {
        if (collectionAdd == null) {
            return false;
        }
        return collection.addAll(collectionAdd);
    }

    /**
     * Determine if the length of a set is 0 but not null
     *
     * @param collection collection
     * @return
     */
    public static boolean isEmptyButNotNull(final Collection<?> collection) {
        return collection != null && collection.isEmpty();
    }

    /**
     * Determine whether there is an array with a length of 0 but not null in a bunch of collections
     *
     * @param collections returns false if it is empty
     * @return
     */
    public static boolean isAnyEmptyButNotNull(final Collection<?>... collections) {
        if (ArrayUtils.isEmpty(collections)) {
            return false;
        }
        for (final Collection<?> collection : collections) {
            if (isEmptyButNotNull(collection)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Add an object to the collection
     * @param collection original collection
     * @param objectAdd the object to be added
     * @param <T>
     */
    public static <T> void add(Collection<T> collection, T objectAdd) {
        if(Objects.isNull(objectAdd)){
            return;
        }
        collection.add(objectAdd);
    }

    /**
     * Deduplication based on specified field collection
     * @param collection original collection
     * @param keyFunction keyFunction
     * @param <E>
     * @param <R>
     * @return the collection after deduplication
     */
    public static <E,R> List<E> distinctByKey(Collection<E> collection, Function<E, R> keyFunction){
        return stream(collection).filter(distinctByKey(keyFunction)).collect(Collectors.toList());
    }


    static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

    public static <E> List<E> union(List<? extends E> list1, List<? extends E> list2) {
        ArrayList<E> result = new ArrayList();
        if(list1 != null && list1.size()>0) {
            result.addAll(list1);
        }
        if(list2!= null && list2.size()>0) {
            result.addAll(list2);
        }
        return result;
    }

}
