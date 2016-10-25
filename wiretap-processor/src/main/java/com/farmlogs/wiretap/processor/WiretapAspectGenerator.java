package com.farmlogs.wiretap.processor;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import org.aspectj.lang.ProceedingJoinPoint;

import javax.lang.model.element.Modifier;

/**
 * @author Damian Wieczorek {@literal <damian@farmlogs.com>}
 * @since 10/25/16
 * (C) 2016
 */
final class WiretapAspectGenerator {

    private static final ClassName CLASS_POINTCUT = ClassName.get("org.aspectj.lang.annotation", "Pointcut");
    private static final ClassName CLASS_ASPECT = ClassName.get("org.aspectj.lang.annotation", "Aspect");
    private static final ClassName CLASS_AROUND = ClassName.get("org.aspectj.lang.annotation", "Around");
    private static final ClassName CLASS_RUNTIME = ClassName.get("com.farmlogs.wiretap.runtime", "$$WiretapRuntimeCommon");

    private WiretapAspectGenerator() {
        throw new UnsupportedOperationException("No instances!");
    }

    private static String generateClassName(final String annotationName) {
        final String[] split = annotationName.split("\\.");
        final String simpleName = split[split.length - 1];
        return "$$WiretapAspect_" + simpleName;
    }

    static TypeSpec generateAspect(final String annotationName) {
        final TypeSpec.Builder builder = TypeSpec.classBuilder(generateClassName(annotationName))
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(CLASS_ASPECT);

        for (PointcutDeclaration pointcut : PointcutDeclaration.values()) {
            builder.addMethod(pointcut.generateMethod(annotationName));
        }

        for (AroundAdvice advice : AroundAdvice.values()) {
            builder.addMethod(advice.generateMethod());
        }

        return builder.build();
    }

    private enum PointcutDeclaration {
        WITHIN_ANNOTATED_CLASS("withinAnnotatedClass", "within(@%s *)", true),
        METHOD_INSIDE_ANNOTATED_TYPE("methodInsideAnnotatedType", "execution(!synthetic * *(..)) && withinAnnotatedClass()", false),
        CONSTRUCTOR_INSIDE_ANNOTATED_TYPE("constructorInsideAnnotatedType", "execution(!synthetic *.new(..)) && withinAnnotatedClass()", false),
        METHOD("method", "execution(@%s * *(..)) || methodInsideAnnotatedType()", true),
        CONSTRUCTOR("constructor", "execution(@com.farmlogs.wiretap.Tap *.new(..)) || constructorInsideAnnotatedType()", true);

        final String label;
        private final String expression;
        private final boolean formatted;

        private String generateExpression(final String annotationName) {
            if (formatted) {
                return String.format(expression, annotationName);
            }
            return expression;
        }

        private AnnotationSpec generateAnnotation(final String annotationName) {
            return AnnotationSpec.builder(CLASS_POINTCUT)
                    .addMember("value", "$S", generateExpression(annotationName))
                    .build();
        }

        MethodSpec generateMethod(final String annotationName) {
            return MethodSpec.methodBuilder(label)
                    .addModifiers(Modifier.PUBLIC)
                    .addAnnotation(generateAnnotation(annotationName))
                    .build();
        }

        PointcutDeclaration(final String label, final String expression, final boolean formatted) {
            this.label = label;
            this.expression = expression;
            this.formatted = formatted;
        }
    }

    private enum AroundAdvice {
        METHOD("executeMethodAndNotify", "method()"),
        CONSTRUCTOR("executeConstructorAndNotify", "constructor()");

        final String label;
        final String pointcut;
        final AnnotationSpec annotationSpec;

        MethodSpec generateMethod() {
            return MethodSpec.methodBuilder(label)
                    .addModifiers(Modifier.PUBLIC)
                    .addAnnotation(annotationSpec)
                    .returns(Object.class)
                    .addException(Throwable.class)
                    .addParameter(ProceedingJoinPoint.class, "joinPoint")
                    .addStatement("return $T.$L(joinPoint)", CLASS_RUNTIME, label)
                    .build();

        }

        AroundAdvice(final String label, final String pointcut) {
            this.label = label;
            this.pointcut = pointcut;
            this.annotationSpec = AnnotationSpec.builder(CLASS_AROUND)
                    .addMember("value", "$S", pointcut)
                    .build();
        }
    }

}
