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
package se.sics.ktoolbox.aggregator.client.events;

import se.sics.kompics.util.Identifier;
import se.sics.ktoolbox.aggregator.event.AggregatorEvent;
import se.sics.ktoolbox.aggregator.util.AggregatorPacket;

/**
 * Event carrying the component information which will be
 * locally aggregated by the local aggregator.
 *
 * Created by babbar on 2015-08-31.
 */
public class ComponentPacketEvent implements AggregatorEvent {

    public final Identifier id;
    public final AggregatorPacket packet;

    public ComponentPacketEvent(Identifier id, AggregatorPacket packet) {
        this.id = id;
        this.packet = packet;
    }

    @Override
    public String toString() {
        return getClass() + "<" + id + ">";
    }

    @Override
    public Identifier getId() {
        return id;
    }
}
