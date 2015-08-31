package se.sics.ktoolbox.aggregator.local.ports;


import se.sics.kompics.PortType;
import se.sics.ktoolbox.aggregator.local.events.ComponentInfoEvent;

/**
 * Main Port for interaction with the local aggregator.
 *
 * Created by babbar on 2015-08-31.
 */
public class LocalAggregatorPort extends PortType {{

    request(ComponentInfoEvent.class);
}}