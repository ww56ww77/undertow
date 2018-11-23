/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2014 Red Hat, Inc., and individual contributors
 * as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package io.undertow.websockets.jsr.handshake;

import java.util.Collections;

import org.xnio.StreamConnection;

import io.undertow.websockets.core.WebSocketChannel;
import io.undertow.websockets.core.protocol.version08.Hybi08Handshake;
import io.undertow.websockets.jsr.ConfiguredServerEndpoint;
import io.undertow.websockets.spi.WebSocketHttpExchange;

/**
 * {@link Hybi08Handshake} sub-class which takes care of match against the {@link javax.websocket.server.ServerEndpointConfig} and
 * stored the config in the attributes for later usage.
 *
 * @author <a href="mailto:nmaurer@redhat.com">Norman Maurer</a>
 */
public final class JsrHybi08Handshake extends Hybi08Handshake {
    private final ConfiguredServerEndpoint config;

    public JsrHybi08Handshake(ConfiguredServerEndpoint config) {
        super(Collections.<String>emptySet(), false);
        this.config = config;
    }

    @Override
    protected void upgradeChannel(final WebSocketHttpExchange exchange, byte[] data) {
        HandshakeUtil.prepareUpgrade(config.getEndpointConfiguration(), exchange);
        super.upgradeChannel(exchange, data);
    }

    @Override
    public WebSocketChannel createChannel(WebSocketHttpExchange exchange, final StreamConnection c, final ByteBufferPool buffers) {
        WebSocketChannel channel = super.createChannel(exchange, c, buffers);
        HandshakeUtil.setConfig(channel, config);
        return channel;
    }

    @Override
    public boolean matches(WebSocketHttpExchange exchange) {
        return super.matches(exchange) && HandshakeUtil.checkOrigin(config.getEndpointConfiguration(), exchange);
    }

    @Override
    protected String supportedSubprotols(String[] requestedSubprotocolArray) {
        return HandshakeUtil.selectSubProtocol(config, requestedSubprotocolArray);
    }
}
