package io.moneyinthesky.dashboard.nodediscovery;

import java.util.List;
import java.util.Map;

@FunctionalInterface
public interface NodeDiscoveryMethod {
    List<String> generateNodeUrls(Map<String, String> configuration);
}
