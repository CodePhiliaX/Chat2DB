// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package ai.chat2db.server.web.api.controller.ai.azure.model;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import ai.chat2db.server.web.api.controller.ai.azure.util.AzureReflectionUtils;
import com.fasterxml.jackson.annotation.JsonValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.lang.invoke.MethodType.methodType;

/**
 * Base implementation for expandable, single string enums.
 *
 * @param <T> a specific expandable enum type
 */
public abstract class AzureExpandableStringEnum<T extends AzureExpandableStringEnum<T>> {
    private static final Map<Class<?>, MethodHandle> CONSTRUCTORS = new ConcurrentHashMap<>();
    private static final Map<Class<?>, ConcurrentHashMap<String, ? extends AzureExpandableStringEnum<?>>> VALUES
        = new ConcurrentHashMap<>();

    private static final Logger LOGGER = LoggerFactory.getLogger(AzureExpandableStringEnum.class);
    private String name;
    private Class<T> clazz;

    /**
     * Creates a new instance of {@link AzureExpandableStringEnum} without a {@link #toString()} value.
     * <p>
     * This constructor shouldn't be called as it will produce a {@link AzureExpandableStringEnum} which doesn't
     * have a String enum value.
     *
     * @deprecated Use the {@link #fromString(String, Class)} factory method.
     */
    @Deprecated
    public AzureExpandableStringEnum() {
    }

    /**
     * Creates an instance of the specific expandable string enum from a String.
     *
     * @param name The value to create the instance from.
     * @param clazz The class of the expandable string enum.
     * @param <T> the class of the expandable string enum.
     * @return The expandable string enum instance.
     *
     * @throws RuntimeException wrapping implementation class constructor exception (if any is thrown).
     */
    @SuppressWarnings({"unchecked", "deprecation"})
    protected static <T extends AzureExpandableStringEnum<T>> T fromString(String name, Class<T> clazz) {
        if (name == null) {
            return null;
        }

        ConcurrentHashMap<String, ?> clazzValues = VALUES.computeIfAbsent(clazz, key -> new ConcurrentHashMap<>());
        T value = (T) clazzValues.get(name);

        if (value != null) {
            return value;
        } else {
            MethodHandle ctor = CONSTRUCTORS.computeIfAbsent(clazz, AzureExpandableStringEnum::getDefaultConstructor);

            if (ctor == null) {
                // logged in ExpandableStringEnum::getDefaultConstructor
                return null;
            }

            try {
                value = (T) ctor.invoke();
            } catch (Throwable e) {
                LOGGER.warn("Failed to create {}, default constructor threw exception", clazz.getName(), e);
                return null;
            }

            return value.nameAndAddValue(name, value, clazz);
        }
    }

    private static <T> MethodHandle getDefaultConstructor(Class<T> clazz) {
        try {
            MethodHandles.Lookup lookup = AzureReflectionUtils.getLookupToUse(clazz);
            return lookup.findConstructor(clazz, methodType(void.class));
        } catch (NoSuchMethodException | IllegalAccessException e) {
            LOGGER.info("Can't find or access default constructor for {}, make sure corresponding package is open to azure-core", clazz.getName(), e);
        } catch (Exception e) {
            LOGGER.info("Failed to get lookup for {}", clazz.getName(), e);
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    T nameAndAddValue(String name, T value, Class<T> clazz) {
        this.name = name;
        this.clazz = clazz;

        ((ConcurrentHashMap<String, T>) VALUES.get(clazz)).put(name, value);
        return (T) this;
    }

    /**
     * Gets a collection of all known values to an expandable string enum type.
     *
     * @param clazz the class of the expandable string enum.
     * @param <T> the class of the expandable string enum.
     * @return A collection of all known values for the given {@code clazz}.
     */
    @SuppressWarnings("unchecked")
    protected static <T extends AzureExpandableStringEnum<T>> Collection<T> values(Class<T> clazz) {
        return new ArrayList<T>((Collection<T>) VALUES.getOrDefault(clazz, new ConcurrentHashMap<>()).values());
    }

    @Override
    @JsonValue
    public String toString() {
        return this.name;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.clazz, this.name);
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        } else if (clazz == null || !clazz.isAssignableFrom(obj.getClass())) {
            return false;
        } else if (obj == this) {
            return true;
        } else if (this.name == null) {
            return ((AzureExpandableStringEnum<T>) obj).name == null;
        } else {
            return this.name.equals(((AzureExpandableStringEnum<T>) obj).name);
        }
    }
}
