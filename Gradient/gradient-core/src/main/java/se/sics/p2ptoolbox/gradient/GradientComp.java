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
package se.sics.p2ptoolbox.gradient;

import se.sics.p2ptoolbox.gradient.util.GradientView;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.sics.kompics.ClassMatchedHandler;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Init;
import se.sics.kompics.Negative;
import se.sics.kompics.Positive;
import se.sics.kompics.Start;
import se.sics.kompics.Stop;
import se.sics.kompics.network.Network;
import se.sics.kompics.network.Transport;
import se.sics.kompics.timer.CancelPeriodicTimeout;
import se.sics.kompics.timer.CancelTimeout;
import se.sics.kompics.timer.SchedulePeriodicTimeout;
import se.sics.kompics.timer.ScheduleTimeout;
import se.sics.kompics.timer.Timeout;
import se.sics.kompics.timer.Timer;
import se.sics.p2ptoolbox.croupier.CroupierPort;
import se.sics.p2ptoolbox.croupier.msg.CroupierSample;
import se.sics.p2ptoolbox.gradient.msg.GradientSample;
import se.sics.p2ptoolbox.gradient.msg.GradientUpdate;
import se.sics.p2ptoolbox.gradient.msg.GradientShuffle;
import se.sics.p2ptoolbox.gradient.util.GradientContainer;
import se.sics.p2ptoolbox.util.Container;
import se.sics.p2ptoolbox.util.config.SystemConfig;
import se.sics.p2ptoolbox.util.network.impl.BasicContentMsg;
import se.sics.p2ptoolbox.util.network.impl.BasicHeader;
import se.sics.p2ptoolbox.util.network.impl.DecoratedAddress;
import se.sics.p2ptoolbox.util.network.impl.DecoratedHeader;
import se.sics.p2ptoolbox.util.traits.Ageing;
import se.sics.p2ptoolbox.util.traits.OverlayMember;

/**
 *
 * Main Gradient class responsible for shuffling peer views with neighbors. It
 * is responsible for maintaining the gradient and returning periodically
 * gradient sample, to the application.
 *
 */
public class GradientComp extends ComponentDefinition {

    private static final Logger log = LoggerFactory.getLogger(GradientComp.class);

    private final SystemConfig systemConfig;
    private final GradientConfig gradientConfig;
    private final int overlayId;
    private final String logPrefix;

    private GradientFilter filter;
    private GradientContainer selfView;
    private final GradientView view;

    private UUID shuffleCycleId;
    private UUID shuffleTimeoutId;

    // == Identify Ports.
    Negative<GradientPort> gradientPort = provides(GradientPort.class);
    Positive<Network> network = requires(Network.class);
    Positive<Timer> timer = requires(Timer.class);
    Positive<CroupierPort> croupierPort = requires(CroupierPort.class);

    public GradientComp(GradientInit init) {
        this.systemConfig = init.systemConfig;
        this.gradientConfig = init.gradientConfig;
        this.overlayId = init.overlayId;
        this.logPrefix = "id:" + systemConfig.self.getBase().toString() + ":" + overlayId;
        log.info("{} initializing...", logPrefix);
        this.view = new GradientView(logPrefix, init.utilityComparator, init.gradientFilter, gradientConfig.viewSize, new Random(systemConfig.seed), gradientConfig.exchangeSMTemp);
        this.filter = init.gradientFilter;

        subscribe(handleStart, control);
        subscribe(handleStop, control);

        subscribe(handleUpdate, gradientPort);
        subscribe(handleCroupierSample, croupierPort);
        subscribe(handleShuffleRequest, network);
        subscribe(handleShuffleResponse, network);
        subscribe(handleShuffleCycle, timer);
        subscribe(handleShuffleTimeout, timer);
    }

    //*********Control**********************************************************
    Handler<Start> handleStart = new Handler<Start>() {
        @Override
        public void handle(Start event) {
            log.info("{} starting...", logPrefix);
        }
    };

    Handler<Stop> handleStop = new Handler<Stop>() {
        @Override
        public void handle(Stop event) {
            log.info("{} stopping...", logPrefix);

        }
    };

    //**************************************************************************
    private boolean haveShufflePartners() {
        return !view.isEmpty();
    }

    private boolean connected() {
        return shuffleCycleId != null;
    }

    //**************************************************************************
    Handler<GradientUpdate> handleUpdate = new Handler<GradientUpdate>() {
        @Override
        public void handle(GradientUpdate update) {
            log.trace("{} {}", logPrefix, update);
            log.debug("{} updating self peer view:{}", new Object[]{logPrefix, update.view});
            if (selfView != null && filter.cleanOldView(selfView.getContent(), update.view)) {
                view.clean(update.view);
            }
            selfView = new GradientContainer(systemConfig.self, update.view);
            if (!connected() && haveShufflePartners()) {
                schedulePeriodicShuffle();
            }
        }
    };

