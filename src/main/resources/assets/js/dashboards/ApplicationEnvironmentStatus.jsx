class ApplicationEnvironmentStatus extends React.Component {

    constructor(props) {
        super(props);

        this.render = () => {

            var optionalDownStatus = "";
            if(this.props.applicationEnvironmentStatus.nodesDown > 0) {
                optionalDownStatus = <button type="button" className="btn btn-danger">{this.props.applicationEnvironmentStatus.nodesDown} Down</button>;
            }

            var versionStatusList = $.map(this.props.applicationEnvironmentStatus.versionToNodeStatusMap, (status, version) => {
                return (
                    <button type="button" className="btn btn-success">v{version}: {status.nodeCount} Up</button>
                );
            });

            return (
                <td>
					{versionStatusList}
					{optionalDownStatus}
                </td>
            );
        };
    }
}