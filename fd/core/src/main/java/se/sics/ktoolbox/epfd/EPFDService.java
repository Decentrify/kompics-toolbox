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
package se.sics.ktoolbox.epfd;

import java.util.UUID;
import se.sics.ktoolbox.epfd.event.EPFDFollow;
import se.sics.ktoolbox.epfd.event.EPFDIndication;
import se.sics.ktoolbox.util.network.KAddress;

/**
 * @author Alex Ormenisan <aaor@kth.se>
 */
public interface EPFDService {
    public void answerRequest(EPFDFollow request, EPFDIndication indication);
    public UUID nextPing(boolean suspected, KAddress probedHost);
    public UUID ping(long ts, KAddress probedHost, long expectedRtt);
    public void stop(UUID nextPingTid, UUID pongTid);
}
