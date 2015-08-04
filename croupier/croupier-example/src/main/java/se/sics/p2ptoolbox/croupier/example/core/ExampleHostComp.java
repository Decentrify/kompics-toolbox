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

import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.sics.kompics.Component;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Init;
import se.sics.kompics.Positive;
import se.sics.kompics.Start;
import se.sics.kompics.Stop;
import se.sics.kompics.network.Network;
import se.sics.kompics.timer.Timer;
import se.sics.p2ptoolbox.croupier.CroupierComp;
import se.sics.p2ptoolbox.croupier.CroupierConfig;
import se.sics.p2ptoolbox.croupier.CroupierControlPort;
import se.sics.p2ptoolbox.croupier.CroupierPort;
import se.sics.p2ptoolbox.croupier.msg.CroupierDisconnected;
import se.sics.p2ptoolbox.croupier.msg.CroupierJoin;
import se.sics.p2ptoolbox.util.config.SystemConfig;
import se.sics.p2ptoolbox.util.filters.IntegerOverlayFilter;
import se.sics.p2ptoolbox.util.network.impl.DecoratedAddress;

/**
 * @author Alex Ormenisan <aaor@sics.se>
 */
public class ExampleHostComp extends ComponentDefinition {

    private static final Logger log = LoggerFactory.getLogger(ExampleHostComp.class);

    private Positive<Network> network = requires(Network.class);
    private Positive<Timer> timer = requires(Timer.class);

    private final SystemConfig systemConfig;
    private final CroupierConfig croupierConfig;
    private final Set<DecoratedAddress> bootstrapNodes;
    
    private Component croupier;
    private Component compA;

    private final String logPrefix;

    public ExampleHostComp(HostInit init) {
        this.systemConfig = init.systemConfig;
        this.croupierConfig = init.croupierConfig;
        this.bootstrapNodes = init.bootstrapNodes;
        this.logPrefix = systemConfig.self.toString();
        log.info("{} initiating...", logPrefix);

        createNConnectCroupier(10);
        createNConnectCompA();

        subscribe(handleDisconnected, croupier.getPositive(CroupierControlPort.class));
        subscribe(handleStart, control);
        subscribe(handleStop, control);
    }

    private void createNConnectCroupier(int overlayId) {
        croupier = create(CroupierComp.class, new CroupierComp.CroupierInit(systemConfig, croupierConfig, overlayId));
        connect(croupier.getNegative(Network.class), network, new IntegerOverlayFilter(overlayId));
        connect(croupier.getNegative(Timer.class), timer);
    }

    private void createNConnectCompA() {
        compA = create(ExampleComponentA.class, new ExampleComponentA.ExampleInitA(systemConfig.self, systemConfig.seed));
        connect(croupier.getPositive(CroupierPort.class), compA.getNegative(CroupierPort.class));
    }

    private Handler<Start> handleStart = new Handler<Start>() {
        @Override
        public void handle(Start event) {
            log.info("{} starting...", logPrefix);
            trigger(new CroupierJoin(bootstrapNodes), croupier.getPositive(CroupierControlPort.class));
        }
    };

    private Handler<Stop> handleStop = new Handler<Stop>() {
        @Override
        public void handle(Stop event) {
            log.info("{} stopping...", logPrefix);
        }
    };

    private Handler<CroupierDisconnected> handleDisconnected = new Handler<CroupierDisconnected>() {
        @Override
        public void handle(CroupierDisconnected event) {
            log.info("{} croupier:{} disconnected", logPrefix, event.overlayId);
        }
    };

    public static class HostInit extends Init<ExampleHostComp> {

        public final SystemConfig systemConfig;
        public final CroupierConfig croupierConfig;
        public final Set<DecoratedAddress> bootstrapNodes;

        public HostInit(SystemConfig systemConfig, CroupierConfig croupierConfig, Set<DecoratedAddress> bootstrapNodes) {
            this.systemConfig = systemConfig;
            this.croupierConfig = croupierConfig;
            this.bootstrapNodes = bootstrapNodes;
        }
    }
}