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
package se.sics.p2ptoolbox.croupier.msg;

import java.util.Set;
import se.sics.p2ptoolbox.util.Container;
import se.sics.p2ptoolbox.util.network.impl.DecoratedAddress;

/**
 * @author Alex Ormenisan <aaor@sics.se>
 */
public class CroupierSample<C extends Object> implements CroupierMsg.OneWay {

    public final int overlayId;
    public final Set<Container<DecoratedAddress, C>> publicSample;
    public final Set<Container<DecoratedAddress, C>> privateSample;
    
    public CroupierSample(int overlayId, Set<Container<DecoratedAddress, C>> publicSample, Set<Container<DecoratedAddress, C>> privateSample) {
        this.overlayId = overlayId;
        this.publicSample = publicSample;
        this.privateSample = privateSample;
    }

    @Override
    public String toString() {
        return "SAMPLE";
    }
}
