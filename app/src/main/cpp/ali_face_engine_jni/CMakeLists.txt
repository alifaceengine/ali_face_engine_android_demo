cmake_minimum_required(VERSION 3.4.1)

project(ali_face_engine_jni)

message(CMAKE_SOURCE_DIR : ${CMAKE_SOURCE_DIR})

aux_source_directory(. SOURCES)

include_directories(.)
include_directories(./include)

message(SOURCES : ${SOURCES})

add_library(AliFaceEngine SHARED IMPORTED)
set_target_properties(AliFaceEngine  PROPERTIES IMPORTED_LOCATION ${CMAKE_SOURCE_DIR}/src/main/libs/${ANDROID_ABI}/libAliFaceEngine.so)

ADD_LIBRARY(AliFaceEngineJNI SHARED ${SOURCES})

target_link_libraries(AliFaceEngineJNI ${log-lib} AliFaceEngine)