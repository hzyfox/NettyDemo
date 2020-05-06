package reacrtor;

import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * create with reacrtor
 * USER: husterfox
 */
class ChannelState {
    static ConcurrentHashMap<SocketChannel, State> state = new ConcurrentHashMap<>();
}

enum State{
    /**
     * 正在读
     */
    Reading,
    /**
     * 正在处理
     */
    Processing,
    /**
     * 正在写
     */
    Writing
}

