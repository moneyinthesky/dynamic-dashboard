class DataCenterTabs extends React.Component {

    constructor(props) {
        super(props);

        this.render = () => {
            var dataCenterTabs = $.map(this.props.data.dataCenters, (value, dataCenter) => {
                return (
                    <li key={dataCenter} className="nav-item">
                    <a className={"nav-link" + (dataCenter===this.props.primaryDataCenter ? ' active' : '')} data-toggle="tab" href={'#' + dataCenter.replace(/\s+/g, '-').toLowerCase()} role="tab"><h5>{dataCenter}</h5></a>
                    </li>
                );
            });

            return (
                <ul className="nav nav-tabs">{dataCenterTabs}</ul>
            );
        };
    }
}