    /**
     * Samples from Croupier used for bootstrapping gradient as well as faster
     * convergence(random samples)
     */
    Handler handleCroupierSample = new Handler<CroupierSample<? extends Object>>() {
        @Override
        public void handle(CroupierSample<? extends Object> sample) {
            log.trace("{} {}", logPrefix, sample);
            log.debug("{} \nCroupier public sample:{} \nCroupier private sample:{}",
                    new Object[]{logPrefix, sample.publicSample, sample.privateSample});

            Set<GradientContainer> gradientCopy = new HashSet<GradientContainer>();
            for (Container<DecoratedAddress, ? extends Object> container : sample.publicSample) {
                int age = 0;
                if (container instanceof Ageing) {
                    age = ((Ageing) container).getAge();
                }
                gradientCopy.add(new GradientContainer(container.getSource(), container.getContent(), age));
            }
            for (Container<DecoratedAddress, ? extends Object> container : sample.privateSample) {
                int age = 0;
                if (container instanceof Ageing) {
                    age = ((Ageing) container).getAge();
                }
                gradientCopy.add(new GradientContainer(container.getSource(), container.getContent(), age));
            }
            view.merge(gradientCopy, selfView);
            if (!connected() && haveShufflePartners()) {
                schedulePeriodicShuffle();
            }
        }
    };

    /**
     * Periodic Gradient Shuffle
     */
    Handler<ShuffleCycle> handleShuffleCycle = new Handler<ShuffleCycle>() {
        @Override
        public void handle(ShuffleCycle event) {
            log.trace("{} {}", logPrefix);

            if (!haveShufflePartners()) {
                log.warn("{} no shuffle partners - disconnected", logPrefix);
                cancelPeriodicShuffle();;
                return;
            }

            if (!view.isEmpty()) {
                log.info("{} view:{}", logPrefix, view.getAllCopy());
                trigger(new GradientSample(view.getAllCopy()), gradientPort);
            }

            // NOTE:
            GradientContainer partner = view.getShuffleNode(selfView);
            view.incrementAges();

            Set<GradientContainer> exchangeGC = view.getExchangeCopy(partner, gradientConfig.shuffleSize);
            DecoratedHeader<DecoratedAddress> requestHeader = new DecoratedHeader(new BasicHeader(systemConfig.self, partner.getSource(), Transport.UDP), null, overlayId);
            GradientShuffle.Request requestContent = new GradientShuffle.Request(UUID.randomUUID(), selfView, exchangeGC);
            BasicContentMsg request = new BasicContentMsg(requestHeader, requestContent);
            log.debug("{} sending:{} to:{}", new Object[]{logPrefix, requestContent.exchangeNodes, partner.getSource()});
            trigger(request, network);
            scheduleShuffleTimeout(partner.getSource());
        }
    };

    Handler<ShuffleTimeout> handleShuffleTimeout = new Handler<ShuffleTimeout>() {

        @Override
        public void handle(ShuffleTimeout event) {
            log.trace("{} {}", logPrefix, event);
            if (shuffleTimeoutId == null) {
                log.debug("{} late timeout {}", logPrefix, event);
                return;
            } else {
                log.debug("{} node:{} timed out", logPrefix, event.dest);
                shuffleTimeoutId = null;
                view.clean(event.dest);
            }
        }
    };

    ClassMatchedHandler handleShuffleRequest
            = new ClassMatchedHandler<GradientShuffle.Request, BasicContentMsg<DecoratedAddress, DecoratedHeader<DecoratedAddress>, GradientShuffle.Request>>() {

                @Override
                public void handle(GradientShuffle.Request content, BasicContentMsg<DecoratedAddress, DecoratedHeader<DecoratedAddress>, GradientShuffle.Request> container) {
                    DecoratedHeader<DecoratedAddress> header = container.getHeader();
                    if (header.getTrait(OverlayMember.class).getOverlayId() != overlayId) {
                        log.error("{} message with header:{} not belonging to gradient overlay:{}", new Object[]{logPrefix, header, overlayId});
                        throw new RuntimeException("message not belonging to gradient overlay");
                    }
                    DecoratedAddress reqSrc = container.getHeader().getSource();
                    if (systemConfig.self.getBase().equals(reqSrc.getBase())) {
                        log.error("{} Tried to shuffle with myself", logPrefix);
                        throw new RuntimeException("tried to shuffle with myself");
                    }
                    log.debug("{} received:{} from:{}", new Object[]{logPrefix, content.exchangeNodes, reqSrc});
                    if (selfView == null) {
                        log.warn("{} not ready to shuffle - no self view available - {} tried to shuffle with me",
                                logPrefix, reqSrc);
                        return;
                    }

                    view.incrementAges();

                    Set<GradientContainer> exchangeGC = view.getExchangeCopy(content.selfGC, gradientConfig.shuffleSize);
                    DecoratedHeader<DecoratedAddress> responseHeader = new DecoratedHeader(new BasicHeader(systemConfig.self, container.getHeader().getSource(), Transport.UDP), null, overlayId);
                    GradientShuffle.Response responseContent = new GradientShuffle.Response(content.getId(), selfView, exchangeGC);
                    BasicContentMsg request = new BasicContentMsg(responseHeader, responseContent);
                    log.debug("{} sending:{} to:{}", new Object[]{logPrefix, responseContent.exchangeNodes, container.getHeader().getSource()});
                    trigger(request, network);

                    view.merge(content.exchangeNodes, selfView);
                    view.merge(content.selfGC, selfView);
                }
            };

