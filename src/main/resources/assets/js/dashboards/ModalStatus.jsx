class ModalStatus extends React.Component {

    constructor(props) {
        super(props);

        this.render = () => {
            var dataCenters = this.props.data ? (this.props.data.dataCenters.map((dataCenterObject, index) => {
                var applications = dataCenterObject.applications.map((applicationObject, index) => {
                    var environments = $.map(applicationObject.environmentStatusMap, (environmentObject, environmentName) => {
                        var modalId = (dataCenterObject.name + applicationObject.name + environmentName).replace(/\s+/g, '-').toLowerCase();
                        var versions = $.map(environmentObject.versionToNodeStatusMap, (aggregatedNodeStatus, version) => {
                        	var nodes = aggregatedNodeStatus.nodesForVersion.map((nodeStatus, index) => {
                        		var downDependencies = nodeStatus.downDependencies.map((dependency, index) => {
                        			return (
                        				<td className="status-summary-table" key={index}>
                        					<div className="down-dependency">{dependency.name}</div>
                        				</td>
                        			);
                        		});
                        		var errorMessage = nodeStatus.errorMessage ? (
                                        <td className="status-summary-table">
                                            {nodeStatus.errorMessage}
                                        </td>
                        		) : "";
                        		return (
                        			<li key={index} className="list-group-item">
                        				<table>
                        					<tbody>
												<tr>
													<td className="status-summary-table">
														<a className="status-summary-link" href={nodeStatus.infoUrl} target="_blank">{nodeStatus.identifier}</a>
													</td>
													{downDependencies}
													{errorMessage}
												</tr>
                        					</tbody>
                        				</table>
									</li>
                        		);
                        	});
                        	return (
								<li key={version} className="list-group-item list-group-item-success">
									Version {version}
									<div>
										<ul className="list-group">
											{nodes}
										</ul>
									</div>
								</li>
                        	);
                        });
                        var unknownVersionNodes = environmentObject.unknownVersionNodes.map((nodeStatus, index) => {
                            return (
                                <li key={index} className="list-group-item">
                                    <table>
                                        <tbody>
                                            <tr>
                                                <td className="status-summary-table">
                                                    <a className="status-summary-link" href={nodeStatus.infoUrl} target="_blank">{nodeStatus.identifier}</a>
                                                </td>
                                                <td className="status-summary-table">
                                                    {nodeStatus.errorMessage}
                                                </td>
                                            </tr>
                                        </tbody>
                                    </table>
                                </li>
                            );
                        });
                        var unknownVersionNodesWrapper = environmentObject.unknownVersionNodes.length>0 ? (
                            <li className="list-group-item list-group-item-success">
                                Unknown Version
                                <div>
                                    <ul className="list-group">
                                        {unknownVersionNodes}
                                    </ul>
                                </div>
                            </li>
                        ) : "";
                        var unhealthyNodes = environmentObject.unhealthyNodes.map((nodeStatus, index) => {
                        	return (
                        		<li key={index} className="list-group-item">
                                    <table>
                                        <tbody>
                                            <tr>
                                                <td className="status-summary-table">
                                                    <a className="status-summary-link" href={nodeStatus.infoUrl} target="_blank">{nodeStatus.identifier}</a>
                                                </td>
                                                <td className="status-summary-table">
                                                    {nodeStatus.errorMessage}
                                                </td>
                                            </tr>
                                        </tbody>
                                    </table>
								</li>
                        	);
                        });
                        var unhealthyNodesWrapper = environmentObject.unhealthyNodes.length>0 ? (
                        	<li className="list-group-item list-group-item-danger">
								Unhealthy Nodes
								<div>
									<ul className="list-group">
										{unhealthyNodes}
									</ul>
								</div>
							</li>
                        ) : "";
                        return (
                            <div key={environmentName} className="modal fade" id={modalId} tabIndex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
                                <div className="modal-dialog status-summary" role="document">
                                    <div className="modal-status-content modal-content">
                                      <div className="modal-header">
                                        <button type="button" className="close" data-dismiss="modal" aria-label="Close">
                                          <span aria-hidden="true">&times;</span>
                                        </button>
                                        <h4 className="modal-title" id="myModalLabel">Status Summary</h4>
                                      </div>
                                      <div className="modal-body">
										<ul className="list-group">
											{versions}
											{unknownVersionNodesWrapper}
											{unhealthyNodesWrapper}
										</ul>
                                      </div>
                                      <div className="modal-footer">
                                        <button type="button" className="btn btn-secondary" data-dismiss="modal">Close</button>
                                      </div>
                                    </div>
                                </div>
                            </div>
                        );
                    });
                    return <div key={index}>{environments}</div>;
                });
                return <div key={index}>{applications}</div>;
            })) : "";
            return (
                <div>{dataCenters}</div>
            );
        };
    }
}
