package se.sics.ktoolbox.cc.sim;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.sics.kompics.*;
import se.sics.kompics.network.Network;
import se.sics.kompics.network.Transport;
import se.sics.kompics.timer.SchedulePeriodicTimeout;
import se.sics.kompics.timer.Timeout;
import se.sics.kompics.timer.Timer;
import se.sics.ktoolbox.cc.sim.msg.OverlaySample;
import se.sics.ktoolbox.cc.sim.msg.PutRequest;
import se.sics.p2ptoolbox.util.network.impl.BasicContentMsg;
import se.sics.p2ptoolbox.util.network.impl.DecoratedAddress;
import se.sics.p2ptoolbox.util.network.impl.DecoratedHeader;

import java.util.*;

/**
 * Main class used to replace the actual caracal client.
 * The simulation class is simply used to store the overlay information
 * along with the address of the node that pinged with this information.
 *
 * Created by babbar on 2015-08-15.
 */
public class CCSimMain extends ComponentDefinition {

    private Logger logger = LoggerFactory.getLogger(CCSimMain.class);
    private Positive<Network> network = requires(Network.class);
    private Positive<Timer> timer = requires(Timer.class);

    private Map<ByteBufferWrapper, List<DecoratedAddress>> serviceAddressMap;
    private int slotLength;
    private DecoratedAddress selfAddress;

    public CCSimMain(CCSimMainInit init){

        doInit(init);
        subscribe(startHandler, control);
        subscribe(putRequestHandler, network);
        subscribe(overlayRequestHandler, network);
    }

    private void doInit(CCSimMainInit init) {

        logger.debug("Perform the initialization tasks.");
        this.slotLength = init.slotLength;
        this.selfAddress = init.address;
        this.serviceAddressMap = new HashMap<ByteBufferWrapper, List<DecoratedAddress>>();

    }

    Handler<Start> startHandler = new Handler<Start>() {
        @Override
        public void handle(Start event) {
            logger.debug("Component booted up, self Address: {}", selfAddress);
        }
    };


    /**
     * Handler for the overlay sample request from the heartbeat component, requesting the information
     * regarding the neighbors for the provided overlay.
     *
     */
    ClassMatchedHandler<OverlaySample.Request, BasicContentMsg<DecoratedAddress, DecoratedHeader<DecoratedAddress>, OverlaySample.Request>> overlayRequestHandler = new ClassMatchedHandler<OverlaySample.Request, BasicContentMsg<DecoratedAddress, DecoratedHeader<DecoratedAddress>, OverlaySample.Request>>() {
        @Override
        public void handle(OverlaySample.Request content, BasicContentMsg<DecoratedAddress, DecoratedHeader<DecoratedAddress>, OverlaySample.Request> context) {
            logger.debug("Received overlay sample request.");

            Set<DecoratedAddress> address = new HashSet<DecoratedAddress>();
            List<DecoratedAddress> serviceAddressList =  serviceAddressMap.get(new ByteBufferWrapper(content.overlayIdentifier));

            if(serviceAddressList != null){
                address.addAll(serviceAddressList);
            }

            OverlaySample.Response response = new OverlaySample.Response(content.overlayIdentifier, address);
            DecoratedHeader<DecoratedAddress> header = new DecoratedHeader<DecoratedAddress>(selfAddress, context.getSource(), Transport.UDP);
            BasicContentMsg<DecoratedAddress, DecoratedHeader<DecoratedAddress>, OverlaySample.Response> contentMsg = new BasicContentMsg<DecoratedAddress, DecoratedHeader<DecoratedAddress>, OverlaySample.Response>(header, response);

            trigger(contentMsg, network);
        }
    };


    /**
     * Handler for the put request from the heartbeat components providing the information about the
     * services to be registered by the
     */
    ClassMatchedHandler<PutRequest, BasicContentMsg<DecoratedAddress, DecoratedHeader<DecoratedAddress>, PutRequest>> putRequestHandler  = new ClassMatchedHandler<PutRequest, BasicContentMsg<DecoratedAddress, DecoratedHeader<DecoratedAddress>, PutRequest>>() {
        @Override
        public void handle(PutRequest content, BasicContentMsg<DecoratedAddress, DecoratedHeader<DecoratedAddress>, PutRequest> context) {

            logger.trace("Received put request from the heartbeat component.");

            Set<byte[]> serviceIdentifiers = content.serviceIdentifiers;
            DecoratedAddress clientAddress = content.selfAddress;

            for(byte[] serviceIdentifier : serviceIdentifiers){

                List<DecoratedAddress> value = serviceAddressMap.get(new ByteBufferWrapper(serviceIdentifier));
                if(value == null){
                    value = new ArrayList<DecoratedAddress>();
                    serviceAddressMap.put(new ByteBufferWrapper(serviceIdentifier), value);
                }

//              At this point we are assuming that the decorated address will not change. ( As used in simulation. )

                if(!value.contains(clientAddress))
                    value.add(clientAddress);

                if(value.size() > slotLength){
                    value.remove(0);
                }
            }
        }
    };

}