    ClassMatchedHandler handleShuffleResponse
            = new ClassMatchedHandler<GradientShuffle.Response, BasicContentMsg<DecoratedAddress, DecoratedHeader<DecoratedAddress>, GradientShuffle.Response>>() {

                @Override
                public void handle(GradientShuffle.Response content, BasicContentMsg<DecoratedAddress, DecoratedHeader<DecoratedAddress>, GradientShuffle.Response> container) {
                    DecoratedHeader<DecoratedAddress> header = container.getHeader();
                    if (header.getTrait(OverlayMember.class).getOverlayId() != overlayId) {
                        log.error("{} message with header:{} not belonging to gradient overlay:{}", new Object[]{logPrefix, header, overlayId});
                        throw new RuntimeException("message not belonging to gradient overlay");
                    }
                    DecoratedAddress respSrc = container.getHeader().getSource();
                    if (systemConfig.self.getBase().equals(respSrc.getBase())) {
                        log.error("{} Tried to shuffle with myself", logPrefix);
                        throw new RuntimeException("tried to shuffle with myself");
                    }
                    log.debug("{} received:{} from:{}", new Object[]{logPrefix, content.exchangeNodes, respSrc});

                    if (shuffleTimeoutId == null) {
                        log.debug("{} req:{}  already timed out", new Object[]{logPrefix, content.getId(), respSrc});
                        return;
                    }
                    cancelShuffleTimeout();

                    view.merge(content.exchangeNodes, selfView);
                    view.merge(content.selfGC, selfView);
                }
            };

    private void schedulePeriodicShuffle() {
        if (shuffleCycleId != null) {
            log.warn("{} double starting periodic shuffle", logPrefix);
            return;
        }
        SchedulePeriodicTimeout spt = new SchedulePeriodicTimeout(gradientConfig.shufflePeriod, gradientConfig.shufflePeriod);
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
        CancelPeriodicTimeout cpt = new CancelPeriodicTimeout(shuffleCycleId);
        shuffleCycleId = null;
        trigger(cpt, timer);
    }

    private void scheduleShuffleTimeout(DecoratedAddress dest) {
        if (shuffleTimeoutId != null) {
            log.warn("{} double starting shuffle timeout", logPrefix);
            return;
        }
        ScheduleTimeout spt = new ScheduleTimeout(gradientConfig.shufflePeriod / 2);
        ShuffleTimeout sc = new ShuffleTimeout(spt, dest);
        spt.setTimeoutEvent(sc);
        shuffleTimeoutId = sc.getTimeoutId();
        trigger(spt, timer);
    }

    private void cancelShuffleTimeout() {
        if (shuffleTimeoutId == null) {
            log.warn("{} double stopping shuffle timeout", logPrefix);
        }
        CancelTimeout cpt = new CancelTimeout(shuffleTimeoutId);
        shuffleTimeoutId = null;
        trigger(cpt, timer);
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

    public class ShuffleTimeout extends Timeout {

        public final DecoratedAddress dest;

        public ShuffleTimeout(ScheduleTimeout request, DecoratedAddress dest) {
            super(request);
            this.dest = dest;
        }

        @Override
        public String toString() {
            return "SHUFFLE_TIMEOUT";
        }
    }

    public static class GradientInit extends Init<GradientComp> {

        public final SystemConfig systemConfig;
        public final GradientConfig gradientConfig;
        public final int overlayId;
        public final Comparator utilityComparator;
        public final GradientFilter gradientFilter;

        public GradientInit(SystemConfig systemConfig, GradientConfig config, int overlayId,
                Comparator utilityComparator, GradientFilter gradientFilter) {
            this.systemConfig = systemConfig;
            this.gradientConfig = config;
            this.overlayId = overlayId;
            this.utilityComparator = utilityComparator;
            this.gradientFilter = gradientFilter;
        }
    }
}