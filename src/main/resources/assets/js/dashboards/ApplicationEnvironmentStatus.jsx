class ApplicationEnvironmentStatus extends React.Component {

    constructor(props) {
        super(props);

        this.render = () => {

            var optionalDownStatus = "";
            if(this.props.applicationEnvironmentStatus.nodesDown > 0) {
                optionalDownStatus = <li className="app-env-status list-group-item"><button type="button" className="btn btn-danger">{this.props.applicationEnvironmentStatus.nodesDown} Down</button></li>;
            }

            var versionStatusList = $.map(this.props.applicationEnvironmentStatus.versionToNodeStatusMap, (status, version) => {
                var unhealthyDependencies = $.map(status.unhealthyDependencies, (dependency, dependencyName) => {
                    return <button key={dependencyName} type="button" className="btn btn-danger">{dependencyName}</button>
                });
                return (
                	<li key={version} className="app-env-status list-group-item">
                		v{version}<br/>
                    	<button key={version} type="button" className="btn btn-success">{status.nodeCount} Up</button>
                    	{unhealthyDependencies}
                   	</li>
                );
            });

            return (
                <td>
                	<ul className="list-group">
						{versionStatusList}
						{optionalDownStatus}
					</ul>
                </td>
            );
        };
    }
}