#pragma once

#include "type.h"

#ifdef WIN32
#define DLL_API __declspec(dllexport)
#else
#define DLL_API
#endif

namespace ali_face_engine {

    class DLL_API FaceAttributeAnalyze {
    public:
        enum Flag {
            QUALITY = 0x1,
            LIVENESS = 0x2,
            AGE = 0x4,
            GENDER = 0x8,
            EXPRESSION = 0x10,
            GLASS = 0x20,
        };

    public:
        static FaceAttributeAnalyze *createInstance(enum Mode mode = TERMINAL);

        static void deleteInstance(FaceAttributeAnalyze *&ins);

    public:
        virtual void setFlag(int flag) = 0;

        virtual int getFlag() = 0;

        virtual int analyze(Image &image, Face &face) = 0;

        virtual int analyze(Image &image, list<Face> &faceList) = 0;

    protected:
        FaceAttributeAnalyze();

        virtual ~FaceAttributeAnalyze();
    };

}
