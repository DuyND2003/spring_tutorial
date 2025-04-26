package com.example.demo;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteException;
import org.apache.ignite.Ignition;
import org.apache.ignite.configuration.ClientConnectorConfiguration;
import org.apache.ignite.configuration.DataRegionConfiguration;
import org.apache.ignite.configuration.DataStorageConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.vm.TcpDiscoveryVmIpFinder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;

@Configuration
public class IgniteConfig {

    @Bean
    public Ignite igniteInstance() throws IgniteException {
        IgniteConfiguration igniteConfiguration = new IgniteConfiguration();

        // Thiết lập tên instance
        igniteConfiguration.setIgniteInstanceName("springBootIgniteInstance");

        // Cấu hình bộ nhớ
        DataStorageConfiguration dataStorageConfiguration = new DataStorageConfiguration();
        DataRegionConfiguration dataRegionConfiguration = new DataRegionConfiguration();
        dataRegionConfiguration.setName("Default_Region");
        dataRegionConfiguration.setInitialSize(100 * 1024 * 1024); // 100MB
        dataRegionConfiguration.setMaxSize(200 * 1024 * 1024); // 200MB
        dataStorageConfiguration.setDefaultDataRegionConfiguration(dataRegionConfiguration);
        igniteConfiguration.setDataStorageConfiguration(dataStorageConfiguration);

        // Cấu hình discovery để kết nối với Ignite đang chạy trong Docker
        TcpDiscoverySpi tcpDiscoverySpi = new TcpDiscoverySpi();
        TcpDiscoveryVmIpFinder ipFinder = new TcpDiscoveryVmIpFinder();
        ipFinder.setAddresses(Collections.singletonList("127.0.0.1:47500..47509"));
        tcpDiscoverySpi.setIpFinder(ipFinder);
        igniteConfiguration.setDiscoverySpi(tcpDiscoverySpi);

        // Cấu hình thin client connector
        ClientConnectorConfiguration clientConnectorConfiguration = new ClientConnectorConfiguration();
        clientConnectorConfiguration.setPort(10800);
        igniteConfiguration.setClientConnectorConfiguration(clientConnectorConfiguration);

        // Tạo instance Ignite
        return Ignition.start(igniteConfiguration);
    }
}
