# 🅺 KMP Playground

[![GitHub Workflow Status][gha_badge]][gha_url]

A Kotlin Multiplatform playground powered by the [Kotlin Toolchain](https://kotlin-toolchain.org/dev/).

### Usage

```bash
# Update the Kotlin CLI
$ ./kotlin update --dev

# Build the app and create an executable jar
$ ./kotlin build [-v release]
$ ./kotlin package

# Run the tests
$ ./kotlin test

# Run the app
$ ./kotlin run -m app \
              --jvm-args="--enable-preview --add-modules=jdk.incubator.vector --enable-native-access=ALL-UNNAMED"
$ ./kotlin run -m macos --platform macosArm64

# JDK Incubator modules
$ ./kotlin run -m app \
              --jvm-args="--enable-preview --add-modules=jdk.incubator.vector --enable-native-access=ALL-UNNAMED" \
              --main-class=AppKt

# Dependency insights
$ ./kotlin show dependencies -m app --scope=runtime --filter=org.jetbrains.kotlin:kotlin-stdlib

# Publish to mavenLocal
$ ./kotlin publish mavenLocal

# Checks and Custom commands
$ ./kotlin check -m ktor [graalVMCheck]
$ ./kotlin do graalVMCheck

# List all the binaries
$ find . \( -path "*/build/*" -type f -perm +111 -o -path "*/build/*executableJar*/*.jar" \) | grep -v -E "(test|debug|dSYM)" | xargs du -h | sort -hr
$ find . \( -path "*/build/*" -perm +111 -o -path "*/build/tasks/*executableJar*/*.jar" \) -type f -ls | awk '{printf "%.3fM %s\n",$7/1048576,$NF}' | sort -rn

```

### Run Binaries

#### ☕ JVM

```bash
$ java --enable-preview \
       --add-modules=jdk.incubator.vector \
       --enable-native-access=ALL-UNNAMED \
       -jar build/tasks/_app_executableJarJvm/app-jvm-executable.jar
```

#### 🍎 macOS

```bash
$ build/tasks/_macos_linkMacosArm64Release/macos.kexe

# Debug
$ log stream --debug --predicate "process == 'macos.kexe' AND senderImagePath ENDSWITH 'macos.kexe'"
$ otool -L build/tasks/_macos_linkMacosArm64Release/macos.kexe
```

#### 🐧 Linux

```bash
# Debian slim (can use ubuntu:latest also)
$ docker run -it --rm \
         --platform=linux/amd64 \
         --pull always \
         --mount type=bind,source=$(pwd),destination=/app,readonly \
         debian:stable-slim
  # apt update && apt install -y ca-certificates libtree
  # /app/build/tasks/_linux_linkLinuxX64Release/linux.kexe
  # libtree -v /app/build/tasks/_linux_linkLinuxX64Release/linux.kexe

# Distroless (cc - base + libgcc1)
$ docker run -it --rm \
         --platform=linux/amd64 \
         --pull always \
         -v "$PWD":/app \
         --entrypoint=/app/build/tasks/_linux_linkLinuxX64Release/linux.kexe \
         gcr.io/distroless/cc
```

#### 🪟 Windows

```bash
# Wine via Docker
$ docker run -it --rm \
         --platform="linux/amd64" \
         --pull always \
         -e DISPLAY=host.docker.internal:0 \
         -v "$PWD":/app \
         scottyhardy/docker-wine:stable-10.0 wine /app/build/tasks/_windows_linkMingwX64Release/windows.exe
```

### GraalVM Native Image

Build a native executable for the Ktor application using the `native-image` plugin.

```bash
# Install GraalVM CE (via SDKMAN)
$ sdk i java 25.0.2-graalce

# Build the native image
$ ./kotlin check -m ktor graalVMCheck
$ ./kotlin task :ktor:buildNativeImage@native-image

# Run the native image
$ ./build/tasks/_ktor_buildNativeImage@native-image/ktor
```

### Self-Contained Binaries

Package executable JARs with [jbundle](https://github.com/avelino/jbundle) to create optimized self-contained native
bundles.

```bash
# Extract JAR contents
$ jar -xf build/tasks/_ktor_executableJarJvm/ktor-jvm-executable.jar

# Note: jarmode layertools not currently supported for Kotlin Toolchain JARs yet
# $ java -Djarmode=layertools -jar build/tasks/_ktor_executableJarJvm/ktor-jvm-executable.jar extract

# Detect required Java modules and build bundle
$ MODULES=$(jdeps -q -R --ignore-missing-deps --print-module-deps --multi-release=25 --class-path "BOOT-INF/lib/*" BOOT-INF/classes)

# Build native bundle (download jbundle from https://github.com/avelino/jbundle/releases/tag/latest)
$ brew tap avelino/jbundle
$ brew install jbundle
$ jbundle build \
      --input build/tasks/_ktor_executableJarJvm/ktor-jvm-executable.jar \
      --jvm-args="--enable-preview" \
      --modules $MODULES \
      --compact-banner \
      --output ktor-app  
```

> [!TIP]
> You can find all the Kotlin CLI dev versions
> [here](https://packages.jetbrains.team/maven/p/amper/amper/org/jetbrains/kotlin/kotlin-cli/).

### Config Settings

| Configuration     | 📝 Description                                                                                                                                    | 🎯 Applies To                   |
|-------------------|---------------------------------------------------------------------------------------------------------------------------------------------------|---------------------------------|
| `settings.kotlin` | Settings for the Kotlin compiler, thus only apply to Kotlin sources                                                                               | 🟣 Kotlin sources               |
| `settings.java`   | Settings for the Java compiler, thus only apply to Java sources                                                                                   | ☕ Java sources                  |
| `settings.jvm`    | Settings that apply to both Java and Kotlin sources (some common compiler options, settings related to the JDK in general, to the test JVM, etc.) | 🔄 Both Java and Kotlin sources |

<!-- Badges -->

[gha_url]: https://github.com/sureshg/kmp-play/actions/workflows/build.yaml

[gha_badge]: https://img.shields.io/github/actions/workflow/status/sureshg/kmp-play/build.yaml?branch=main&style=flat&logo=kotlin&logoColor=white&label=Kotlin%20Build
