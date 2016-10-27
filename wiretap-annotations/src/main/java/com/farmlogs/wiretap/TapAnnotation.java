package com.farmlogs.wiretap;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Damian Wieczorek {@literal <damian@farmlogs.com>}
 * @since 10/25/16
 * (C) 2016
 */
@Target(ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface TapAnnotation {
}
