package io.moneyinthesky.dashboard.core.app.guice;

import com.google.inject.BindingAnnotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@BindingAnnotation
@Target({ PARAMETER })
@Retention(RUNTIME)
public @interface ForkJoinPoolSize {
}
