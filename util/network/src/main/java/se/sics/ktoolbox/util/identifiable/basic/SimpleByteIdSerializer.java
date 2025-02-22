/*
 * Copyright (C) 2009 Swedish Institute of Computer Science (SICS) Copyright (C)
 * 2009 Royal Institute of Technology (KTH)
 *
 * KompicsToolbox is free software; you can redistribute it and/or
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
package se.sics.ktoolbox.util.identifiable.basic;

import com.google.common.base.Optional;
import io.netty.buffer.ByteBuf;
import se.sics.kompics.network.netty.serialization.Serializer;

/**
 * @author Alex Ormenisan <aaor@kth.se>
 */
public class SimpleByteIdSerializer implements Serializer {

    public final int id;

    public SimpleByteIdSerializer(int id) {
        this.id = id;
    }

    @Override
    public int identifier() {
        return id;
    }

    @Override
    public void toBinary(Object o, ByteBuf buf) {
        SimpleByteId obj = (SimpleByteId) o;
        buf.writeInt(obj.id.length);
        buf.writeBytes(obj.id);
    }

    @Override
    public Object fromBinary(ByteBuf buf, Optional<Object> hint) {
        int idLength = buf.readInt();
        byte[] idBytes = new byte[idLength];
        buf.readBytes(idBytes);
        return new SimpleByteId(idBytes);
    }
}
