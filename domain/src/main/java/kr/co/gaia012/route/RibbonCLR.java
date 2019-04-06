package kr.co.gaia012.route;

import com.netflix.loadbalancer.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@AllArgsConstructor
@Component
public class RibbonCLR implements CommandLineRunner {

    private final DiscoveryClient discoveryClient;
    ConfigurationBasedServerList list;

    @Override
    public void run(String... args) throws Exception {
        String serviceId = "community-cloud-config-server";


        List<Server> servers = this.discoveryClient
                .getInstances(serviceId)
                .stream()
                .map(si -> new Server(si.getHost(), si.getPort()))
                .collect(Collectors.toList());
        IRule roundRobinRule = new RoundRobinRule();

        BaseLoadBalancer loadBalancer = LoadBalancerBuilder.newBuilder()
                .buildFixedServerListLoadBalancer(servers);

        IntStream.range(0, 10).forEach(i -> {
            Server server = loadBalancer.chooseServer();
            URI uri = URI.create("http://" + server.getHost() + ":" + server.getPort() + "/");
            log.info("resolved service " + uri.toString());
        });

    }
}
