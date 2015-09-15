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
package se.sics.ktoolbox.aggregator.server.event;

import se.sics.kompics.KompicsEvent;
import se.sics.p2ptoolbox.util.network.impl.BasicAddress;

import java.util.List;
import java.util.Map;
import se.sics.ktoolbox.aggregator.util.PacketInfo;


/**
 * Event from the aggregator indicating the
 * aggregated information from the nodes in the system.
 *
 * Created by babbarshaer on 2015-09-02.
 */
public class AggregatedInfo implements KompicsEvent {

    private final Map<Integer, List<PacketInfo>> nodePacketMap;

    public AggregatedInfo(Map<Integer, List<PacketInfo>> nodePacketMap){
        this.nodePacketMap = nodePacketMap;
    }

    public Map<Integer, List<PacketInfo>> getNodePacketMap() {
        return nodePacketMap;
    }
}
