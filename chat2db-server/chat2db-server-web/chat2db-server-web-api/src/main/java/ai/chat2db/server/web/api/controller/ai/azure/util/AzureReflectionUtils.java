// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package ai.chat2db.server.web.api.controller.ai.azure.util;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Constructor;
import java.security.PrivilegedExceptionAction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility methods that aid in performing reflective operations.
 */
@SuppressWarnings("deprecation")
public final class AzureReflectionUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(AzureReflectionUtils.class);

    private static final boolean MODULE_BASED;

    private static final MethodHandle CLASS_GET_MODULE_METHOD_HANDLE;
    private static final MethodHandle MODULE_IS_NAMED_METHOD_HANDLE;
    private static final MethodHandle MODULE_ADD_READS_METHOD_HANDLE;
    private static final MethodHandle METHOD_HANDLES_PRIVATE_LOOKUP_IN_METHOD_HANDLE;
    private static final MethodHandle MODULE_IS_OPEN_UNCONDITIONALLY_METHOD_HANDLE;
    private static final MethodHandle MODULE_IS_OPEN_TO_OTHER_MODULE_METHOD_HANDLE;

    private static final MethodHandles.Lookup LOOKUP;
    private static final Object CORE_MODULE;

    private static final MethodHandle JDK_INTERNAL_PRIVATE_LOOKUP_IN_CONSTRUCTOR;

    static {
        boolean moduleBased = false;
        MethodHandle classGetModule = null;
        MethodHandle moduleIsNamed = null;
        MethodHandle moduleAddReads = null;
        MethodHandle methodHandlesPrivateLookupIn = null;
        MethodHandle moduleIsOpenUnconditionally = null;
        MethodHandle moduleIsOpenToOtherModule = null;

        MethodHandles.Lookup lookup = MethodHandles.lookup();
        Object coreModule = null;

        MethodHandle jdkInternalPrivateLookupInConstructor = null;

        try {
            Class<?> moduleClass = Class.forName("java.lang.Module");
            classGetModule = lookup.unreflect(Class.class.getDeclaredMethod("getModule"));
            moduleIsNamed = lookup.unreflect(moduleClass.getDeclaredMethod("isNamed"));
            moduleAddReads = lookup.unreflect(moduleClass.getDeclaredMethod("addReads", moduleClass));
            methodHandlesPrivateLookupIn = lookup.findStatic(MethodHandles.class, "privateLookupIn",
                MethodType.methodType(MethodHandles.Lookup.class, Class.class, MethodHandles.Lookup.class));
            moduleIsOpenUnconditionally = lookup.unreflect(moduleClass.getDeclaredMethod("isOpen", String.class));
            moduleIsOpenToOtherModule = lookup.unreflect(
                moduleClass.getDeclaredMethod("isOpen", String.class, moduleClass));

            coreModule = classGetModule.invokeWithArguments(AzureReflectionUtils.class);
            moduleBased = true;
        } catch (Throwable throwable) {
            if (throwable instanceof Error) {
                throw (Error) throwable;
            } else {
                LOGGER.error("Unable to create MethodHandles to use Java 9+ MethodHandles.privateLookupIn. "
                        + "Will attempt to fallback to using the package-private constructor.", throwable);
            }
        }

        if (!moduleBased) {
            try {
                Constructor<MethodHandles.Lookup> privateLookupInConstructor =
                    MethodHandles.Lookup.class.getDeclaredConstructor(Class.class);

                if (!privateLookupInConstructor.isAccessible()) {
                    privateLookupInConstructor.setAccessible(true);
                }

                jdkInternalPrivateLookupInConstructor = lookup.unreflectConstructor(privateLookupInConstructor);
            } catch (ReflectiveOperationException ex) {
                LOGGER.error("Unable to use package-private MethodHandles.Lookup constructor.", ex);
            }
        }

        MODULE_BASED = moduleBased;
        CLASS_GET_MODULE_METHOD_HANDLE = classGetModule;
        MODULE_IS_NAMED_METHOD_HANDLE = moduleIsNamed;
        MODULE_ADD_READS_METHOD_HANDLE = moduleAddReads;
        METHOD_HANDLES_PRIVATE_LOOKUP_IN_METHOD_HANDLE = methodHandlesPrivateLookupIn;
        MODULE_IS_OPEN_UNCONDITIONALLY_METHOD_HANDLE = moduleIsOpenUnconditionally;
        MODULE_IS_OPEN_TO_OTHER_MODULE_METHOD_HANDLE = moduleIsOpenToOtherModule;
        LOOKUP = lookup;
        CORE_MODULE = coreModule;
        JDK_INTERNAL_PRIVATE_LOOKUP_IN_CONSTRUCTOR = jdkInternalPrivateLookupInConstructor;
    }

    /**
     * Gets the {@link MethodHandles.Lookup} to use when performing reflective operations.
     * <p>
     * If Java 8 is being used this will always return {@link MethodHandles.Lookup#publicLookup()} as Java 8 doesn't
     * have module boundaries that will prevent reflective access to the {@code targetClass}.
     * <p>
     * If Java 9 or above is being used this will return a {@link MethodHandles.Lookup} based on whether the module
     * containing the {@code targetClass} exports the package containing the class. Otherwise, the
     * {@link MethodHandles.Lookup} associated to {@code com.azure.core} will attempt to read the module containing
     * {@code targetClass}.
     *
     * @param targetClass The {@link Class} that will need to be reflectively accessed.
     * @return The {@link MethodHandles.Lookup} that will allow {@code com.azure.core} to access the {@code targetClass}
     * reflectively.
     * @throws Exception If the underlying reflective calls throw an exception.
     */
    public static MethodHandles.Lookup getLookupToUse(Class<?> targetClass) throws Exception {
        try {
            if (MODULE_BASED) {
                Object responseModule = CLASS_GET_MODULE_METHOD_HANDLE.invoke(targetClass);

                // The unnamed module is opened unconditionally, have Core read it and use a private proxy lookup to
                // enable all lookup scenarios.
                if (!(boolean) MODULE_IS_NAMED_METHOD_HANDLE.invoke(responseModule)) {
                    MODULE_ADD_READS_METHOD_HANDLE.invokeWithArguments(CORE_MODULE, responseModule);
                    return performSafePrivateLookupIn(targetClass);
                }


                // If the response module is the Core module return the Core private lookup.
                if (responseModule == CORE_MODULE) {
                    return LOOKUP;
                }

                // Next check if the target class module is opened either unconditionally or to Core's module. If so,
                // also use a private proxy lookup to enable all lookup scenarios.
                String packageName = targetClass.getPackage().getName();
                if ((boolean) MODULE_IS_OPEN_UNCONDITIONALLY_METHOD_HANDLE
                    .invokeWithArguments(responseModule, packageName)
                    || (boolean) MODULE_IS_OPEN_TO_OTHER_MODULE_METHOD_HANDLE
                    .invokeWithArguments(responseModule, packageName, CORE_MODULE)) {
                    MODULE_ADD_READS_METHOD_HANDLE.invokeWithArguments(CORE_MODULE, responseModule);
                    return performSafePrivateLookupIn(targetClass);
                }

                // Otherwise, return the public lookup as there are no specialty ways to access the other module.
                return MethodHandles.publicLookup();
            } else {
                return (MethodHandles.Lookup) JDK_INTERNAL_PRIVATE_LOOKUP_IN_CONSTRUCTOR.invoke(targetClass);
            }
        } catch (Throwable throwable) {
            // invoke(Class<?) throws a Throwable as the underlying method being called through reflection can throw
            // anything, but the constructor being called is owned by the Java SDKs which won't throw Throwable. So,
            // only Error needs to be inspected and handled specially, otherwise it can be assumed the Throwable is
            // a type of Exception which can be thrown based on this method having Exception checked.
            if (throwable instanceof Error) {
                throw (Error) throwable;
            } else {
                throw (Exception) throwable;
            }
        }
    }

    @SuppressWarnings("removal")
    private static MethodHandles.Lookup performSafePrivateLookupIn(Class<?> targetClass) throws Throwable {
        // MethodHandles::privateLookupIn() throws SecurityException if denied by the security manager
        if (System.getSecurityManager() == null) {
            return (MethodHandles.Lookup) METHOD_HANDLES_PRIVATE_LOOKUP_IN_METHOD_HANDLE
                .invokeExact(targetClass, LOOKUP);
        } else {
            return java.security.AccessController.doPrivileged((PrivilegedExceptionAction<MethodHandles.Lookup>) () -> {
                try {
                    return (MethodHandles.Lookup) METHOD_HANDLES_PRIVATE_LOOKUP_IN_METHOD_HANDLE
                        .invokeExact(targetClass, LOOKUP);
                } catch (Throwable throwable) {
                    if (throwable instanceof Error) {
                        throw (Error) throwable;
                    } else {
                        throw (Exception) throwable;
                    }
                }
            });
        }
    }

    public static boolean isModuleBased() {
        return MODULE_BASED;
    }

    AzureReflectionUtils() {
    }
}
