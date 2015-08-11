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

package se.sics.ktoolbox.echo.serializers;

import com.google.common.base.Optional;
import io.netty.buffer.ByteBuf;
import se.sics.kompics.network.Address;
import se.sics.kompics.network.netty.serialization.Serializer;
import se.sics.kompics.network.netty.serialization.Serializers;
import se.sics.ktoolbox.echo.Pong;

/**
 * @author Alex Ormenisan <aaor@kth.se>
 */
public class PongSerializer implements Serializer {
    private final int id;
    
    public PongSerializer(int id) {
        this.id = id;
    }

    public int identifier() {
        return id;
    }

    public void toBinary(Object o, ByteBuf bb) {
        Pong obj = (Pong)o;
        Serializers.toBinary(obj.pingSrc, bb);
    }

    public Pong fromBinary(ByteBuf bb, Optional<Object> optnl) {
        Address pingSrc = (Address)Serializers.fromBinary(bb, optnl);
        return new Pong(pingSrc);
    }
}
