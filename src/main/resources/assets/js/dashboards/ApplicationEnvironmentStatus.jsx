class ApplicationEnvironmentStatus extends React.Component {

    constructor(props) {
        super(props);

        this.render = () => {

            var optionalDownStatus = "";
            if(this.props.applicationEnvironmentStatus.nodesDown > 0) {
                optionalDownStatus = <li className="app-env-status status-down list-group-item">{this.props.applicationEnvironmentStatus.nodesDown} Down</li>;
            }

            var versionStatusList = $.map(this.props.applicationEnvironmentStatus.versionToNodeStatusMap, (status, version) => {
                var numberOfUnhealthyDependencies = Object.keys(status.unhealthyDependencies).length;
                var unhealthyDependencies = numberOfUnhealthyDependencies>0 ? <div className="app-env-status-deps">{numberOfUnhealthyDependencies + " Deps Down"}</div> : "";
                return (
                	<li key={version} className="app-env-status status-up list-group-item">
                	    <div className="container app-env-status-container">
                          v{version} - {status.nodeCount} Up
                          {unhealthyDependencies}
                        </div>
                   	</li>
                );
            });

            var modalId = (this.props.dataCenterName + this.props.applicationName + this.props.applicationEnvironmentStatus.name).replace(/\s+/g, '-').toLowerCase();
            return (
                <td data-toggle="modal" data-target={"#" + modalId}>
                	<ul className="list-group">
						{versionStatusList}
						{optionalDownStatus}
					</ul>
                </td>
            );
        };
    }
}