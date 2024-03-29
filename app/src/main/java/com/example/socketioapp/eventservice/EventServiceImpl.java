/*
 * Copyright 2018 Mayur Rokade
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE.
 */

package com.example.socketioapp.eventservice;

import android.support.annotation.NonNull;
import android.util.Log;


import com.example.socketioapp.data.ChatMessage;

import java.net.URISyntaxException;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * Implementation of {@link EventService} which connects and disconnects to the server.
 * It also sends and receives events from the server.
 */
public class EventServiceImpl implements EventService {

    private static final String TAG = EventServiceImpl.class.getSimpleName();
    private static final String SOCKET_URL = "http://192.168.1.117:3000";
    private static final String EVENT_CONNECT = Socket.EVENT_CONNECT;
    private static final String EVENT_DISCONNECT = Socket.EVENT_DISCONNECT;
    private static final String EVENT_CONNECT_ERROR = Socket.EVENT_CONNECT_ERROR;
    private static final String EVENT_CONNECT_TIMEOUT = Socket.EVENT_CONNECT_TIMEOUT;
    private static final String EVENT_NEW_MESSAGE = "new message";
    private static final String EVENT_USER_JOINED = "user joined";
    private static final String EVENT_USER_LEFT = "user left";
    private static final String EVENT_TYPING = "typing";
    private static final String EVENT_STOP_TYPING = "stop typing";
    private static EventService INSTANCE;
    private static EventListener mEventListener;
    private static Socket mSocket;
    private String mUsername;

    // Prevent direct instantiation
    private EventServiceImpl() {}

    /**
     * Returns single instance of this class, creating it if necessary.
     *
     * @return
     */
    public static EventService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new EventServiceImpl();
        }

        return INSTANCE;
    }

    /**
     * Connect to the server.
     *
     * @param username
     * @throws URISyntaxException
     */
    @Override
    public void connect(String username) throws URISyntaxException {
        mUsername = username;
        mSocket = IO.socket(SOCKET_URL);

        // Register the incoming events and their listeners
        // on the socket.
        mSocket.on(EVENT_CONNECT, onConnect);
        mSocket.on(EVENT_DISCONNECT, onDisconnect);
        mSocket.on(EVENT_CONNECT_ERROR, onConnectError);
        mSocket.on(EVENT_CONNECT_TIMEOUT, onConnectError);
        mSocket.on(EVENT_NEW_MESSAGE, onNewMessage);
        mSocket.on(EVENT_USER_JOINED, onUserJoined);
        mSocket.on(EVENT_USER_LEFT, onUserLeft);
        mSocket.on(EVENT_TYPING, onTyping);
        mSocket.on(EVENT_STOP_TYPING, onStopTyping);

        mSocket.connect();
    }

    /**
     * Disconnect from the server.
     *
     */
    @Override
    public void disconnect() {
        if (mSocket != null) mSocket.disconnect();
    }

    /**
     * Send chat message to the server.
     *
     * @param chatMessage
     * @return
     */
    @Override
    public Flowable<ChatMessage> sendMessage(@NonNull final ChatMessage chatMessage) {
        return Flowable.create(new FlowableOnSubscribe<ChatMessage>() {
            @Override
            public void subscribe(FlowableEmitter<ChatMessage> emitter) throws Exception {
                /*
                 * Socket.io supports acking messages.
                 * This feature can be used as
                 * mSocket.emit("EVENT_NEW_MESSAGE", chatMessage.getMessage(), new Ack() {
                 *   @Override
                 *   public void call(Object... args) {
                 *       // Do something with args
                 *
                 *       // On success
                 *       emitter.onNext(chatMessage);
                 *
                 *       // On error
                 *       emitter.onError(new Exception("Sending message failed."));
                 *    }
                 * });
                 *
                 * */

                mSocket.emit(EVENT_NEW_MESSAGE, chatMessage.getMessage());
                emitter.onNext(chatMessage);
            }
        }, BackpressureStrategy.BUFFER);
    }

    /**
     * Send typing event to the server.
     *
     */
    @Override
    public void onTyping() {
        mSocket.emit(EVENT_TYPING);
    }

    /**
     * Send stop typing event to the server.
     *
     */
    @Override
    public void onStopTyping() {
        mSocket.emit(EVENT_STOP_TYPING);
    }

    /**
     * Set eventListener.
     *
     * When server sends events to the socket, those events are passed to the
     * RemoteDataSource -> Repository -> Presenter -> View using EventListener.
     *
     * @param eventListener
     */
    @Override
    public void setEventListener(EventListener eventListener) {
        mEventListener = eventListener;
    }

    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.i(TAG, "call: onConnect");
            mSocket.emit("add user", mUsername);
            if (mEventListener != null) mEventListener.onConnect(args);
        }
    };

    private Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.i(TAG, "call: onDisconnect");
            if (mEventListener != null) mEventListener.onDisconnect(args);
        }
    };

    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.i(TAG, "call: onConnectError");
            if (mEventListener != null) mEventListener.onConnectError(args);
        }
    };

    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            Log.i(TAG, "call: onNewMessage");
            if (mEventListener != null) mEventListener.onNewMessage(args);
        }
    };

    private Emitter.Listener onUserJoined = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            Log.i(TAG, "call: onNewMessage");
            if (mEventListener != null) mEventListener.onUserJoined(args);
        }
    };

    private Emitter.Listener onUserLeft = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            Log.i(TAG, "call: onNewMessage");
            if (mEventListener != null) mEventListener.onUserLeft(args);
        }
    };

    private Emitter.Listener onTyping = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            Log.i(TAG, "call: onNewMessage");
            if (mEventListener != null) mEventListener.onTyping(args);
        }
    };

    private Emitter.Listener onStopTyping = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            Log.i(TAG, "call: onNewMessage");
            if (mEventListener != null) mEventListener.onStopTyping(args);
        }
    };
}
