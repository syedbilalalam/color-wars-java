# CMakeList.txt : CMake project for wlan-pc-bcode, include source and define
# project specific logic here.
#
cmake_minimum_required (VERSION 3.10)

project (ColorWarsJava VERSION 1.0 LANGUAGES CXX)

# Sub project
add_subdirectory(external/frost_colorwars)

# Add library
add_library(
	ColorWarsJava SHARED
    src/launcher.cpp
)
add_library(ColorWars::Java ALIAS ColorWarsJava)
target_include_directories(ColorWarsJava
    PUBLIC
        ${CMAKE_CURRENT_SOURCE_DIR}/include
)
target_link_libraries(ColorWarsJava PRIVATE frost_colorwars::static)
set_target_properties(ColorWarsJava PROPERTIES
    OUTPUT_NAME "frost_colorwars_java"
    PREFIX ""
)

# MACRO for cpp lib
target_compile_definitions(ColorWarsJava PRIVATE F_COLOR_WARS_COMPILING)


# CXX version
function(target_cxx_strict_version target cxx_std_version)
    target_compile_features(${target} PUBLIC cxx_std_${cxx_std_version})

    set_target_properties(
        ${target}
        PROPERTIES
            CXX_STANDARD ${cxx_std_version}
            CXX_STANDARD_REQUIRED ON
            CXX_EXTENSIONS OFF
    )
    if(CMAKE_CXX_COMPILER_ID MATCHES "GNU|Clang")
        target_compile_options(${target} PRIVATE -Wall -Wextra -pedantic-errors)
        target_compile_options(${target} PRIVATE -Wall -Wextra -pedantic-errors)
    elseif(MSVC)
        target_compile_options(${target} PRIVATE /W4 /permissive-)
        target_compile_options(${target} PRIVATE /W4 /permissive-)
    endif()
endfunction()

# Setting strict CXX version
target_cxx_strict_version(ColorWarsJava 17)