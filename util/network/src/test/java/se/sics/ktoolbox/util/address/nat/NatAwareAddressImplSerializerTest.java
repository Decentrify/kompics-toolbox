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
package se.sics.ktoolbox.util.address.nat;

import com.google.common.base.Optional;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Objects;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import se.sics.kompics.network.netty.serialization.Serializer;
import se.sics.kompics.network.netty.serialization.Serializers;
import se.sics.kompics.simutil.identifiable.impl.IntIdentifier;
import se.sics.kompics.simutil.msg.impl.BasicAddress;
import se.sics.ktoolbox.util.setup.BasicSerializerSetup;

/**
 *
 * @author Alex Ormenisan <aaor@kth.se>
 */
public class NatAwareAddressImplSerializerTest {

    @BeforeClass
    public static void setup() {
        int serializerId = 128;
        serializerId = BasicSerializerSetup.registerBasicSerializers(serializerId);
    }

    @Test
    public void testOpenAddress() throws UnknownHostException {
        Serializer serializer = Serializers.lookupSerializer(NatAwareAddressImpl.class);
        NatAwareAddressImpl original, copy;
        ByteBuf buf;

        BasicAddress address = new BasicAddress(InetAddress.getLocalHost(), 10000, new IntIdentifier(1));
        original = NatAwareAddressImpl.open(address);
        buf = Unpooled.buffer();
        serializer.toBinary(original, buf);

        ByteBuf copyBuf = Unpooled.buffer();
        buf.getBytes(0, copyBuf, buf.readableBytes());
        copy = (NatAwareAddressImpl) serializer.fromBinary(copyBuf, Optional.absent());
        Assert.assertEquals(original.getPublicAdr(), copy.getPublicAdr());
        Assert.assertEquals(original.getNatType(), copy.getNatType());
        Assert.assertTrue(Objects.equals(original.parents, copy.parents));
        Assert.assertFalse(copy.getPrivateAdr().isPresent());
        Assert.assertEquals(0, copyBuf.readableBytes());
    }

    @Test
    public void testNatedAddress() throws UnknownHostException {
        Serializer serializer = Serializers.lookupSerializer(NatAwareAddressImpl.class);
        NatAwareAddressImpl original, copy;
        ByteBuf buf, copyBuf;

        BasicAddress privateAdr = new BasicAddress(InetAddress.getByName("192.100.100.2"), 10000, new IntIdentifier(2));
        BasicAddress publicAdr = new BasicAddress(InetAddress.getByName("193.200.200.2"), 20000, new IntIdentifier(2));
        NatType natType = NatType.nated(Nat.MappingPolicy.HOST_DEPENDENT, Nat.AllocationPolicy.PORT_CONTIGUITY, 1,
                Nat.FilteringPolicy.HOST_DEPENDENT, 10000);
        ArrayList<BasicAddress> parents = new ArrayList<>();
        BasicAddress parent;
        parent = new BasicAddress(InetAddress.getByName("193.200.200.3"), 10000, new IntIdentifier(3));
        parents.add(parent);
        parent = new BasicAddress(InetAddress.getByName("193.200.200.4"), 10001, new IntIdentifier(4));
        parents.add(parent);
        original = NatAwareAddressImpl.nated(privateAdr, publicAdr, natType, parents);
        buf = Unpooled.buffer();
        serializer.toBinary(original, buf);

        copyBuf = Unpooled.buffer();
        buf.getBytes(0, copyBuf, buf.readableBytes());
        copy = (NatAwareAddressImpl) serializer.fromBinary(copyBuf, Optional.absent());
        Assert.assertEquals(original.getPublicAdr(), copy.getPublicAdr());
        Assert.assertEquals(original.getNatType(), copy.getNatType());
        Assert.assertTrue(Objects.equals(original.parents, copy.parents));
        Assert.assertFalse(copy.getPrivateAdr().isPresent());
        Assert.assertEquals(0, copyBuf.readableBytes());

        copyBuf = Unpooled.buffer();
        buf.getBytes(0, copyBuf, buf.readableBytes());
        InetSocketAddress isa = new InetSocketAddress(privateAdr.getIp(), privateAdr.getPort());
        copy = (NatAwareAddressImpl) serializer.fromBinary(copyBuf, Optional.of((Object) isa));
        Assert.assertEquals(original.getPublicAdr(), copy.getPublicAdr());
        Assert.assertEquals(original.getNatType(), copy.getNatType());
        Assert.assertTrue(Objects.equals(original.parents, copy.parents));
        Assert.assertTrue(copy.getPrivateAdr().isPresent());
        Assert.assertEquals(original.getPrivateAdr().get(), copy.getPrivateAdr().get());
        Assert.assertEquals(0, copyBuf.readableBytes());
    }
}