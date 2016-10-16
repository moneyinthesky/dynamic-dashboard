package io.moneyinthesky.dashboard.nodediscovery;

import java.util.List;
import java.util.Map;

import static io.moneyinthesky.dashboard.patterns.ExplodableString.explode;

public class UrlPatternMethod implements NodeDiscoveryMethod {

    @Override
    public List<String> generateNodeUrls(Map<String, String> configuration) {
        String urlPattern = configuration.get("urlPattern");
        return explode(urlPattern);
    }
}
