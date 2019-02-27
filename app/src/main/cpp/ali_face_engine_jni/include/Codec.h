#pragma once

#include "type.h"

#ifdef WIN32
#define DLL_API __declspec(dllexport)
#else
#define DLL_API
#endif

namespace ali_face_engine {
    struct Region {
        int x;
        int y;
        int width;
        int height;

        Region() :
                x(0),
                y(0),
                width(0),
                height(0) {

        }

        Region(int x, int y, int width, int height) {
            this->x = x;
            this->y = y;
            this->width = width;
            this->height = height;
        }
    };

    class DLL_API Codec {
    public:
        static Image toBGR888(Image &src);

        static Image toRGB888(Image &src);

        static Image toNV21(Image &src);

        static Image toJpeg(Image &src);

        static Image rotateToPortrait(Image &src);

        static void rgb888ToBGR888(unsigned char *rgb, int width, int height, unsigned char *&bgr);

        static void rgb888ToBGR888(unsigned char *rgb, int width, int height, unsigned char *&bgr, int &bgrLen);

        static void rgb888ToBGR888InPlace(unsigned char *rgb, int width, int height);

        static void bgr888ToRGB888(unsigned char *bgr, int width, int height, unsigned char *&rgb);

        static void bgr888ToRGB888(unsigned char *bgr, int width, int height, unsigned char *&rgb, int &rgbLen);

        static void nv21ToRGB888(unsigned char *yuv, int width, int height, unsigned char *&rgb);

        static void nv21ToRGB888(unsigned char *yuv, int width, int height, unsigned char *&rgb, int &rgbLen);

        static void nv21ToBGR888(unsigned char *yuv, int width, int height, unsigned char *&bgr);

        static void nv21ToBGR888(unsigned char *yuv, int width, int height, unsigned char *&bgr, int &bgrLen);

        static void yv12ToRGB888(unsigned char *yv12, int width, int height, unsigned char *&rgb);

        static void yv12ToRGB888(unsigned char *yv12, int width, int height, unsigned char *&rgb, int &rgbLen);

        static void yv12ToBGR888(unsigned char *yv12, int width, int height, unsigned char *&bgr);

        static void yv12ToBGR888(unsigned char *yv12, int width, int height, unsigned char *&bgr, int &bgrLen);

        static void i420ToRGB888(unsigned char *i420, int width, int height, unsigned char *&rgb);

        static void i420ToRGB888(unsigned char *i420, int width, int height, unsigned char *&rgb, int &rgbLen);

        static void i420ToBGR888(unsigned char *i420, int width, int height, unsigned char *&bgr);

        static void i420ToBGR888(unsigned char *i420, int width, int height, unsigned char *&bgr, int &bgrLen);

        static void rgb888ToNV21(unsigned char *rgb, int width, int height, unsigned char *&yuv);

        static void rgb888ToNV21(unsigned char *rgb, int width, int height, unsigned char *&yuv, int &yuvLen);

        static void bgr888ToNV21(unsigned char *bgr, int width, int height, unsigned char *&yuv);

        static void bgr888ToNV21(unsigned char *bgr, int width, int height, unsigned char *&yuv, int &yuvLen);

        static void nv21Rotate90(unsigned char *src, int width, int height, unsigned char *dest);

        static void nv21Rotate90InPlace(unsigned char *src, int width, int height);

        static void nv21Rotate180(unsigned char *src, int width, int height, unsigned char *dest);

        static void nv21Rotate180InPlace(unsigned char *src, int width, int height);

        static void nv21Rotate270(unsigned char *src, int width, int height, unsigned char *dest);

        static void nv21Rotate270InPlace(unsigned char *src, int width, int height);

        static void jpegToRGB888(unsigned char *jpeg, int jpegLen, unsigned char *&rgb, int &width, int &height);

        static void jpegToBGR888(unsigned char *jpeg, int jpegLen, unsigned char *&bgr, int &width, int &height);

        static void
        rgb888ToJpeg(unsigned char *rgb888, int width, int height, int quality, unsigned char *&jpeg, int &jpegLen);

        static void pngToRGB888(unsigned char *png, int pngLen, unsigned char *&rgb, int &width, int &height);

        static void pngToBGR888(unsigned char *png, int pngLen, unsigned char *&bgr, int &width, int &height);

        static void bmpToRGB888(unsigned char *bmp, int bmpLen, unsigned char *&rgb, int &width, int &height);

        static void bmpToBGR888(unsigned char *bmp, int bmpLen, unsigned char *&bgr, int &width, int &height);

        static bool isJpeg(unsigned char *data, int len);

        static bool isPng(unsigned char *data, int len);

        static bool isBMP(unsigned char *data, int len);

        static void cropRGB(unsigned char *src, int srcWidth, int srcHeight, int srcChl, Region &destRegion,
                            unsigned char *&dest);

        static void resiseBilinearRGB(unsigned char *pSrc, int srcWidth, int srcHeight, int srcChl,
                                      unsigned char *&pDst, int dstWidth, int dstHeight, float &ratio);
    };

}
