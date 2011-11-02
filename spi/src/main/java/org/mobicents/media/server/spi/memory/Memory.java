/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc. and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
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

package org.mobicents.media.server.spi.memory;

import org.mobicents.media.server.scheduler.Clock;
import org.mobicents.media.server.scheduler.DefaultClock;

/**
 *
 * @author kulikov
 */
public class Memory {
    public static Clock clock = new DefaultClock();
    public final static int PARTITIONS = 100;

    private static Partition[] partitions = new Partition[PARTITIONS];
    private static int sCount;

    private static int findPartition(int size) {
        for (int i = 0; i < sCount; i++) {
            if (partitions[i].size >= size) {
                return i;
            }
        }
        return -1;
    }

    public synchronized static Frame allocate(int size) {
        int i = findPartition(size);

        if (i < 0) {
            i = sCount;
            partitions[sCount++] = new Partition(size);
            //allocate new segment
        }

        return partitions[i].allocate();
    }

}