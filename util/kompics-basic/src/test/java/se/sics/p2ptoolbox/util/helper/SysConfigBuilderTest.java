package se.sics.p2ptoolbox.util.helper;

import com.google.common.collect.ImmutableMap;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.sics.caracaldb.Address;
import se.sics.p2ptoolbox.util.config.SystemConfig;
import se.sics.p2ptoolbox.util.network.impl.BasicAddress;
import se.sics.p2ptoolbox.util.network.impl.DecoratedAddress;

import java.net.InetAddress;
import java.net.UnknownHostException;
import se.sics.p2ptoolbox.util.nat.NatedTrait;
import se.sics.p2ptoolbox.util.traits.AcceptedTraits;

/**
 * Testing the system configuration builder.
 *
 * Created by babbar on 2015-08-25.
 */
public class SysConfigBuilderTest {

    public static Config config;
    private static Logger logger = LoggerFactory.getLogger(SysConfigBuilderTest.class);

    @BeforeClass
    public static void beforeClass() {
        logger.debug("Starting with the load of the default configuration.");
        config = ConfigFactory.load("application.conf");
        
        ImmutableMap acceptedTraits = ImmutableMap.of(NatedTrait.class, 0);
        DecoratedAddress.setAcceptedTraits(new AcceptedTraits(acceptedTraits));
    }

    @AfterClass
    public static void afterClass() {
        config = null;

    }

    @Test
    public void configBuilderTest() throws UnknownHostException {

        logger.debug("Initiating test for the builder created with config.");

        SystemConfigBuilder builder = new SystemConfigBuilder(config);
        SystemConfig sysConfig = builder.build();

        DecoratedAddress expectedAddress = getSelfAddress(config);
        DecoratedAddress actualAddress = sysConfig.self;

        Assert.assertEquals("Self Address comparison", expectedAddress, actualAddress);

        logger.debug("Moving onto comparison for the caracal address.");

        Address expectedCaracalAddress = getCaracalAddress(config);
        Address actualCaracalAddress = sysConfig.caracalAddress.get();

        Assert.assertEquals("Caracal Address Comparison", expectedCaracalAddress, actualCaracalAddress);

        logger.debug("Moving onto comparison for the aggregator address");

        DecoratedAddress expectedAggregatorAddress = getAggregatorAddress(config);
        DecoratedAddress actualAggregatorAddress = sysConfig.aggregator.get();

        Assert.assertEquals("Aggregator Address comparison", expectedAggregatorAddress, actualAggregatorAddress);
    }

    /**
     * Extract the Self Address from the configuration provided.
     *
     * @param config configuration
     * @return SelfAddress
     */
    private DecoratedAddress getSelfAddress(Config config) throws UnknownHostException {

        InetAddress selfIp = InetAddress.getByName(config.getString("system.self.ip"));
        Integer selfPort = config.getInt("system.self.port");
        Integer selfId = config.getInt("system.self.id");

        return new DecoratedAddress(new BasicAddress(selfIp, selfPort, selfId));
    }

    /**
     * Extract the address for the caracal which will enable the application to
     * fetch the nodes for the bootstrap.
     *
     * @param config config.
     * @return CaracalAddress
     */
    private Address getCaracalAddress(Config config) {

        Address caracalAddress = null;

        try {

            InetAddress ip = InetAddress.getByName(config.getString("caracal.address.ip"));
            int port = config.getInt("caracal.address.port");
            caracalAddress = new Address(ip, port, null);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        return caracalAddress;
    }

    /**
     * Extract the address of the aggregator from the configuration.
     *
     * @param config config.
     * @return AggregatorAddress.
     */
    private DecoratedAddress getAggregatorAddress(Config config) throws UnknownHostException {
        InetAddress aggregatorIp = InetAddress.getByName(config.getString("system.aggregator.ip"));
        int aggregatorPort = config.getInt("system.aggregator.port");
        int aggregatorId = config.getInt("system.aggregator.id");
        return DecoratedAddress.open(aggregatorIp, aggregatorPort, aggregatorId);
    }
}
