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
package se.sics.ktoolbox.gradient;

import se.sics.kompics.PortType;
import se.sics.ktoolbox.croupier.event.CroupierEvent;
import se.sics.ktoolbox.gradient.event.GradientEvent;
import se.sics.ktoolbox.gradient.event.GradientSample;

/**
 * Normal Functioning port of the gradient service.
 *
 * Created by babbarshaer on 2015-02-26.
 */
public class GradientPort extends PortType {
    {
        indication(GradientSample.class);
        //temporary placeholder for requests
        //due to correct selector placement when doing connections
        //one place of importance - overlay-manager module
        request(GradientEvent.class);
    }
}
