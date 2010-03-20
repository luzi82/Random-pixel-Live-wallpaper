#include <jni.h>
#include <stdlib.h>

jlong multiplier = 0x5deece66dLL;
jlong seed = 0;

JNIEXPORT void JNICALL Java_com_luzi82_randomwallpaper_LiveWallpaper_setSeed(JNIEnv* env,
		jclass cls, jlong _seed) {
	seed = _seed;
}

JNIEXPORT void JNICALL Java_com_luzi82_randomwallpaper_LiveWallpaper_genRandom(JNIEnv* env,
		jclass cls, jbyteArray out) {
	static jint*ptr,*buf2,*bufEnd;
	static jsize len;
//	static jboolean isCopy;
	buf2=(jint*)((*env)->GetByteArrayElements(env,out,NULL));
	if(buf2) {
		len=(*env)->GetArrayLength(env,out);
//		if(isCopy){
//			memset(buf2,0xff,len);
//		}else{
			ptr=buf2;
			bufEnd=buf2+(len>>2);
			while(ptr!=bufEnd) {
				seed=(seed*multiplier+0xbLL)&((1LL<<48)-1);
				*ptr=(int)((seed>>(48-32))|0xff000000);
				ptr++;
			}
//		}
		(*env)->ReleaseByteArrayElements(env,out,(jbyte*)buf2,0);
	}
}
