package net.siudek.media;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface LocalTest {
}
