class ApplicationStatus extends React.Component {

    constructor(props) {
        super(props);

        this.render = () => {
            var applicationEnvironmentStatusCells = $.map(this.props.applicationStatus.environments, (value, environment) => (
                    <ApplicationEnvironmentStatus key={environment} applicationEnvironmentStatus={value} environmentName={environment} />
                )
            );

            return (
                <tr>
                    <th className="table-active" scope="row"><h4>{this.props.applicationName}</h4></th>
                    {applicationEnvironmentStatusCells}
                </tr>
            );
        };
    }
}