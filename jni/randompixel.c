#include <jni.h>
#include <stdlib.h>
#include "version.h"

const jlong MULTIPLIER = 0x5deece66dLL;
jlong seed = 0;

JNIEXPORT void JNICALL Java_com_luzi82_randomwallpaper_LiveWallpaper_getVersion(JNIEnv* env,
		jclass cls, jbyteArray out) {
	jbyte*buf=((*env)->GetByteArrayElements(env,out,NULL));
	if(buf){
		memcpy(buf,__AUTO_VERSION__,sizeof(__AUTO_VERSION__));
		(*env)->ReleaseByteArrayElements(env,out,buf,0);
	}
}

JNIEXPORT void JNICALL Java_com_luzi82_randomwallpaper_LiveWallpaper_setSeed(JNIEnv* env,
		jclass cls, jlong _seed) {
	seed = _seed;
}

JNIEXPORT void JNICALL Java_com_luzi82_randomwallpaper_LiveWallpaper_genRandom(JNIEnv* env,
		jclass cls, jbyteArray out) {
	static jint*ptr,*buf2,*bufEnd;
	static jsize len;
	buf2=(jint*)((*env)->GetByteArrayElements(env,out,NULL));
	if(buf2) {
		len=(*env)->GetArrayLength(env,out);
		ptr=buf2;
		bufEnd=buf2+(len>>2);
		while(ptr!=bufEnd) {
			seed=(seed*MULTIPLIER+0xbLL)&((1LL<<48)-1);
			*ptr=((int)(seed>>(48-32)))|0xff000000;
			++ptr;
		}
		(*env)->ReleaseByteArrayElements(env,out,(jbyte*)buf2,0);
	}
}
