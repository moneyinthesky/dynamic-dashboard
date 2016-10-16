class ApplicationStatus extends React.Component {

    constructor(props) {
        super(props);

        this.render = () => {
            var applicationEnvironmentStatusCells = this.props.environmentList.map((environment, index) => (
            	<ApplicationEnvironmentStatus key={index} applicationEnvironmentStatus={this.props.applicationStatus.environmentStatusMap[environment]} />
            ));

            return (
                <tr>
                    <th className="table-active" scope="row"><h4>{this.props.applicationStatus.name}</h4></th>
                    {applicationEnvironmentStatusCells}
                </tr>
            );
        };
    }
}