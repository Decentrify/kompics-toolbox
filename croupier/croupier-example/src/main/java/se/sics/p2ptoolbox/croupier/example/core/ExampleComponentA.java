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
package se.sics.p2ptoolbox.croupier.example.core;

import java.util.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Init;
import se.sics.kompics.Negative;
import se.sics.kompics.Positive;
import se.sics.kompics.Start;
import se.sics.kompics.Stop;
import se.sics.kompics.network.Address;
import se.sics.p2ptoolbox.croupier.CroupierPort;
import se.sics.p2ptoolbox.croupier.msg.CroupierSample;
import se.sics.p2ptoolbox.croupier.msg.CroupierUpdate;
import se.sics.p2ptoolbox.util.update.SelfViewUpdatePort;

/**
 * @author Alex Ormenisan <aaor@sics.se>
 */
public class ExampleComponentA extends ComponentDefinition {

    private static final Logger log = LoggerFactory.getLogger(ExampleComponentA.class);

    private Positive croupier = requires(CroupierPort.class);
    private Negative viewUpdate = provides(SelfViewUpdatePort.class);

    private final Address selfAddress;
    private final Random rand;
    private boolean flag;
    private boolean observer;

    public ExampleComponentA(ExampleInitA init) {
        this.selfAddress = init.selfAddress;
        log.info("{} intiating...", selfAddress);
        this.rand = init.rand;
        this.flag = true;
        this.observer = init.observer;
        
        subscribe(handleStart, control);
        subscribe(handleStop, control);
        subscribe(handleCroupierSample, croupier);
    }

    Handler<Start> handleStart = new Handler<Start>() {

        @Override
        public void handle(Start event) {
            log.info("{} starting...", selfAddress);
            log.debug("sending first self update");
            trigger(new CroupierUpdate(new PeerViewA(flag, observer)), viewUpdate);
        }
    };
    Handler<Stop> handleStop = new Handler<Stop>() {

        @Override
        public void handle(Stop event) {
            log.info("{} stopping...", selfAddress);
        }
    };

    Handler<CroupierSample> handleCroupierSample = new Handler<CroupierSample>() {

        @Override
        public void handle(CroupierSample sample) {
            log.info("\n{} Croupier public sample:{} \n{} Croupier private sample:{}", 
                    new Object[]{selfAddress, sample.publicSample, selfAddress, sample.privateSample});
            if (rand.nextDouble() > 0.7) {
                flag = !flag;
                trigger(new CroupierUpdate(new PeerViewA(flag, observer)), viewUpdate);
            }
        }
    };

    public static class ExampleInitA extends Init<ExampleComponentA> {

        public final Address selfAddress;
        public final Random rand;
        public final boolean observer;

        public ExampleInitA(Address selfAddress, long seed, boolean observer) {
            this.selfAddress = selfAddress;
            this.rand = new Random(seed + 1);
            this.observer = observer;
        }
    }
}
