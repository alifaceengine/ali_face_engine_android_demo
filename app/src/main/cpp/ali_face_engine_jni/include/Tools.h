#pragma once

#include <string>
#include "type.h"

using namespace std;

#ifdef WIN32
#define DLL_API __declspec(dllexport)
#else
#define DLL_API
#endif

namespace ali_face_engine {
    class DLL_API Tools {
    public:
        static void drawFaceRect(Image &image, Face &face, int color);

        static void drawFaceRect(Image &image, list<Face> faceList, int color);

        static void drawFacePoint(Image &image, Face &face, int color);

        static void drawFacePoint(Image &image, list<Face> faceList, int color);


        static void drawFaceRect(Image *image, Face *face, int color);

        static void drawFaceRect(Image *image, Face *faces, int faceNum, int color);

        static void drawFacePoint(Image *image, Face *face, int color);

        static void drawFacePoint(Image *image, Face *faces, int faceNum, int color);


        static string httpPost(string &url, string &postData);

        static string httpPost(string &url, char *postDataFile);
    };
}