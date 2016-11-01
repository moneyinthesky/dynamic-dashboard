class DataCenterDashboards extends React.Component {

    constructor(props) {
        super(props);

        this.render = () => {
            var dataCenterDashboards = this.props.data.dataCenters.map((dataCenterObject, index) => {
                return (
                    <div key={index} className={"tab-pane" + (index==0 ? ' active' : '')} id={dataCenterObject.name.replace(/\s+/g, '-').toLowerCase()} role="tabpanel">
                        <DataCenterDashboard dataCenter={dataCenterObject} />
                    </div>
                );
            });

            var dataCenterInitializationMessage = (this.props.data.dataCenters.length === 0) ? (
                <div className="setup-alert alert alert-info" role="alert">
                  Add <strong>data centers</strong> in settings
                </div>
            ) : "";

            return (
                <div className="tab-content">{dataCenterDashboards}{dataCenterInitializationMessage}</div>
            );
        };
    }
}
