//
// Created by admin on 2020/8/3.
//

#ifndef VIRTUALAPPEX_LBCOREIPCCLIENT_H
#define VIRTUALAPPEX_LBCOREIPCCLIENT_H

#include <string>
#include <unistd.h>

class LBCoreIPCClient {
    static const uint8_t CUR_PROTOCAL_VERSION = 1;
    static const uint8_t CMD_GETCALLINGUID = 1;
    static const uint32_t CMD_ERROR_NO_ERROR = 0;

public:
    LBCoreIPCClient(const char *socketname) {
        connected_ = false;
        client_socket_ = -1;
        connect(socketname);
    }

    ~LBCoreIPCClient() {
        if (client_socket_ > 0) {
            close(client_socket_);
        }
    }

    int getCallingUid(int vuid, int callingUid, int callingPid, int myPid);

private:
    bool connect(const char *socketname);

    bool connected_;
    int client_socket_;
};

#endif //VIRTUALAPPEX_LBCOREIPCCLIENT_H
