cmake_minimum_required(VERSION 3.22.1)
project(ventoy_android_native LANGUAGES CXX)

add_library(ventoy_android SHARED native-lib.cpp)

find_library(android_log log)

target_compile_features(ventoy_android PRIVATE cxx_std_17)
target_compile_options(ventoy_android PRIVATE -Wall -Wextra -Werror)
target_link_libraries(ventoy_android PRIVATE ${android_log})
