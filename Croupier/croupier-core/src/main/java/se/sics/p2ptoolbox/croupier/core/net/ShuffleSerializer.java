/*
 * Copyright (C) 2009 Swedish Institute of Computer Science (SICS) Copyright (C)
 * Copyright (C) 2009 Royal Institute of Technology (KTH)
 *
 * Croupier is free software; you can redistribute it and/or
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
package se.sics.p2ptoolbox.croupier.core.net;

import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import java.util.List;
import se.sics.p2ptoolbox.croupier.api.util.CroupierPeerView;
import se.sics.p2ptoolbox.croupier.core.msg.Shuffle;
import se.sics.p2ptoolbox.serialization.SerializationContext;
import se.sics.p2ptoolbox.serialization.Serializer;

/**
 * @author Alex Ormenisan <aaor@sics.se>
 */
public class ShuffleSerializer implements Serializer<Shuffle> {

    public ByteBuf encode(SerializationContext context, ByteBuf buf, Shuffle obj) throws SerializerException, SerializationContext.MissingException {
        if(obj.publicNodes.isEmpty() && obj.privateNodes.isEmpty()) {
            buf.writeInt(-1);
            return buf;
        }
        Serializer serializer = context.getSerializer(obj.publicNodes.isEmpty() ? obj.privateNodes.get(0).getClass() : obj.publicNodes.get(0).getClass());

        buf.writeInt(obj.publicNodes.size());
        for (CroupierPeerView cpv : obj.publicNodes) {
            serializer.encode(context, buf, cpv);
        }
        buf.writeInt(obj.privateNodes.size());
        for (CroupierPeerView cpv : obj.privateNodes) {
            serializer.encode(context, buf, cpv);
        }
        return buf;
    }

    public Shuffle decode(SerializationContext context, ByteBuf buf) throws SerializerException, SerializationContext.MissingException {
        Serializer serializer = context.getSerializer(CroupierPeerView.class);
        
        int publicNodesSize = buf.readInt();
        if(publicNodesSize == -1) {
            return new Shuffle(new ArrayList<CroupierPeerView>(), new ArrayList<CroupierPeerView>());
        }
        List<CroupierPeerView> publicNodes = new ArrayList<CroupierPeerView>();
        for (int i = 0; i < publicNodesSize; i++) {
            publicNodes.add((CroupierPeerView) serializer.decode(context, buf));
        }
        
        int privateNodesSize = buf.readInt();
        List<CroupierPeerView> privateNodes = new ArrayList<CroupierPeerView>();
        for (int i = 0; i < privateNodesSize; i++) {
            privateNodes.add((CroupierPeerView) serializer.decode(context, buf));
        }
        return new Shuffle(publicNodes, privateNodes);
    }

    public int getSize(SerializationContext context, Shuffle obj) throws SerializerException, SerializationContext.MissingException {
        int size = 0;
        
        if(obj.publicNodes.isEmpty() && obj.privateNodes.isEmpty()) {
            size += Integer.BYTES/8;
            return size;
        }
        Serializer serializer = context.getSerializer(obj.publicNodes.isEmpty() ? obj.privateNodes.get(0).getClass() : obj.publicNodes.get(0).getClass());

        size += Integer.BYTES/8;
        for (CroupierPeerView cpv : obj.publicNodes) {
            size += serializer.getSize(context, cpv);
        }
        size += Integer.BYTES/8;
        for (CroupierPeerView cpv : obj.privateNodes) {
            size += serializer.getSize(context, cpv);
        }
        return size;
    }
}
