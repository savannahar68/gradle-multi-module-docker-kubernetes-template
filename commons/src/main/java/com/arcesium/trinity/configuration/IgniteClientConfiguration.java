package com.example.org.configuration;

import org.apache.ignite.Ignite;
import org.apache.ignite.Ignition;
import org.apache.ignite.configuration.DeploymentMode;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.kubernetes.configuration.KubernetesConnectionConfiguration;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.kubernetes.TcpDiscoveryKubernetesIpFinder;
import org.apache.ignite.spi.discovery.tcp.ipfinder.vm.TcpDiscoveryVmIpFinder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.Arrays;

@Configuration
public class IgniteClientConfiguration {
    private static final Logger LOGGER = LogManager.getLogger(IgniteClientConfiguration.class);

    private static final String PROP_IGNITE_ADDRESSES = "properties.ignite.addresses";
    private static final String PROP_IGNITE_NAMESPACE = "properties.ignite.namespace";
    private static final String PROP_IGNITE_SERVICENAME = "properties.ignite.serviceName";
    private static final String PROP_IGNITE_NUM_PARTITIONS = "properties.ignite.numPartitions";

    private final Environment env;
    private final ClientConfiguration ClientConfiguration;
    private final int numPartitions;
    private Ignite ignite = null;

    @Autowired
    public IgniteClientConfiguration(Environment env, ClientConfiguration ClientConfiguration) {
        this.env = env;
        this.ClientConfiguration = ClientConfiguration;
        this.numPartitions = env.getProperty(PROP_IGNITE_NUM_PARTITIONS, Integer.class, 128);
    }

    public Ignite igniteInstance() {
        System.out.println(env);
        String igniteAddressesStr = env.getProperty(PROP_IGNITE_ADDRESSES, "127.0.0.1:47500..47509");
        IgniteConfiguration igniteCfg = new IgniteConfiguration();
        // Makes application classes available to Ignite grid
        igniteCfg.setPeerClassLoadingEnabled(true);
        igniteCfg.setDeploymentMode(DeploymentMode.CONTINUOUS);

        // Client mode doesn't hold data, but enabled us to maintain near caches.
        igniteCfg.setClientMode(true);

        // Discover Ignite grid nodes using TCP
        TcpDiscoverySpi spi = new TcpDiscoverySpi();
        if(igniteAddressesStr.contains("127.0.0.1")) {
            TcpDiscoveryVmIpFinder ipFinder = new TcpDiscoveryVmIpFinder();
            // Set initial IP addresses.
            // Note that you can optionally specify a port or a port range.
            ipFinder.setAddresses(Arrays.asList(igniteAddressesStr.split(",")));
            spi.setIpFinder(ipFinder);
        } else {
            String namespace = env.getProperty(PROP_IGNITE_NAMESPACE);
            String serviceName = env.getProperty(PROP_IGNITE_SERVICENAME); // Ignite server service name
            KubernetesConnectionConfiguration kubeConfig = new KubernetesConnectionConfiguration();
            kubeConfig.setNamespace(namespace);
            kubeConfig.setServiceName(serviceName);
            TcpDiscoveryKubernetesIpFinder ipFinder = new TcpDiscoveryKubernetesIpFinder(kubeConfig);

            spi.setIpFinder(ipFinder);
            igniteCfg.setDiscoverySpi(spi);
        }

        LOGGER.info("Initiating Ignite connection with addresses: {}", igniteAddressesStr);
        ignite = Ignition.start(igniteCfg);
        return ignite;
    }

    public synchronized Ignite getIgnite() {
        if (ignite != null) {
            return ignite;
        }
        else return igniteInstance();
    }
}
