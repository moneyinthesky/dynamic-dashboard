class DataCenterTabs extends React.Component {

    constructor(props) {
        super(props);

        this.state = {
            selectedTab: 0,
            rotationInitialized: false
        };

        this.rotateDataCenterTabs = () => {
            if(this.props.data.dataCenters.length > 1 && this.props.rotating) {
                var dataCenterTabId = "tab-" + this.props.data.dataCenters[this.state.selectedTab].name.replace(/\s+/g, '-').toLowerCase();
                document.getElementById(dataCenterTabId).click();
                if(this.state.selectedTab >= this.props.data.dataCenters.length-1) {
                    this.state.selectedTab = 0;
                } else {
                    this.state.selectedTab++;
                }
                this.setState({selectedTab: this.state.selectedTab});
            }
        };

        this.startRotatingDataCenterTabs = () => {
            setInterval(this.rotateDataCenterTabs, 10000);
        };

        this.componentWillReceiveProps = (nextProps) => {
            if(!this.state.rotationInitialized && this.props.data.dataCenters.length>0) {
                this.startRotatingDataCenterTabs();
                this.setState({rotationInitialized: true});
            }
        };

        this.render = () => {
            var dataCenterTabs = this.props.data.dataCenters.map((dataCenterObject, index) => {
                var dataCenter = dataCenterObject.name;
                return (
                    <li key={dataCenter} className="nav-item">
                        <a id={"tab-" + dataCenter.replace(/\s+/g, '-').toLowerCase()} className={"nav-link" + (index==0 ? ' active' : '')} data-toggle="tab" href={'#' + dataCenter.replace(/\s+/g, '-').toLowerCase()} role="tab"><h5>{dataCenter}</h5></a>
                    </li>
                );
            });

            return (
                <ul className="nav nav-tabs">{dataCenterTabs}</ul>
            );
        };
    }
}
