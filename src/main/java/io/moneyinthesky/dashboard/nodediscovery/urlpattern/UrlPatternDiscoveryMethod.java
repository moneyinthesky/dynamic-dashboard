package io.moneyinthesky.dashboard.nodediscovery.urlpattern;

import io.moneyinthesky.dashboard.nodediscovery.NodeDiscoveryMethod;

import java.util.List;
import java.util.Map;

import static io.moneyinthesky.dashboard.patterns.ExplodableString.explode;

public class UrlPatternDiscoveryMethod implements NodeDiscoveryMethod {

    @Override
    public List<String> generateNodeUrls(Map<String, String> configuration) {
        String urlPattern = configuration.get("urlPattern");
        return explode(urlPattern);
    }
}
