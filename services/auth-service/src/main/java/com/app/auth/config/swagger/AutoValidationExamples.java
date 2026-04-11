package com.app.auth.config.swagger;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marker annotation for controller methods.
 * When present, the {@link ValidationExampleCustomizer} will auto-generate
 * 400 error examples in Swagger from the {@code @RequestBody} DTO's Jakarta validation annotations.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoValidationExamples {
}
