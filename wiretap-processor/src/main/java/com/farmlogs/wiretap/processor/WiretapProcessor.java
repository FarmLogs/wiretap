package com.farmlogs.wiretap.processor;

import com.farmlogs.wiretap.TapAnnotation;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

/**
 * @author Damian Wieczorek {@literal <damian@farmlogs.com>}
 * @since 10/25/16
 * (C) 2016
 */
@AutoService(Processor.class)
@SupportedAnnotationTypes("com.farmlogs.wiretap.TapAnnotation")
public class WiretapProcessor extends AbstractProcessor {

    private boolean built = false;

    @Override
    public boolean process(final Set<? extends TypeElement> annotations,
                           final RoundEnvironment roundEnv) {
        if (built) {
            return true;
        }

        final Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(TapAnnotation.class);

        for (Element element : elements) {
            if (!(element instanceof TypeElement)) {
                final Messager messager = processingEnv.getMessager();
                messager.printMessage(Diagnostic.Kind.WARNING, "Element is not a type", element);
                continue;
            }
            generateAspect((TypeElement) element);
        }

        built = true;
        return true;
    }

    private void generateAspect(final TypeElement element) {
        final String annotationName = element.getQualifiedName().toString();
        final TypeSpec aspectSpec = WiretapAspectGenerator.generateAspect(annotationName);
        final JavaFile file = JavaFile.builder("com.farmlogs.wiretap.runtime", aspectSpec).build();
        try {
            file.writeTo(processingEnv.getFiler());
        } catch (IOException e) {
            final Messager messager = processingEnv.getMessager();
            messager.printMessage(Diagnostic.Kind.ERROR, "Generating aspect failed", element);
        }
    }

}
