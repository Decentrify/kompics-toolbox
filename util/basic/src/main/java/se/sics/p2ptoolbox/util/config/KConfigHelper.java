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
package se.sics.p2ptoolbox.util.config;

import com.google.common.base.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.sics.p2ptoolbox.util.config.KConfigOption.Base;

/**
 *
 * @author Alex Ormenisan <aaor@kth.se>
 */
public class KConfigHelper {

    public static <O extends Object> O read(KConfigCache config, Base<O> opt, Logger LOG, String logPrefix) {
        Optional<O> optValue = config.read(opt);
        if (!optValue.isPresent()) {
            LOG.error("{}missing{}", logPrefix, opt.name);
            throw new RuntimeException("missing" + opt.name);
        }
        return optValue.get();
    }

    public static <O extends Object> O read(KConfigCore config, Base<O> opt) {
        Logger LOG = LoggerFactory.getLogger("KConfig");
        Optional<O> optValue;
        try {
            optValue = config.readValue(opt);
        } catch (IllegalArgumentException ex) {
            throw new RuntimeException(ex);
        }
        if (!optValue.isPresent()) {
            LOG.error("missing:{}", opt.name);
            throw new RuntimeException("missing " + opt.name);
        }
        return optValue.get();
    }
}
