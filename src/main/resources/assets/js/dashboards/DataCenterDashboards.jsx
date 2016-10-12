class DataCenterDashboards extends React.Component {

    constructor(props) {
        super(props);

        this.render = () => {
            var dataCenterDashboards = $.map(this.props.data.dataCenters, (value, dataCenter) => {
                return (
                    <div key={dataCenter} className={"tab-pane" + (dataCenter===this.props.primaryDataCenter ? ' active' : '')} id={dataCenter.replace(/\s+/g, '-').toLowerCase()} role="tabpanel">
                        <DataCenterDashboard dataCenter={value} />
                    </div>
                );
            });

            return (
                <div className="tab-content">{dataCenterDashboards}</div>
            );
        };
    }
}
