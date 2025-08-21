cmake_minimum_required(VERSION 3.18.1)

project("jailbreak_root_detection")

set(can_use_assembler TRUE)
enable_language(ASM)

add_library(antifrida SHARED syscall.S antifrida.cpp)

# ✅ ใช้ LLD และกำหนด page size 16KB
# (ใช้ได้ตั้งแต่ CMake 3.13+ ซึ่งคุณมี 3.18.1 อยู่แล้ว)
add_link_options("-fuse-ld=lld")
target_link_options(antifrida PRIVATE "-Wl,-z,max-page-size=16384")

# (เผื่อบางสภาพแวดล้อมไม่อ่าน add/target_link_options)
set(CMAKE_SHARED_LINKER_FLAGS "${CMAKE_SHARED_LINKER_FLAGS} -Wl,-z,max-page-size=16384 -fuse-ld=lld")

find_library(log-lib log)
target_link_libraries(antifrida ${log-lib})
