package se.sics.ktoolbox.aggregator.global.example.helper;

import se.sics.ktoolbox.aggregator.global.api.system.DesignInfoContainer;

import java.util.Collection;

/**
 * Design Information Container.
 * Created by babbarshaer on 2015-09-05.
 */
public class PseudoDesignInfoContainer extends DesignInfoContainer<PseudoDesignInfo> {
    
    PseudoDesignInfoContainer(Collection<PseudoDesignInfo> processedWindows) {
        super(processedWindows);
    }
}