package se.sics.p2ptoolbox.election.core.util;

import se.sics.p2ptoolbox.election.api.LCPeerView;
import se.sics.p2ptoolbox.util.network.impl.DecoratedAddress;

import java.util.Collection;

/**
 * Interface used by the leader to filter the samples,
 * at specific points in time.
 *
 * Created by babbarshaer on 2015-03-27.
 */
public interface LeaderFilter {


    /**
     * Invoked by the leader when trying to initiate or extend leadership.
     * Based on the result the leader moves ahead with initiating or extending leadership.
     *
     * @param cohorts followers
     * @return Initiate Leadership.
     */
    public boolean initiateLeadership(Collection<DecoratedAddress> cohorts);


    /**
     * Based on the application protocol, let them decide when do I need to terminate being the leader in case of change
     * of the view ?
     *
     * @param old old view
     * @param updated current view
     * @return terminate leadership
     */
    public boolean terminateLeader(LCPeerView old, LCPeerView updated);

}
