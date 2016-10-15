package io.moneyinthesky.dashboard.nodediscovery;

import java.util.List;
import java.util.Map;

public interface NodeDiscoveryMethod {
    List<String> generateNodeUrls(Map<String, String> configuration);
}
