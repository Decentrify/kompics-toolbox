package se.sics.ktoolbox.aggregator.global.api.system;

/**
 * Marker Interface indicating packet information from the component
 * which needs to be captured and aggregated by the local aggregator.
 *
 * Created by babbar on 2015-08-31.
 */
public interface ComponentInfo extends PacketInfo{


    /**
     * The option instructs the component to append the
     * updated information regarding the component to the
     * original one.
     *
     * @param cInfo Component Info
     * @return updated information.
     */
    public ComponentInfo append(ComponentInfo cInfo);

}
