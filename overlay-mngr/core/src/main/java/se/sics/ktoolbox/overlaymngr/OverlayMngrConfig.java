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
package se.sics.ktoolbox.overlaymngr;

import java.util.Arrays;
import java.util.Optional;
import se.sics.kompics.util.Identifier;
import se.sics.ktoolbox.util.config.options.BasicAddressBootstrapOption;
import se.sics.ktoolbox.util.identifiable.BasicBuilders;
import se.sics.ktoolbox.util.identifiable.BasicIdentifiers;
import se.sics.ktoolbox.util.identifiable.IdentifierFactory;
import se.sics.ktoolbox.util.identifiable.IdentifierRegistryV2;

/**
 * @author Alex Ormenisan <aaor@kth.se>
 */
public class OverlayMngrConfig {

    public static final byte[] GLOBAL_CROUPIER_ID = new byte[]{};
    public static final byte[] GLOBAL_CROUPIER_ID_ALT = new byte[]{0,0,0,0};

    public final static BasicAddressBootstrapOption bootstrap = new BasicAddressBootstrapOption("globalcroupier.bootstrap");
    
    public static Identifier getGlobalCroupierIntegerId() {
        IdentifierFactory baseIdFactory = IdentifierRegistryV2.instance(BasicIdentifiers.Values.OVERLAY, Optional.of(1234l));
        return baseIdFactory.id(new BasicBuilders.IntBuilder(0));
    }
    
    public static boolean isGlobalCroupier(Identifier overlayId) {
        return getGlobalCroupierIntegerId().equals(overlayId);
    }
    public static boolean isGlobalCroupier(byte[] id) {
        return (Arrays.equals(id, GLOBAL_CROUPIER_ID) || Arrays.equals(id, GLOBAL_CROUPIER_ID_ALT));
    }
}
