# FP stands for FilePath
# FN stands for FileName
# DIR stands for Directory | Must be a relative path from workdir

# STAGE_INDEPENDENT_ARGS

FROM mstorsjo/llvm-mingw:dev-20260922 AS builder
WORKDIR /app

# WINDOWS_FILE_NAME
ARG WIN_FN="frost_colorwars_java.dll"
# NAMES
ARG WIN32_NAME="windows-x86"
ARG WIN64_NAME="windows-x64"
ARG WIN_ARM64_NAME="windows-arm64"
# BUILD_DIRECTORY
ARG BUILD_DIR="build"
ARG WIN64_BUILD_DIR="${BUILD_DIR}/${WIN64_NAME}"
ARG WIN32_BUILD_DIR="${BUILD_DIR}/${WIN32_NAME}"
ARG WIN_ARM64_BUILD_DIR="${BUILD_DIR}/${WIN_ARM64_NAME}"
# BINARY_DIRECTORY
ARG BIN_DIR="bin"
ARG WIN64_BIN_DIR="${BIN_DIR}/${WIN64_NAME}"
ARG WIN32_BIN_DIR="${BIN_DIR}/${WIN32_NAME}"
ARG WIN_ARM64_BIN_DIR="${BIN_DIR}/${WIN_ARM64_NAME}"
# BUILD_FILE_PATH
ARG WIN64_BUILD_FP="${WIN64_BUILD_DIR}/${WIN_FN}"
ARG WIN32_BUILD_FP="${WIN32_BUILD_DIR}/${WIN_FN}"
ARG WIN_ARM64_BUILD_FP="${WIN_ARM64_BUILD_DIR}/${WIN_FN}"
# BINARY_FILE_PATH
ARG WIN64_BIN_FP="${WIN64_BIN_DIR}/${WIN_FN}"
ARG WIN32_BIN_FP="${WIN32_BIN_DIR}/${WIN_FN}"
ARG WIN_ARM64_BIN_FP="${WIN_ARM64_BIN_DIR}/${WIN_FN}"
# RESOURCE_FILE_PATH
#ARG APP_RESOURCE_DIR="external/frost_colorwars/resources"

COPY ./src/main/cpp .

RUN cmake --preset "${WIN64_NAME}"
RUN cmake --build --preset "${WIN64_NAME}"
RUN cmake --preset ${WIN32_NAME}
RUN cmake --build --preset ${WIN32_NAME}
RUN cmake --preset ${WIN_ARM64_NAME}
RUN cmake --build --preset ${WIN_ARM64_NAME}

RUN mkdir -p ${WIN64_BIN_DIR}
RUN mkdir -p ${WIN32_BIN_DIR}
RUN mkdir -p ${WIN_ARM64_BIN_DIR}

RUN cp ${WIN64_BUILD_FP} ${WIN64_BIN_FP}
RUN cp ${WIN32_BUILD_FP} ${WIN32_BIN_FP}
RUN cp ${WIN_ARM64_BUILD_FP} ${WIN_ARM64_BIN_FP}

#RUN cp -r ${APP_RESOURCE_DIR} ${WIN64_BIN_DIR}

FROM scratch AS export

COPY --from=builder /app/bin /src/main/resources/game/bin
COPY --from=builder /app/external/frost_colorwars/resources /src/main/resources/game