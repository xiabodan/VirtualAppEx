//
// Created by admin on 2020/8/3.
//

#include <stdint.h>
#include <unistd.h>
#include <unistd.h>
#include <fcntl.h>
#include <sys/socket.h>
#include <sys/types.h>
#include <sys/un.h>
#include <android/log.h>
#include <strings.h>
#include "LBCoreIPCClient.h"

//#define DEBUG

#undef LOG_TAG
#define LOG_TAG "LBIPC"

#ifdef DEBUG
#define LOGD(fmt, args...)  do {__android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, fmt, ##args);} while(0)
#else
#define LOGD(fmt, args...) do {} while(0)
#endif

template <typename T>
void ReadInt(uint8_t *&p, T &val) {
    val = 0;
    for (int i = sizeof(T) - 1; i >= 0; i--) {
        uint8_t u8 = p[i];
        val = (val << 8) | u8;
    }
    p += sizeof(T);
}

template <typename T>
void WriteInt(uint8_t *&p, T val) {
    uint8_t *u8;
    u8 = (uint8_t *) &val;
    for (int i = 0; i < sizeof(T); i++) {
        p[i] = u8[i];
    }
    p += sizeof(T);
}

bool LBCoreIPCClient::connect(const char *socketname) {
    char path[UNIX_PATH_MAX]; // 108

    if (socketname == nullptr) {
        return false;
    }

    size_t namelen = strlen(socketname);
    if ((namelen + 2) > sizeof(path)) { // null + namelen + null > UNIX_PATH_MAX
        return false;
    }

    sockaddr_un addr;
    addr.sun_family = AF_UNIX;
    path[0] = 0;
    strcpy(path + 1, socketname);

    memcpy(addr.sun_path, path, namelen + 1);
    socklen_t addrlen = namelen + offsetof(sockaddr_un, sun_path) + 1;

    int client_socket = socket(AF_UNIX, SOCK_STREAM, 0);
    LOGD("LocalServerSocket connecting %s fd = %d", socketname, client_socket);
    if (client_socket > 0) {
        if (::connect(client_socket, (struct sockaddr *) &addr, addrlen) == 0) {
            LOGD("LocalServerSocket connected");
            connected_ = true;
            client_socket_ = client_socket;
        } else {
            LOGD("LocalServerSocket connecting failed");
            connected_ = false;
        }
    } else {
        connected_ = false;
    }
    return connected_;
}

int LBCoreIPCClient::getCallingUid(int vuid, int callingUid, int callingPid, int myPid) {
    if (!connected_) {
        return 0;
    }

    uint8_t buffer[64];
    uint8_t *request = buffer;
    WriteInt(request, (uint8_t) CUR_PROTOCAL_VERSION);
    WriteInt(request, (uint8_t) CMD_GETCALLINGUID);
    WriteInt(request, (uint32_t) vuid);
    WriteInt(request, (uint32_t) callingUid);
    WriteInt(request, (uint32_t) callingPid);
    WriteInt(request, (uint32_t) myPid);
    size_t length = request - buffer;
    if (::write(client_socket_, buffer, length) != length) {
        return 0;
    }

    bzero(buffer, sizeof(buffer));
    length = ::read(client_socket_, buffer, sizeof(buffer));
    if (length >= 5) { // ver + errno
        uint8_t ver;
        uint32_t error_code;
        uint32_t fake_uid;
        uint8_t *respone = buffer;
        ReadInt(respone, ver);
        ReadInt(respone, error_code);
        fake_uid = 0;
        if (error_code == CMD_ERROR_NO_ERROR) {
            ReadInt(respone, fake_uid);
        }
        return fake_uid;
    }
    return 0;
}