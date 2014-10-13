#!/bin/sh

# Compile java
if [ ! -d obj ]; then
	mkdir obj
fi	
javac -d obj -bootclasspath $ANDROID_HOME/sdk/platforms/android-19/android.jar -classpath $ANDROID_HOME/sdk/extras/android/support/v4/android-support-v4.jar src/com/imaginea/instrumentation/*.java

# Convert to dex
if [ ! -d bin ]; then
	mkdir bin
fi
$ANDROID_HOME/sdk/build-tools/android-4.4.2/dx --dex --output=bin/classes.dex obj/

# Extract smali
INSTRUMENTATION_PATH=../android-tool/libs/InstrumentationPackage
java -jar $INSTRUMENTATION_PATH/baksmali-2.0.3.jar bin/classes.dex -o bin/smali

# Copy smali
cp bin/smali/com/imaginea/instrumentation/*.smali $INSTRUMENTATION_PATH/wrapper/com/imaginea/instrumentation/