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
package se.sics.ktoolbox.util.setup;

import se.sics.ktoolbox.util.msg.BasicHeaderSerializer;
import se.sics.ktoolbox.util.address.basic.BasicAddressSerializer;
import se.sics.kompics.network.netty.serialization.Serializers;
import se.sics.kompics.simutil.identifiable.impl.IntIdentifier;
import se.sics.kompics.simutil.identifiable.impl.UUIDIdentifier;
import se.sics.kompics.simutil.msg.impl.BasicAddress;
import se.sics.kompics.simutil.msg.impl.BasicContentMsg;
import se.sics.kompics.simutil.msg.impl.BasicHeader;
import se.sics.kompics.simutil.msg.impl.DecoratedHeader;
import se.sics.ktoolbox.util.IntIdentifierSerializer;
import se.sics.ktoolbox.util.UUIDIdentifierSerializer;
import se.sics.ktoolbox.util.address.nat.NatAwareAddressImpl;
import se.sics.ktoolbox.util.address.nat.NatAwareAddressImplSerializer;
import se.sics.ktoolbox.util.address.nat.NatType;
import se.sics.ktoolbox.util.msg.BasicContentMsgSerializer;
import se.sics.ktoolbox.util.msg.DecoratedHeaderSerializer;
import se.sics.ktoolbox.util.address.nat.NatTypeSerializer;

/**
 * @author Alex Ormenisan <aaor@sics.se>
 */
public class BasicSerializerSetup {

    public static final int serializerIds = 8;

    public static enum BasicSerializers {

        IntIdentifier(IntIdentifier.class, "intIdentifierSerializer"),
        UUIDIdentifier(UUIDIdentifier.class, "uuidIdentifierSerializer"),
        BasicAddress(BasicAddress.class, "basicAddressSerializer"),
        NatAwareAddressImpl(NatAwareAddressImpl.class, "strippedNAAddressSerializer"),
        BasicHeader(BasicHeader.class, "basicHeaderSerializer"),
        DecoratedHeader(DecoratedHeader.class, "decoratedHeaderSerializer"),
        BasicContentMsg(BasicContentMsg.class, "basicContentMsgSerializer"),
        NatType(NatType.class, "natTypeSerializer");

        public final Class serializedClass;
        public final String serializerName;

        BasicSerializers(Class serializedClass, String serializerName) {
            this.serializedClass = serializedClass;
            this.serializerName = serializerName;
        }
    }

    public static void checkSetup() {
        for (BasicSerializers bs : BasicSerializers.values()) {
            if (Serializers.lookupSerializer(bs.serializedClass) == null) {
                throw new RuntimeException("No serializer for " + bs.serializedClass);
            }
        }
    }

    public static int registerBasicSerializers(int startingId) {
        if (startingId < 128) {
            throw new RuntimeException("start your serializer ids at 128");
        }
        int currentId = startingId;

        IntIdentifierSerializer intIdentifierSerializer = new IntIdentifierSerializer(currentId++);
        Serializers.register(intIdentifierSerializer, BasicSerializers.IntIdentifier.serializerName);
        Serializers.register(BasicSerializers.IntIdentifier.serializedClass, BasicSerializers.IntIdentifier.serializerName);

        UUIDIdentifierSerializer uuidIdentifierSerializer = new UUIDIdentifierSerializer(currentId++);
        Serializers.register(uuidIdentifierSerializer, BasicSerializers.UUIDIdentifier.serializerName);
        Serializers.register(BasicSerializers.UUIDIdentifier.serializedClass, BasicSerializers.UUIDIdentifier.serializerName);

        BasicAddressSerializer basicAddressSerializer = new BasicAddressSerializer(currentId++);
        Serializers.register(basicAddressSerializer, BasicSerializers.BasicAddress.serializerName);
        Serializers.register(BasicSerializers.BasicAddress.serializedClass, BasicSerializers.BasicAddress.serializerName);

        NatAwareAddressImplSerializer natAwareAddressSerializer = new NatAwareAddressImplSerializer(currentId++);
        Serializers.register(natAwareAddressSerializer, BasicSerializers.NatAwareAddressImpl.serializerName);
        Serializers.register(BasicSerializers.NatAwareAddressImpl.serializedClass, BasicSerializers.NatAwareAddressImpl.serializerName);

        BasicHeaderSerializer basicHeaderSerializer = new BasicHeaderSerializer(currentId++);
        Serializers.register(basicHeaderSerializer, BasicSerializers.BasicHeader.serializerName);
        Serializers.register(BasicSerializers.BasicHeader.serializedClass, BasicSerializers.BasicHeader.serializerName);

        DecoratedHeaderSerializer decoratedHeaderSerializer = new DecoratedHeaderSerializer(currentId++);
        Serializers.register(decoratedHeaderSerializer, BasicSerializers.DecoratedHeader.serializerName);
        Serializers.register(BasicSerializers.DecoratedHeader.serializedClass, BasicSerializers.DecoratedHeader.serializerName);

        BasicContentMsgSerializer basicContentMsgSerializer = new BasicContentMsgSerializer(currentId++);
        Serializers.register(basicContentMsgSerializer, BasicSerializers.BasicContentMsg.serializerName);
        Serializers.register(BasicSerializers.BasicContentMsg.serializedClass, BasicSerializers.BasicContentMsg.serializerName);

        NatTypeSerializer natTypeSerializer = new NatTypeSerializer(currentId++);
        Serializers.register(natTypeSerializer, BasicSerializers.NatType.serializerName);
        Serializers.register(BasicSerializers.NatType.serializedClass, BasicSerializers.NatType.serializerName);

        assert startingId + serializerIds == currentId;
        return currentId;
    }
}