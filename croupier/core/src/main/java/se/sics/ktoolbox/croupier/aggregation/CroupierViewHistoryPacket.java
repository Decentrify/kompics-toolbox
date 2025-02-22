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
package se.sics.ktoolbox.croupier.aggregation;

import java.util.ArrayList;
import java.util.List;
import se.sics.ktoolbox.croupier.event.CroupierSample;
import se.sics.ktoolbox.util.aggregation.StatePacket;
import se.sics.ktoolbox.util.other.AgingAdrContainer;

/**
 * @author Alex Ormenisan <aaor@kth.se>
 */
public class CroupierViewHistoryPacket implements StatePacket {

    public final List<CroupierSample> viewHistory = new ArrayList<>();

    public void append(CroupierSample sample) {
        viewHistory.add(sample);
    }

    @Override
    public String shortPrint() {
        StringBuilder sb = new StringBuilder();
        sb.append("\ncroupier view print:");
        for (CroupierSample<AgingAdrContainer> slice : viewHistory) {
            sb.append("\ncroupier view slice public:");
            for (AgingAdrContainer<?, ?> neighbour : slice.publicSample.values()) {
                sb.append(neighbour.getSource().getId()).append("<");
                sb.append("a:").append(neighbour.getAge());
                sb.append(">");
            }
            sb.append("\ncroupier view slice private:");
            for (AgingAdrContainer<?, ?> neighbour : slice.privateSample.values()) {
                sb.append(neighbour.getSource().getId()).append("<");
                sb.append("a:").append(neighbour.getAge());
                sb.append(">");
            }
        }
        return sb.toString();
    }
}
