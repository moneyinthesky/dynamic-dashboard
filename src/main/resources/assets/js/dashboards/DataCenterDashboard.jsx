class DataCenterDashboard extends React.Component {

    constructor(props) {
        super(props);

        this.render = () => {
            var environmentHeaders = this.props.dataCenter.environments.map((environment) => (
                <th key={environment}><h4>{environment}</h4></th>
            ));
            var applicationStatusRows = this.props.dataCenter.applications.map((applicationObject, index) => (
                <ApplicationStatus key={index} applicationStatus={applicationObject} environmentList={this.props.dataCenter.environments} dataCenterName={this.props.dataCenter.name} />
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