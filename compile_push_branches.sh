#!/bin/sh
git checkout mc-1.8 && ./gradlew clean build \
&& git checkout mc-1.14 && ./gradlew clean build \
&& git checkout mc-1.15 && ./gradlew clean build \
&& git checkout mc-1.16 && ./gradlew clean build \
&& git push origin mc-1.8 mc-1.14 mc-1.15 mc-1.16