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

package se.sics.ktoolbox.overlaymngr.util;

import com.google.common.primitives.Ints;
import org.javatuples.Pair;

/**
 * @author Alex Ormenisan <aaor@kth.se>
 */
public class OverlayMngrHelper {
    public static final byte croupier = 0;
    public static final byte gradient = 1;
    public static final byte tgradient = 2;
    public static Pair<Byte, Byte> getGlobalCroupierId() {
        return Pair.with((byte)0, (byte)0);
    }
    
    public static boolean isGlobalCroupierId(Pair<Byte, Byte> overlayId) {
        return getGlobalCroupierId().equals(overlayId);
    }
    
    public static Integer getCroupierIntOverlayId(Pair<Byte, Byte> overlayId) {
        return Ints.fromBytes((byte)0, croupier, overlayId.getValue0(), overlayId.getValue1());
    }
    
    public static byte[] getCroupierOverlayId(Pair<Byte, Byte> overlayId) {
        return new byte[]{(byte)0, croupier, overlayId.getValue0(), overlayId.getValue1()};
    }
}
