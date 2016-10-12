class DataCenterTabs extends React.Component {

    constructor(props) {
        super(props);

        this.render = () => {
            var dataCenterTabs = this.props.data.dataCenters.map((dataCenterObject, index) => {
                var dataCenter = dataCenterObject.name;
                return (
                    <li key={dataCenter} className="nav-item">
                        <a className={"nav-link" + (index==0 ? ' active' : '')} data-toggle="tab" href={'#' + dataCenter.replace(/\s+/g, '-').toLowerCase()} role="tab"><h5>{dataCenter}</h5></a>
                    </li>
                );
            });

            return (
                <ul className="nav nav-tabs">{dataCenterTabs}</ul>
            );
        };
    }
}
