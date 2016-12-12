# Dynamic Dashboard

A generic, flexible and extensible dashboard for monitoring clustered applications across environments and data centers.

![screenshot](/screenshot.jpg?raw=true)

## Building & Running
### Pre-requisites
Java 8 and Maven.
### Building
`mvn clean package`
### Running
`java -jar target/dynamic-dashboard-X.X.jar server configuration.yml`
## Adding bespoke functionality
### Node Discovery
The application comes with 3 methods of node discovery out of the box:
1. URL Pattern Matching
2. AWS (via Route53 and EC2)
3. Fleet (via the Fleet REST API)

In order to add a bespoke node discovery method:
1. Implement the following interface: `io.moneyinthesky.dashboard.nodediscovery.NodeDiscoveryMethod`
2. Modify `io.moneyinthesky.dashboard.core.dao.DashboardDataDao.discoveryMethodMap` to reference the new method of node discovery
3. Modify ModalSettings.jsx (which generates the Settings UI).

### Node Status Population
The application comes with a default method of populating node status information:
`io.moneyinthesky.dashboard.statuspopulation.DefaultNodeStatusPopulation`

In order to add a bespoke node status population mechanism, implement the following interface:
`io.moneyinthesky.dashboard.statuspopulation.NodeStatusPopulation`
