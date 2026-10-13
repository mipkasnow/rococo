package mipkasnow.dev.rococo.jupiter.annotation;

import mipkasnow.dev.rococo.jupiter.extension.MuseumExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@ExtendWith(MuseumExtension.class)
public @interface Museum {

    String title() default "";

    String description() default "";

    String city() default "";

    String country() default "";
}
