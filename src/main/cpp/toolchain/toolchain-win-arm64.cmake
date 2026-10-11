# Toolchain file: Windows ARM64 via llvm-mingw (Clang/LLD)
# Get a release from https://github.com/mstorsjo/llvm-mingw/releases
# then either put its bin/ on PATH or set LLVM_MINGW_ROOT.
set(CMAKE_SYSTEM_NAME Windows)
set(CMAKE_SYSTEM_PROCESSOR ARM64)

set(TOOLCHAIN_PREFIX aarch64-w64-mingw32)

if(DEFINED ENV{LLVM_MINGW_ROOT})
  set(_root "$ENV{LLVM_MINGW_ROOT}")
  set(_bin  "${_root}/bin/")
  set(CMAKE_FIND_ROOT_PATH "${_root}/${TOOLCHAIN_PREFIX}")
else()
  set(_bin "")
  set(CMAKE_FIND_ROOT_PATH "")
endif()

set(CMAKE_C_COMPILER   ${_bin}${TOOLCHAIN_PREFIX}-clang)
set(CMAKE_CXX_COMPILER ${_bin}${TOOLCHAIN_PREFIX}-clang++)
set(CMAKE_RC_COMPILER  ${_bin}${TOOLCHAIN_PREFIX}-windres)

set(CMAKE_FIND_ROOT_PATH_MODE_PROGRAM NEVER)
set(CMAKE_FIND_ROOT_PATH_MODE_LIBRARY ONLY)
set(CMAKE_FIND_ROOT_PATH_MODE_INCLUDE ONLY)
set(CMAKE_FIND_ROOT_PATH_MODE_PACKAGE ONLY)

# Optional: fully static runtime (libc++, libunwind)
set(CMAKE_SHARED_LINKER_FLAGS_INIT "-static")
set(CMAKE_MODULE_LINKER_FLAGS_INIT "-static")
set(CMAKE_EXE_LINKER_FLAGS_INIT "-static")