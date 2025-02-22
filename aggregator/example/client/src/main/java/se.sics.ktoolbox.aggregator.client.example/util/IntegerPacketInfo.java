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
package se.sics.ktoolbox.aggregator.client.example.util;

import se.sics.ktoolbox.aggregator.util.PacketInfo;

/**
 * PacketInformation containing the integer pair from
 * the application state.
 *
 * Created by babbarshaer on 2015-08-31.
 */
public class IntegerPacketInfo implements PacketInfo {

    public final Integer var1;
    public final Integer var2;

    public IntegerPacketInfo(Integer it1, Integer it2){
        this.var1  = it1;
        this.var2  = it2;
    }


    @Override
    public String toString() {
        return "IntegerPacketInfo{" +
                "var1=" + var1 +
                ", var2=" + var2 +
                '}';
    }
}
