package ai.chat2db.server.domain.core.cache;

import java.lang.reflect.Field;

import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.lang.NonNull;

import ai.chat2db.spi.model.LuceneField;

/**
 * AOT RuntimeHints 注册器
 * 为 GraalVM 原生镜像编译注册反射元数据
 * 确保 @LuceneField 注解字段在 AOT 编译时可访问
 */
public class LuceneFieldRuntimeHints implements RuntimeHintsRegistrar {

    @Override
    public void registerHints(@NonNull RuntimeHints hints, ClassLoader classLoader) {
        hints.reflection()
            .registerType(LuceneFieldHintProcessor.class, MemberCategory.INVOKE_DECLARED_METHODS);
    }

    /**
     * 提示处理器：在运行时动态注册带 @LuceneField 注解的类
     * 实际使用中，需要扫描所有带 @LuceneField 注解的实体类并注册
     */
    static class LuceneFieldHintProcessor {

        /**
         * 注册带 @LuceneField 注解的类及其字段反射权限
         * 应在应用启动时调用此方法
         *
         * @param hints RuntimeHints 实例
         * @param classes 需要注册的实体类
         */
        @SafeVarargs
        public static void registerClasses(RuntimeHints hints, Class<?>... classes) {
            for (Class<?> clazz : classes) {
                hints.reflection().registerType(clazz,
                    MemberCategory.INVOKE_DECLARED_CONSTRUCTORS,
                    MemberCategory.DECLARED_FIELDS,
                    MemberCategory.INVOKE_DECLARED_METHODS);

                for (Field field : clazz.getDeclaredFields()) {
                    if (field.isAnnotationPresent(LuceneField.class)) {
                        hints.reflection().registerField(field);
                    }
                }
            }
        }
    }
}
