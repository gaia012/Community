package kr.co.gaia012.route;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DiscoveryClientCLR implements CommandLineRunner {

    private final DiscoveryClient discoveryClient;

    @Autowired
    public DiscoveryClientCLR(DiscoveryClient discoveryClient){
        this.discoveryClient = discoveryClient;
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("localServiceInstance");
//        this.logServiceInstance(this.discoveryClient.get);

        String serviceId = "community-cloud-config-server";
        log.info(String.format("registered instances of '%s'", serviceId));
        this.discoveryClient.getInstances(serviceId).forEach(this::logServiceInstance);
    }

    private void logServiceInstance(ServiceInstance si){
        String msg = String.format("host = %s, port = %s, service ID = %s", si.getHost(), si.getPort(), si.getServiceId());
        log.info(msg);
    }
}
