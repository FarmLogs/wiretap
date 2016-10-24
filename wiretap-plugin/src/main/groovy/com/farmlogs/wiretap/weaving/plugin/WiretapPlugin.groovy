package com.farmlogs.wiretap.weaving.plugin

import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.LibraryPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project

class WiretapPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        def hasApp = project.plugins.withType(AppPlugin)
        def hasLib = project.plugins.withType(LibraryPlugin)
        if (!hasApp && !hasLib) {
            throw new IllegalStateException("'android' or 'android-library' plugin required.")
        }

        final def transform = new WiretapTransform(project)

        if (hasLib) {
            final def android = project.extensions.getByType(LibraryExtension)
            android.registerTransform(transform)
            android.libraryVariants.all(transform.&putVariant)
        } else {
            final def android = project.extensions.getByType(AppExtension)
            android.registerTransform(transform)
            android.applicationVariants.all(transform.&putVariant)
        }

        project.dependencies {
            compile 'com.farmlogs.wiretap:wiretap-runtime:0.1-SNAPSHOT'
            // TODO this should come transitively
            compile 'org.aspectj:aspectjrt:1.8.9'
            compile 'com.farmlogs.wiretap:wiretap-annotations:0.1-SNAPSHOT'
            compile 'com.farmlogs.wiretap:wiretap-library:0.1-SNAPSHOT'
        }

        project.extensions.create('wiretap', WiretapExtension)


    }

}
