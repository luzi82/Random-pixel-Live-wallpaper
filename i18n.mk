NDK_PATH=/home/luzi82/project/android/software/android-ndk-r3

.PHONY : all

all : .i18n_timpstamp librandompixel.so

.i18n_timpstamp : i18n.ods
	ods2xml.sh
	cp res/values-zh-rHK/strings.xml res/values-zh-rMO
	cp res/values-zh-rHK/strings.xml res/values-zh-rTW
	touch .i18n_timpstamp

librandompixel.so : jni/randompixel.c
	jni_version.sh
	cd ${NDK_PATH};make APP=randompixel
	cp libs/armeabi/librandompixel.so ./
