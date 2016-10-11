package com.farmlogs.wiretap.weaving.plugin

import com.android.build.api.transform.*
import com.android.build.gradle.api.BaseVariant
import com.android.utils.Pair
import com.google.common.collect.ImmutableMap
import groovy.transform.CompileStatic
import org.apache.commons.io.FileUtils
import org.gradle.api.Project
import org.gradle.api.ProjectConfigurationException
import org.gradle.api.file.FileCollection
import org.gradle.api.internal.file.collections.SimpleFileCollection
import org.gradle.api.tasks.compile.JavaCompile

@CompileStatic
class WiretapTransform extends Transform {

    private final Project project
    private final boolean enabled

    private final Map<Pair<String, String>, JavaCompile> javaCompileTasks = new HashMap<>()

    public WiretapTransform(final Project project, final boolean enabled) {
        this.project = project
        this.enabled = enabled
    }

    /**
     * We need to set this later because the classpath is not fully calculated until the last
     * possible moment when the java compile task runs. While a Transform currently doesn't have any
     * variant information, we can guess the variant based off the input path.
     */
    public void putVariant(final BaseVariant variant) {
        javaCompileTasks.put(Pair.of(variant.flavorName, variant.buildType.name), variant.javaCompile)
    }

    @Override
    void transform(final Context context,
                   final Collection<TransformInput> inputs,
                   final Collection<TransformInput> referencedInputs,
                   final TransformOutputProvider outputProvider,
                   final boolean isIncremental) throws IOException, TransformException, InterruptedException {
        println("Compile tasks: ")
        println(javaCompileTasks)

        inputs.each { TransformInput input ->
            def outputDir = outputProvider.getContentLocation("wiretap", outputTypes, scopes, Format.DIRECTORY)

            input.directoryInputs.each { DirectoryInput directoryInput ->
                final File inputFile = directoryInput.file

                // All classes need to be copied regardless for some reason. So if we want to
                // disable wiretap simply copy files.
                if (!enabled) {
                    FileUtils.copyDirectory(inputFile,outputDir)
                    return
                }

                String inputDirs
                if (isIncremental) {
                    final FileCollection changed = new SimpleFileCollection(project.files().asList())
                    directoryInput.changedFiles.each { File file, Status status ->
                        if (status == Status.ADDED || status == Status.CHANGED) {
                            changed += project.files(file.parent)
                        }
                    }
                    inputDirs = changed.asPath
                } else {
                    inputDirs = inputFile.path
                }

                final JavaCompile javaCompile = getJavaCompile(inputFile)
                final FileCollection bootClassPath = getBootClassPath(javaCompile)

                def exec = new WiretapExec(project)
                exec.inpath = inputDirs
                exec.aspectpath = javaCompile.classpath.asPath
                exec.destinationpath = outputDir
                exec.classpath = javaCompile.classpath.asPath
                exec.bootclasspath = bootClassPath.asPath
                exec.exec()
            }
        }
    }

    private FileCollection getBootClassPath(final JavaCompile javaCompile) {
        def bootClasspath = javaCompile.options.bootClasspath
        if (bootClasspath) {
            return project.files(bootClasspath.tokenize(File.pathSeparator))
        } else {
            // If this is null it means the javaCompile task didn't need to run, however, we still
            // need to run but can't without the bootClasspath. Just fail and ask the user to rebuild.
            throw new ProjectConfigurationException("Unable to obtain the bootClasspath. This may happen if your javaCompile tasks didn't run but retrolambda did. You must rebuild your project or otherwise force javaCompile to run.", null)
        }
    }

    private JavaCompile getJavaCompile(File inputFile) {
        String buildName = inputFile.name
        String flavorName = inputFile.parentFile.name

        // If either one starts with a number or is 'folders', it's probably the result of a transform, keep moving
        // up the dir structure until we find the right folders.
        // Yes I know this is bad, but hopefully per-variant transforms will land soon.
        File current = inputFile
        while (Character.isDigit(buildName.charAt(0)) || Character.isDigit(flavorName.charAt(0)) || buildName.equals("folders") || flavorName.equals("folders")) {
            current = current.parentFile
            buildName = current.name
            flavorName = current.parentFile.name
        }

        def javaCompileTask = javaCompileTasks.get(Pair.of(flavorName, buildName))
        if (javaCompileTask == null) {
            // Flavor might not exist
            javaCompileTask = javaCompileTasks.get(Pair.of("", buildName))
        }

        return javaCompileTask;
    }

    @Override
    public String getName() {
        return "wiretap"
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return Collections.singleton(QualifiedContent.DefaultContentType.CLASSES)
    }

    @Override
    Set<QualifiedContent.Scope> getScopes() {
        return Collections.singleton(QualifiedContent.Scope.PROJECT)
    }

    @Override
    Set<QualifiedContent.Scope> getReferencedScopes() {
        return Collections.singleton(QualifiedContent.Scope.PROJECT)
    }

    @Override
    public Map<String, Object> getParameterInputs() {
        return ImmutableMap.<String, Object> builder()
                .put("enabled", enabled)
                .build();
    }

    @Override
    public boolean isIncremental() {
        return true
    }

}
