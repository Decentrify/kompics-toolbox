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
package se.sics.p2ptoolbox.croupier.core.util;

import java.util.HashSet;
import java.util.Set;
import se.sics.gvod.net.VodAddress;
import se.sics.p2ptoolbox.croupier.api.util.PeerView;

/**
 * @author Alex Ormenisan <aaor@sics.se>
 */
public class ViewEntry {

    public final PeerView peerView;
    private int age;
    private final Set<VodAddress> sentTo; 
    private long sentAt;
    private final long addedAt;
    public ViewEntry(PeerView peerView) {
        this.peerView = peerView;
        this.age = 0;
        this.sentTo = new HashSet<VodAddress>();
        this.addedAt = System.currentTimeMillis();
        this.sentAt = 0;
    }
    
    public int getAge() {
        return age;
    }
    
    public void incrementAge() {
        age++;
    }
    
    public void resetAge() {
        age = 0;
    }
    
    public void sentTo(VodAddress peer) {
        sentTo.add(peer);
        sentAt = System.currentTimeMillis();
    }
    
    public boolean wasSentTo(VodAddress peer) {
        return sentTo == null ? false : sentTo.contains(peer);
    }
}
