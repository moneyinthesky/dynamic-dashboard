class DataCenterDashboard extends React.Component {

    constructor(props) {
        super(props);

        this.render = () => {
            var environmentHeaders = $.map(this.props.dataCenter.environments, (environment) => (
                <th key={environment}><h4>{environment}</h4></th>
            ));

            var applicationStatusRows = $.map(this.props.dataCenter.applications, (value, application) => (
                <ApplicationStatus key={application} applicationStatus={value} applicationName={application} />
            ));

            return (
                <table className="table table-bordered">
                    <thead>
                        <tr className="table-active">
                            <th></th>
                            {environmentHeaders}
                        </tr>
                    </thead>
                    <tbody>
                        {applicationStatusRows}
                    </tbody>
                </table>
            );
        };
    }
}