class DataCenterDashboards extends React.Component {

    constructor(props) {
        super(props);

        this.render = () => {
            var dataCenterDashboards = this.props.data.dataCenters.map((dataCenterObject, index) => {
                var dataCenter = dataCenterObject.name;
                return (
                    <div key={dataCenter} className={"tab-pane" + (index==0 ? ' active' : '')} id={dataCenter.replace(/\s+/g, '-').toLowerCase()} role="tabpanel">
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
