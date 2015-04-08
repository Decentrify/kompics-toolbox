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
package se.sics.p2ptoolbox.gradient.counter;

import java.util.Random;
import java.util.UUID;
import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Init;
import se.sics.kompics.Positive;
import se.sics.kompics.Start;
import se.sics.kompics.Stop;
import se.sics.kompics.network.Address;
import se.sics.kompics.timer.CancelTimeout;
import se.sics.kompics.timer.SchedulePeriodicTimeout;
import se.sics.kompics.timer.Timeout;
import se.sics.kompics.timer.Timer;
import se.sics.p2ptoolbox.croupier.CroupierPort;
import se.sics.p2ptoolbox.croupier.msg.CroupierSample;
import se.sics.p2ptoolbox.croupier.msg.CroupierUpdate;
import se.sics.p2ptoolbox.gradient.GradientPort;
import se.sics.p2ptoolbox.gradient.msg.GradientSample;
import se.sics.p2ptoolbox.gradient.msg.GradientUpdate;

/**
 * @author Alex Ormenisan <aaor@sics.se>
 */
public class CounterComp extends ComponentDefinition {

    private static final Logger log = LoggerFactory.getLogger(CounterComp.class);

    private Positive gradient = requires(GradientPort.class);
    private Positive croupier = requires(CroupierPort.class);
    private Positive timer = requires(Timer.class);

    private final Address selfAddress;
    private final Random rand;
    private int counter;
    private final long period;
    private final Pair<Double, Integer> rate;
    private final String logPrefix;

    private UUID shuffleCycleId;

    public CounterComp(CounterInit init) {
        this.selfAddress = init.selfAddress;
        this.logPrefix = "id:" + selfAddress;
        log.info("{} initiating...", logPrefix);
        this.rand = init.rand;
        this.counter = 0;
        this.period = init.period;
        this.rate = init.rate;

        subscribe(handleStart, control);
        subscribe(handleStop, control);
        subscribe(handleCroupierSample, croupier);
        subscribe(handleGradientSample, gradient);
        subscribe(handlePeriodicEvent, timer);
    }

    Handler handleStart = new Handler<Start>() {
        @Override
        public void handle(Start event) {
            log.info("{} starting...", logPrefix);
            trigger(new GradientUpdate(new CounterView(counter)), gradient);
            trigger(new CroupierUpdate(new CounterView(counter)), croupier);
            schedulePeriodicShuffle();
        }
    };

    Handler handleStop = new Handler<Stop>() {
        @Override
        public void handle(Stop event) {
            log.info("{} stopping...", logPrefix);
        }
    };
    
     Handler handleCroupierSample = new Handler<CroupierSample>() {

        @Override
        public void handle(CroupierSample sample) {
            log.info("{} Croupier public sample:{}", logPrefix, sample.publicSample);
            log.info("{} Croupier private sample:{}", logPrefix, sample.privateSample);
        }
     };

    Handler handleGradientSample = new Handler<GradientSample>() {

        @Override
        public void handle(GradientSample sample) {
            log.info("{} counter:{} gradient:{}", 
                    new Object[]{logPrefix, counter, sample.gradientSample});
        }
    };

    private int times = 5;
    Handler handlePeriodicEvent = new Handler<ShuffleCycle>() {

        @Override
        public void handle(ShuffleCycle event) {
            if (rand.nextDouble() > rate.getValue0()) {
                times--;
                if(times == 0) {
                    cancelPeriodicShuffle();
                }
                counter = counter + rate.getValue1();
                trigger(new GradientUpdate(new CounterView(counter)), gradient);
                trigger(new CroupierUpdate(new CounterView(counter)), croupier);
            }
        }

    };

    private void schedulePeriodicShuffle() {
        if (shuffleCycleId != null) {
            log.warn("{} double starting periodic shuffle", logPrefix);
            return;
        }
        SchedulePeriodicTimeout spt = new SchedulePeriodicTimeout(period, period);
        ShuffleCycle sc = new ShuffleCycle(spt);
        spt.setTimeoutEvent(sc);
        shuffleCycleId = sc.getTimeoutId();
        trigger(spt, timer);
    }

    private void cancelPeriodicShuffle() {
        if (shuffleCycleId == null) {
            log.warn("{} double stopping periodic shuffle", logPrefix);
            return;
        }
        CancelTimeout cpt = new CancelTimeout(shuffleCycleId);
        shuffleCycleId = null;
        trigger(cpt, timer);
    }

    public static class CounterInit extends Init<CounterComp> {

        public final Address selfAddress;
        public final Random rand;
        public final long period;
        public final Pair<Double, Integer> rate;

        public CounterInit(Address selfAddress, long seed, long period, Pair<Double, Integer> rate) {
            this.selfAddress = selfAddress;
            this.rand = new Random(seed);
            this.period = period;
            this.rate = rate;
        }
    }

    public class ShuffleCycle extends Timeout {

        public ShuffleCycle(SchedulePeriodicTimeout request) {
            super(request);
        }

        @Override
        public String toString() {
            return "SHUFFLE_CYCLE";
        }
    }
}
