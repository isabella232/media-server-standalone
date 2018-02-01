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

package org.restcomm.media.server.standalone.bootstrap.ioc;

import com.google.inject.AbstractModule;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import org.restcomm.media.network.deprecated.UdpManager;
import org.restcomm.media.resource.player.audio.RemoteStreamProvider;
import org.restcomm.media.rtp.crypto.DtlsSrtpServerProvider;
import org.restcomm.media.scheduler.Clock;
import org.restcomm.media.scheduler.PriorityQueueScheduler;
import org.restcomm.media.scheduler.Scheduler;
import org.restcomm.media.server.standalone.bootstrap.ioc.provider.*;
import org.restcomm.media.server.standalone.bootstrap.ioc.provider.mgcp.Mgcp2ControllerProvider;
import org.restcomm.media.server.standalone.configuration.MediaServerConfiguration;
import org.restcomm.media.spi.ServerManager;

/**
 * @author Henrique Rosa (henrique.rosa@telestax.com)
 */
public class CoreModule extends AbstractModule {

    private final MediaServerConfiguration config;

    public CoreModule(MediaServerConfiguration config) {
        this.config = config;
    }

    @Override
    protected void configure() {
        bind(MediaServerConfiguration.class).toInstance(this.config);
        bind(Clock.class).toProvider(WallClockProvider.class).in(Singleton.class);
        bind(PriorityQueueScheduler.class).toProvider(MediaSchedulerProvider.class).in(Singleton.class);
        bind(Scheduler.class).toProvider(TaskSchedulerProvider.class).in(Singleton.class);
        bind(UdpManager.class).toProvider(UdpManagerProvider.class).in(Singleton.class);
        bind(AudioPlayerFactoryProvider.AudioPlayerFactoryType.INSTANCE).toProvider(AudioPlayerFactoryProvider.class).in(Singleton.class);
        bind(AudioRecorderFactoryProvider.AudioRecorderFactoryType.INSTANCE).toProvider(AudioRecorderFactoryProvider.class).in(Singleton.class);
        bind(DtmfDetectorFactoryProvider.DtmfDetectorFactoryType.INSTANCE).toProvider(DtmfDetectorFactoryProvider.class).in(Singleton.class);
        bind(DtmfGeneratorFactoryProvider.DtmfGeneratorFactoryType.INSTANCE).toProvider(DtmfGeneratorFactoryProvider.class).in(Singleton.class);
        bind(ServerManager.class).toProvider(Mgcp2ControllerProvider.class).in(Singleton.class);
        bind(DtlsSrtpServerProvider.class).toProvider(DtlsSrtpServerProviderProvider.class).in(Singleton.class);
        Class<? extends Provider<? extends RemoteStreamProvider>> remoteStreamProvider;
        if (this.config.getResourcesConfiguration().getPlayerCacheEnabled()) {
            remoteStreamProvider = CachedRemoteStreamProvider.class;
        } else {
            remoteStreamProvider = DirectRemoteStreamProvider.class;
        }
        bind(RemoteStreamProvider.class).toProvider(remoteStreamProvider).in(Singleton.class);
    }

}