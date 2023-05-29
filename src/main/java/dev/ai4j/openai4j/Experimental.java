package dev.ai4j.openai4j;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * This annotation indicates that the annotated method, class, or field is experimental and may change in future versions.
 */
@Documented
@Retention(RUNTIME)
public @interface Experimental {
}