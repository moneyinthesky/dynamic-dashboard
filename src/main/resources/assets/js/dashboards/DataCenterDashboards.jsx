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

            return (
                <div className="tab-content">{dataCenterDashboards}</div>
            );
        };
    }
}
