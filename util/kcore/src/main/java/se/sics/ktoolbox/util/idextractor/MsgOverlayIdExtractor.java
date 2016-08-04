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
package se.sics.ktoolbox.util.idextractor;

import se.sics.kompics.KompicsEvent;
import se.sics.kompics.network.MessageNotify;
import se.sics.ktoolbox.util.identifiable.Identifier;
import se.sics.ktoolbox.util.network.KContentMsg;
import se.sics.ktoolbox.util.network.ports.ChannelIdExtractor;
import se.sics.ktoolbox.util.overlays.OverlayEvent;

/**
 * @author Alex Ormenisan <aaor@kth.se>
 */
public class MsgOverlayIdExtractor extends ChannelIdExtractor<KompicsEvent, Identifier> {

    public MsgOverlayIdExtractor() {
        super(KompicsEvent.class);
    }

    @Override
    public Identifier getValue(KompicsEvent m) {
        KContentMsg msg = null;
        if (m instanceof KContentMsg) {
            msg = (KContentMsg) m;

        } else if (m instanceof MessageNotify.Req) {
            MessageNotify.Req mm = (MessageNotify.Req) m;
            if(mm.msg instanceof KContentMsg) {
                msg = (KContentMsg)(mm.msg);
            } else {
                return null;
            }
        } else {
            return null;
        }
        if (msg.getContent() instanceof OverlayEvent) {
            return ((OverlayEvent) msg.getContent()).overlayId();
        } else {
            return null;
        }
    }
}
