Wiretap
=======

Annotation-triggered method call listeners for Android, based on [Jake Wharton's](https://github.com/JakeWharton) awesome [Hugo](https://github.com/JakeWharton/hugo) library.

[Hugo](https://github.com/JakeWharton/hugo) is a wonderful tool for tracking calls during debugging,
but sometimes you want more options for tapping into method calls. For example, you may want to
trigger other side effects when certain events happen in your app:
  * When a user clicks this button, track it with a metrics event
  * Debug logging (like Hugo), but synced remotely for internal builds
  * etc
  
Generally, you want to be able to do this without cluttering up your code and minimize runtime set-up.
Compile-time tools are getting easier to use, but are still out of the reach of many developers: they
still aren't "easy" to use and take time to set up. Wiretap attempts to provide a general solution
for listening to calls at runtime. It also works with Kotlin!

Set-up is two steps:

1. Include and apply the plugin:

```Groovy
buildscript {
  dependencies {
    classpath "com.farmlogs.wiretap:wiretap-plugin:0.1-SNAPSHOT"
  }
}

apply plugin: 'wiretap'

wiretap {
  enabled = true
}
```

2. Implement [WiretapListener](wiretap-library/src/main/java/com/farmlogs/wiretap/runtime/WiretapListener.java) and tell Wiretap about it.
   You will probably want to do this in your `Application`'s `onCreate` or `attachBaseContext`.

```java
@Override public void onCreate() {
  super.onCreate();
  Wiretap.setMethodCallListener(new MyFancyListener());
}
```

You're ready to go! Whenever you annotate a method or constructor with `@Tap`, your listener will be called.
You can also annotate entire classes with `@Tap` to enable Wiretap on all methods.

```Java
@Tap
void myMethod() {
  ...
}
```

For more information, see [the example project](wiretap-example), which
implements `WiretapListener` [as a debug logger](wiretap-example/src/main/java/com/example/wiretap/LoggingListener.java).

Custom Annotations
------------------

You can also trigger wiretap with your own annotations. For example, you might have a `@FancyLog("fancy option")`
annotation. Instead of having to include the extra `@Tap` annotation at each site, you can annotate `FancyLog`
with `@TapAnnotation`:

```Java
@TapAnnotation
public @interface FancyLog {
  String value();
}
```

In order for these to take effect, you also need to run the `wiretap-processor` with android-apt (or `kapt` if you are using Kotlin):

```Groovy
dependencies {
  apt 'com.farmlogs.wiretap:wiretap-processor:0.1-SNAPSHOT'
}
```

Runtime Impact
--------------

Annotated methods will incur a small performance hit. Under the hood, Wiretap uses [AspectJ](http://www.eclipse.org/aspectj/) which does not affect performance too much,
but also uses a couple reflection calls at runtime to obtain the instance of the `Method` to pass to you, the listener.
That is ultimately the cost of such a general approach. In the future, `Wiretap` may support "cheaper" alternative.

If you want better performance, check out the Transform API and AspectJ, where you'll be able to inject your own code directly :).

For most uses (debug logging, occasional metrics events, etc), Wiretap performance should be sufficient (especially if you are only using it in debug builds).


Wiretap is still in development!
--------------------------------

There is no stable release yet. If you want to try it out, version `0.1-SNAPSHOT` is available. Note however that APIs may change unexpectedly in future versions.


Local Development
-----------------

Working on this project? Here's some helpful Gradle tasks:

 * `install` - Install plugin, runtime, and annotations into local repo.
 * `cleanExample` - Clean the example project build.
 * `assembleExample` - Build the example project. Must run `install` first.
 * `installExample` - Build and install the example project debug APK onto a device.


License
--------

    Copyright 2016 Agrisight, Inc

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
