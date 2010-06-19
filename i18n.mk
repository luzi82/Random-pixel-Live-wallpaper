NDK_PATH=/home/luzi82/project/android/software/android-ndk-r3
ANDROIDKIT_PATH=/home/luzi82/project/android/tool/android_kit

.PHONY : all clean

all : .i18n_timpstamp librandompixel.so

.i18n_timpstamp : i18n.ods
	${ANDROIDKIT_PATH}/ods2xml.sh
	-mkdir res/values-zh-rMO/
	-mkdir res/values-zh-rTW/
	cp res/values-zh-rHK/strings.xml res/values-zh-rMO
	cp res/values-zh-rHK/strings.xml res/values-zh-rTW
	touch .i18n_timpstamp

librandompixel.so : jni/randompixel.c jni/Android.mk ${NDK_PATH}/apps/randompixel
	${ANDROIDKIT_PATH}/jni_version.sh
	cd ${NDK_PATH};make APP=randompixel
	cp libs/armeabi/librandompixel.so ./

${NDK_PATH}/apps/randompixel :
	ln -s ${PWD}/ndkapps ${NDK_PATH}/apps/randompixel

clean :
	-rm jni/version.h
	-rm librandompixel.so
	-rm libs/armeabi/librandompixel.so
	-rm .i18n_timpstamp
