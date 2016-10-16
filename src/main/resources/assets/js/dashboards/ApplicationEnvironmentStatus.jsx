class ApplicationEnvironmentStatus extends React.Component {

    constructor(props) {
        super(props);

        this.render = () => {
			var nodeVersionList = this.props.applicationEnvironmentStatus.nodeStatusList.map((status, index) => (
				<div key={index}>{status.version}</div>
			));

            return (
                <td>
					{nodeVersionList}
                </td>
            );
        };
    }
}