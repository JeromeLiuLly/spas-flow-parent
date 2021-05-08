package com.candao.spas.flow.mysql.annotation;

import org.springframework.stereotype.Indexed;
import java.lang.annotation.*;
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Indexed
public @interface RouteSelector {
	String value();
}