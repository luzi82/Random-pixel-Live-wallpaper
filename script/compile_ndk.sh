#!/bin/bash

pushd /home/luzi82/project/android/software/android-ndk-r3
make APP=randompixel
popd

cp libs/armeabi/*.so ./
