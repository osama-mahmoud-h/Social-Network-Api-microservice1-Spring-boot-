package com.app.auth.config.swagger;

import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import jakarta.validation.constraints.*;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.HandlerMethod;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Auto-generates 400 validation error examples in Swagger UI by reading Jakarta
 * validation annotations from the {@code @RequestBody} DTO class.
 * Example values match the shape of {@code MyApiResponse} returned by
 * {@code GlobalExceptionHandler#handleValidationExceptions}.
 * <p>
 * Only activates on methods annotated with {@link AutoValidationExamples}.
 */
@Component
public class ValidationExampleCustomizer implements OperationCustomizer {

    private record FieldValidation(String fieldName, String message) {}

    @Override
    public Operation customize(Operation operation, HandlerMethod handlerMethod) {
        if (!handlerMethod.hasMethodAnnotation(AutoValidationExamples.class)) {
            return operation;
        }

        Class<?> dtoClass = findRequestBodyType(handlerMethod);
        if (dtoClass == null) {
            return operation;
        }

        Map<String, FieldValidation> violations = extractViolations(dtoClass);
        if (violations.isEmpty()) {
            return operation;
        }

        apply400Examples(operation, violations);
        return operation;
    }

    private Class<?> findRequestBodyType(HandlerMethod handlerMethod) {
        for (java.lang.reflect.Parameter param : handlerMethod.getMethod().getParameters()) {
            if (param.isAnnotationPresent(org.springframework.web.bind.annotation.RequestBody.class)) {
                return param.getType();
            }
        }
        return null;
    }

    private Map<String, FieldValidation> extractViolations(Class<?> dtoClass) {
        Map<String, FieldValidation> result = new LinkedHashMap<>();
        Class<?> current = dtoClass;
        while (current != null && current != Object.class) {
            for (Field field : current.getDeclaredFields()) {
                for (Annotation annotation : field.getAnnotations()) {
                    String message = resolveMessage(annotation);
                    if (message != null) {
                        String exampleName = toExampleName(field.getName(), annotation);
                        result.putIfAbsent(exampleName, new FieldValidation(field.getName(), message));
                    }
                }
            }
            current = current.getSuperclass();
        }
        return result;
    }

    private String resolveMessage(Annotation annotation) {
        if (annotation instanceof NotBlank a)      return resolve(a.message(), "must not be blank");
        if (annotation instanceof NotNull a)       return resolve(a.message(), "must not be null");
        if (annotation instanceof NotEmpty a)      return resolve(a.message(), "must not be empty");
        if (annotation instanceof Size a)          return resolve(a.message(), "size must be between " + a.min() + " and " + a.max());
        if (annotation instanceof Min a)           return resolve(a.message(), "must be at least " + a.value());
        if (annotation instanceof Max a)           return resolve(a.message(), "must be at most " + a.value());
        if (annotation instanceof DecimalMin a)    return resolve(a.message(), "must be >= " + a.value());
        if (annotation instanceof DecimalMax a)    return resolve(a.message(), "must be <= " + a.value());
        if (annotation instanceof Positive a)      return resolve(a.message(), "must be positive");
        if (annotation instanceof PositiveOrZero a)return resolve(a.message(), "must be zero or positive");
        if (annotation instanceof Negative a)      return resolve(a.message(), "must be negative");
        if (annotation instanceof NegativeOrZero a)return resolve(a.message(), "must be zero or negative");
        if (annotation instanceof Email a)         return resolve(a.message(), "must be a valid email address");
        if (annotation instanceof Pattern a)       return resolve(a.message(), "must match pattern: " + a.regexp());
        if (annotation instanceof Digits a)        return resolve(a.message(), "numeric value out of bounds");
        return null;
    }

    private String resolve(String message, String fallback) {
        return (message == null || message.startsWith("{")) ? fallback : message;
    }

    private String toExampleName(String fieldName, Annotation annotation) {
        String readable = camelToReadable(fieldName);
        return switch (annotation.annotationType().getSimpleName()) {
            case "NotBlank", "NotNull", "NotEmpty" -> readable + " required";
            case "Size"                            -> readable + " invalid size";
            case "Min", "DecimalMin"               -> readable + " below minimum";
            case "Max", "DecimalMax"               -> readable + " above maximum";
            case "Positive"                        -> readable + " must be positive";
            case "PositiveOrZero"                  -> readable + " must be zero or positive";
            case "Email"                           -> readable + " invalid format";
            case "Pattern"                         -> readable + " invalid pattern";
            case "Digits"                          -> readable + " invalid digits";
            default                                -> readable + " invalid";
        };
    }

    private String camelToReadable(String camelCase) {
        StringBuilder sb = new StringBuilder();
        for (char c : camelCase.toCharArray()) {
            if (Character.isUpperCase(c) && !sb.isEmpty()) sb.append(' ');
            sb.append(sb.isEmpty() ? Character.toUpperCase(c) : c);
        }
        return sb.toString();
    }

    /**
     * Builds 400 examples whose shape matches {@code MyApiResponse} as returned by
     * {@code GlobalExceptionHandler#handleValidationExceptions}:
     * <pre>
     * {
     *   "success": false,
     *   "message": "Validation failed",
     *   "timestamp": "...",
     *   "error": {
     *     "code": "VALIDATION_ERROR",
     *     "details": "One or more fields have validation errors",
     *     "validationErrors": [{ "field": "...", "message": "..." }]
     *   }
     * }
     * </pre>
     */
    private void apply400Examples(Operation operation, Map<String, FieldValidation> violations) {
        if (operation.getResponses() == null) {
            operation.setResponses(new ApiResponses());
        }

        ApiResponse response400 = operation.getResponses().get("400");

        if (response400 != null && response400.getContent() != null) {
            MediaType existing = response400.getContent().get("application/json");
            if (existing != null && existing.getExamples() != null && !existing.getExamples().isEmpty()) {
                return; // preserve manually-defined examples
            }
        }

        if (response400 == null) {
            response400 = new ApiResponse().description("Validation failed");
            operation.getResponses().addApiResponse("400", response400);
        }

        Map<String, Example> exampleMap = new LinkedHashMap<>();
        for (Map.Entry<String, FieldValidation> entry : violations.entrySet()) {
            FieldValidation v = entry.getValue();
            Example example = new Example();
            example.setValue(Map.of(
                    "success", false,
                    "message", "Validation failed",
                    "timestamp", "2026-01-01T10:00:00",
                    "error", Map.of(
                            "code", "VALIDATION_ERROR",
                            "details", "One or more fields have validation errors",
                            "validationErrors", List.of(Map.of("field", v.fieldName(), "message", v.message()))
                    )
            ));
            exampleMap.put(entry.getKey(), example);
        }

        MediaType mediaType = new MediaType();
        mediaType.setExamples(exampleMap);

        Content content = new Content();
        content.addMediaType("application/json", mediaType);
        response400.setContent(content);
    }
}
