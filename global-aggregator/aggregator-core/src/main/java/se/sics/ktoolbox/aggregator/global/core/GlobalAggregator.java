package se.sics.ktoolbox.aggregator.global.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.sics.kompics.*;
import se.sics.kompics.network.Network;
import se.sics.kompics.timer.SchedulePeriodicTimeout;
import se.sics.kompics.timer.Timer;
import se.sics.ktoolbox.aggregator.global.api.PacketContainer;
import se.sics.ktoolbox.aggregator.global.api.PacketInfo;
import se.sics.ktoolbox.aggregator.global.core.event.AggregatedInfo;
import se.sics.ktoolbox.aggregator.global.core.ports.GlobalAggregatorPort;
import se.sics.ktoolbox.aggregator.global.core.util.AggregationTimeout;
import se.sics.p2ptoolbox.util.network.impl.BasicAddress;
import se.sics.p2ptoolbox.util.network.impl.BasicContentMsg;
import se.sics.p2ptoolbox.util.network.impl.DecoratedAddress;
import se.sics.p2ptoolbox.util.network.impl.DecoratedHeader;

import java.util.*;

/**
 * Main global aggregator component.
 *
 * Created by babbarshaer on 2015-09-01.
 */
public class GlobalAggregator extends ComponentDefinition {

    Logger logger = LoggerFactory.getLogger(GlobalAggregator.class);
    private long timeout;
    private Map<BasicAddress, List<PacketInfo>> nodePacketMap;

    Positive<Network> network = requires(Network.class);
    Positive<Timer> timer = requires(Timer.class);
    Negative<GlobalAggregatorPort> aggregatorPort = provides(GlobalAggregatorPort.class);


    public GlobalAggregator(GlobalAggregatorInit init){

        doInit(init);
        subscribe(startHandler, control);
        subscribe(timeoutHandler, timer);
        subscribe(packetMsgHandler, network);
    }

    /**
     * Initialize the global aggregator.
     * @param init
     */
    private void doInit(GlobalAggregatorInit init) {

        logger.debug("Initializing the global aggregator.");
        this.timeout = init.timeout;
        this.nodePacketMap = new HashMap<BasicAddress, List<PacketInfo>>();
    }


    /**
     * Start handler for the component. Invoke other components
     * if any in the start handler.
     */
    Handler<Start> startHandler = new Handler<Start>() {
        @Override
        public void handle(Start start) {

            logger.debug("Component Started.");
            doStart();
        }
    };

    /**
     * Initiate the start of the system. All the necessary components
     * needs to be booted up before the
     */
    private void doStart() {

        logger.debug("Starting the component.");

        SchedulePeriodicTimeout spt = new SchedulePeriodicTimeout(timeout, timeout);
        AggregationTimeout agt = new AggregationTimeout(spt);
        spt.setTimeoutEvent(agt);

        trigger(spt, timer);
    }


    /**
     * Handler for aggregation timeout.
     */
    Handler<AggregationTimeout> timeoutHandler = new Handler<AggregationTimeout>() {
        @Override
        public void handle(AggregationTimeout aggregationTimeout) {

            logger.debug("Aggregation timeout handler invoked, forwarding the aggregated information.");
            trigger(new AggregatedInfo(nodePacketMap), aggregatorPort);

//          Clear the map for the next round.
            nodePacketMap = new HashMap<BasicAddress, List<PacketInfo>>();
        }
    };


    /**
     * Network message handler containing the packet information from the
     * node in the system.
     *
     */
    ClassMatchedHandler<PacketContainer, BasicContentMsg<DecoratedAddress, DecoratedHeader<DecoratedAddress>, PacketContainer>> packetMsgHandler = new ClassMatchedHandler<PacketContainer, BasicContentMsg<DecoratedAddress, DecoratedHeader<DecoratedAddress>, PacketContainer>>() {
        @Override
        public void handle(PacketContainer packetContainer, BasicContentMsg<DecoratedAddress, DecoratedHeader<DecoratedAddress>, PacketContainer> event) {

            logger.debug("Handler for the packet container from the node in the system.");
            PacketContainer container = event.getContent();

            BasicAddress sourceAddress = container.sourceAddress.getBase();
            List<PacketInfo> packets = nodePacketMap.get(sourceAddress);

            if(packets == null){
                packets = new ArrayList<PacketInfo>();
                nodePacketMap.put(sourceAddress, packets);
            }

            packets.add(container.packetInfo);
        }
    };

}
