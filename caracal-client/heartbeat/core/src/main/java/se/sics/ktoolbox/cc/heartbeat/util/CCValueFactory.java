/*
 * Copyright (C) 2009 Swedish Institute of Computer Science (SICS) Copyright (C)
 * 2009 Royal Institute of Technology (KTH)
 *
 * GVoD is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package se.sics.ktoolbox.cc.heartbeat.util;

import com.google.common.base.Optional;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import se.sics.kompics.network.netty.serialization.Serializers;
import se.sics.ktoolbox.util.network.KAddress;

/**
 * @author Alex Ormenisan <aaor@kth.se>
 */
public class CCValueFactory {

    public static byte[] getHeartbeatValue(KAddress src) {
        ByteBuf buf = Unpooled.buffer();
        Serializers.toBinary(src, buf);
        return Arrays.copyOf(buf.array(), buf.readableBytes());
    }

    public static List<KAddress> extractHeartbeatSrc(Collection<byte[]> values) {
        List<KAddress> result = new ArrayList<>();
        for (byte[] value : values) {
            ByteBuf buf = Unpooled.wrappedBuffer(value);
            result.add((KAddress)Serializers.fromBinary(buf, Optional.absent()));
        }
        return result;
    }
}
