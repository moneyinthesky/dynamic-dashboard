class ApplicationEnvironmentStatus extends React.Component {

    constructor(props) {
        super(props);

        this.render = () => {

            var optionalDownStatus = "";
            if(this.props.applicationEnvironmentStatus.nodesDown > 0) {
                optionalDownStatus = <li className="app-env-status status-down list-group-item"><button type="button" className="btn btn-danger">{this.props.applicationEnvironmentStatus.nodesDown} Down</button></li>;
            }

            var versionStatusList = $.map(this.props.applicationEnvironmentStatus.versionToNodeStatusMap, (status, version) => {
                var numberOfUnhealthyDependencies = Object.keys(status.unhealthyDependencies).length;
                var unhealthyDependencies = numberOfUnhealthyDependencies>0 ?
                        <button type="button" className="btn btn-danger">{numberOfUnhealthyDependencies + " DD"}</button> : "";
                return (
                	<li key={version} className="app-env-status status-up list-group-item">
                	    <div className="container">
                          <div className="col-md-4"><h5>{version}</h5></div>
                          <div className="col-md-4"><button key={version} type="button" className="btn btn-success">{status.nodeCount} Up</button></div>
                          <div className="col-md-4">{unhealthyDependencies}</div>
                        </div>
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