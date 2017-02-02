/*
 * TeleStax, Open Source Cloud Communications
 * Copyright 2011-2016, Telestax Inc and individual contributors
 * by the @authors tag. 
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.mobicents.media.control.mgcp.call;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.log4j.Logger;

/**
 * Manages a group of calls throughout the system. Assumes the call-id is global.
 * 
 * @author Henrique Rosa (henrique.rosa@telestax.com)
 *
 */
public class GlobalMgcpCallManager implements MgcpCallManager {

    private static final Logger log = Logger.getLogger(GlobalMgcpCallManager.class);

    private final ConcurrentMap<Integer, MgcpCall> calls;

    public GlobalMgcpCallManager() {
        this.calls = new ConcurrentHashMap<>();
    }

    @Override
    public MgcpCall getCall(int callId) {
        return this.calls.get(callId);
    }

    @Override
    public boolean registerCall(MgcpCall call) {
        MgcpCall old = this.calls.putIfAbsent(call.getCallId(), call);
        boolean registered = (old == null);

        if (registered && log.isDebugEnabled()) {
            log.debug("Registered new call " + call.getCallIdHex());
        }

        return registered;
    }

    @Override
    public MgcpCall unregisterCall(int callId) {
        MgcpCall call = this.calls.remove(callId);
        boolean removed = (call != null);

        if (removed && log.isDebugEnabled()) {
            log.debug("Unregistered call " + call.getCallIdHex());
        }
        return call;
    }

    @Override
    public Set<MgcpCall> unregisterCalls() {
        Set<MgcpCall> values;
        if (this.calls.isEmpty()) {
            // No calls. Return empty set.
            values = Collections.<MgcpCall> emptySet();
        } else {
            // Copy values
            values = new HashSet<>(this.calls.values());
            // Unregister all calls
            this.calls.clear();
        }
        return values;
    }

}
