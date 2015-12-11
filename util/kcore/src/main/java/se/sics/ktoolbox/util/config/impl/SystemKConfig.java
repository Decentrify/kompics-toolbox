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
package se.sics.ktoolbox.util.config.impl;

import com.google.common.base.Optional;
import java.security.SecureRandom;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import se.sics.ktoolbox.util.config.KConfigCore;
import se.sics.ktoolbox.util.config.KConfigOption.Basic;
import se.sics.ktoolbox.util.config.options.BasicAddressOption;

/**
 * @author Alex Ormenisan <aaor@kth.se>
 */
public class SystemKConfig {
    public final static Basic<Long> seed = new Basic("system.seed", Long.class);
    public final static Basic<Integer> id = new Basic("system.id", Integer.class);
    public static BasicAddressOption aggregator = new BasicAddressOption("system.aggregator");
    
    public String toString() {
        return "SystemKConfig";
    }
    
    public static void setup(KConfigCore config) {
        Long rSeed;
        Optional<Long> readSeed = config.readValue(seed);
        if(!readSeed.isPresent()) {
            SecureRandom rand = new SecureRandom();
            rSeed = rand.nextLong();
            config.writeValue(seed, rSeed);
        } else {
            rSeed = readSeed.get();
        }
        Optional<Integer> readId = config.readValue(SystemKConfig.id);
        if(!readId.isPresent()) {
            Random rand = new Random(rSeed);
            config.writeValue(id, rand.nextInt());
        }
    }
}