#include <jni.h>
#include <stdlib.h>

jlong seed = 0;
jint intSize = 0;
jint byteSize = 0;
jint* buf = NULL;
jint* bufEnd = NULL;
jlong multiplier = 0x5deece66dLL;

void clean() {
	free(buf);
	buf = NULL;
	bufEnd = NULL;
	intSize = 0;
	byteSize = 0;
}

JNIEXPORT void JNICALL JNI_OnUnload(JavaVM *jvm, void *reserved) {
	clean();
}

JNIEXPORT void JNICALL Java_com_luzi82_randomwallpaper_LiveWallpaper_setSeed(JNIEnv* env,
		jclass cls, jlong _seed) {
	seed = _seed;
}

JNIEXPORT void JNICALL Java_com_luzi82_randomwallpaper_LiveWallpaper_setSize(JNIEnv* env,
		jclass cls, jint _imgSize) {
	if(_imgSize!=intSize)
	{
		clean();
		intSize = _imgSize;
		byteSize = intSize*sizeof(jint);
		buf=(jint*)malloc(byteSize);
		bufEnd=buf+intSize;
	}
}

JNIEXPORT void JNICALL Java_com_luzi82_randomwallpaper_LiveWallpaper_genRandom(JNIEnv* env,
		jclass cls, jbyteArray out) {
	static jint*ptr;
	if(buf) {
		ptr=buf;
		while(ptr!=bufEnd){
			seed=(seed*multiplier+0xbLL)&((1LL<<48)-1);
			*ptr=(int)((seed>>(48-32))|0xff000000);
			ptr++;
		}
		(*env)->SetByteArrayRegion(env,out,0,byteSize,(jbyte*)buf);
	}
}
