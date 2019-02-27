#include <jni.h>
#include <string>
#include <stdlib.h>
#include <android/bitmap.h>
#include <android/log.h>

#define LOGI(fmt, args...) __android_log_print(ANDROID_LOG_INFO, "AliFaceEngine_DemoJNI", fmt, ##args)
#define LOGE(fmt, args...) __android_log_print(ANDROID_LOG_ERROR, "AliFaceEngine_DemoJNI", fmt, ##args)

void nv21ToRGB888(unsigned char *yuv, int width, int height, unsigned char *&rgb, int &rgbLen);

void displayToBitmap(jobject bitmap, JNIEnv *env, unsigned char *rgb888, bool hasAlpha);

extern "C"
JNIEXPORT jstring JNICALL
Java_com_alibaba_cloud_alifaceenginedemo_Utils_displayNV21ToBitmap(
        JNIEnv *env, jobject obj,
        jobject bitmap, jbyteArray data, jint width, jint height) {
    unsigned char *yuv = (unsigned char *) (env->GetByteArrayElements(data, 0));
    unsigned char *rgb = 0;
    int rgbLen = 0;
    nv21ToRGB888(yuv, width, height, rgb, rgbLen);
    if (rgb) {
        displayToBitmap(bitmap, env, rgb, false);
        free(rgb);
    }

    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}

#define ARGB8888_MASK_ALPHA 0xFF000000
#define ARGB8888_MASK_RED   0x00FF0000
#define ARGB8888_MASK_GREEN 0x0000FF00
#define ARGB8888_MASK_BLUE  0x000000FF

void displayToBitmap(jobject bitmap, JNIEnv *env, unsigned char *rgb888, bool hasAlpha) {
    int ret;
    uint32_t red, green, blue, alpha;
    AndroidBitmapInfo imageInfo;
    void *pixelscolor;
    if ((ret = AndroidBitmap_getInfo(env, bitmap, &imageInfo)) < 0) {
        return;
    }

    if ((ret = AndroidBitmap_lockPixels(env, bitmap, &pixelscolor)) < 0) {
        LOGE("AndroidBitmap_lockPixels() failed ! error=%d", ret);
    }

    if (imageInfo.format == ANDROID_BITMAP_FORMAT_RGBA_8888);

    uint8_t *pData = rgb888;
    for (unsigned int row = 0; row < imageInfo.height; row++) {
        uint32_t *line = (uint32_t *) pixelscolor;
        pData = rgb888 + (row * imageInfo.width + imageInfo.width - 1) * 3;
        for (unsigned int col = 0; col < imageInfo.width; col++) {

            red = *pData++;
            green = *pData++;
            blue = *pData++;
            if (hasAlpha) {
                alpha = *pData++;
                pData = pData - 8;
            } else {
                alpha = 255;
                pData = pData - 6;
            }

            line[col] = ((alpha << 24) | ARGB8888_MASK_ALPHA)
                        | ((red << 16) & ARGB8888_MASK_RED)
                        | ((green << 8) & ARGB8888_MASK_GREEN)
                        | (blue & ARGB8888_MASK_BLUE);

        }
        pixelscolor = (uint8_t *) pixelscolor + imageInfo.stride;
    }
    AndroidBitmap_unlockPixels(env, bitmap);
}

#define CLIP(x) (x)>255?255:((x)<0?0:(x))
#define YUV2R(Y, U, V)  ((Y)+((359*(V))>>8)-178)
#define YUV2G(Y, U, V)  ((Y)-((88*(U)+183*(V))>>8) + 135)
#define YUV2B(Y, U, V)  ((Y)+((454*(U))>>8) - 226)

void nv21ToRGB888(unsigned char *yuv, int width, int height, unsigned char *&rgb, int &rgbLen) {
    rgbLen = width * height * 3;
    rgb = (unsigned char *) malloc(rgbLen);
    unsigned char *pY = yuv;
    unsigned char *pUV = yuv + width * height;
    unsigned char *pdst = rgb;
    int i;
    int j;
    for (i = 0; i < height; i++) {
        for (j = 0; j < width; j++) {
            pdst[3 * (width * i + j)] = *(pY + i * width + j);
            pdst[3 * (width * i + j) + 1] = *(pUV + (i / 2) * width
                                              + 2 * (j / 2));
            pdst[3 * (width * i + j) + 2] = *(pUV + (i / 2) * width
                                              + 2 * (j / 2) + 1);
        }
    }

    char y, u, v;
    unsigned char *p;
    pdst = rgb;
    p = rgb;
    for (i = 0; i < width * height; i++) {
        y = *pdst++;
        u = *pdst++;
        v = *pdst++;

        *p++ = CLIP(YUV2R(y, u, v));
        *p++ = CLIP(YUV2G(y, u, v));
        *p++ = CLIP(YUV2B(y, u, v));
    }
}
