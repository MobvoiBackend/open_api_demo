cmake_minimum_required(VERSION 3.27)
project("openapi demo")

message(STATUS "Project: ${PROJECT_NAME}")
message(STATUS "Vcpkg: ${CMAKE_TOOLCHAIN_FILE}")

set(CMAKE_CXX_STANDARD 23)
set(CMAKE_C_STANDARD 17)

set(CMAKE_EXPORT_COMPILE_COMMANDS ON)
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -save-temps")

find_package(glog CONFIG REQUIRED)
find_package(nlohmann_json CONFIG REQUIRED)
find_package(CURL REQUIRED)
find_package(OpenSSL REQUIRED)

set(glog_lib glog::glog)
set(json_lib nlohmann_json::nlohmann_json)
set(curl_lib CURL::libcurl)
set(ssl_lib OpenSSL::SSL OpenSSL::Crypto)

add_executable(tts_demo tts_demo.cc)
target_link_libraries(tts_demo PRIVATE ${glog_lib} ${json_lib} ${curl_lib} ${ssl_lib})
