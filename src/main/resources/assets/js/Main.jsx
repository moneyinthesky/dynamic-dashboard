var placeholder = document.createElement("li");
placeholder.className = "placeholder";

function getByName(object, name) {
    return $.grep(object, function(e) { return e.name === name })[0];
}

function removeByName(object, name) {
    return object.filter(function(e) {
        return e.name !== name;
    });
}

class Parent extends React.Component {

    constructor(props) {

        super(props);

        this.state = {
            data: {
                dataCenters: []
            },
            settings: {},
            rotating: false
        };

        this.loadData = () => {
            $.ajax({
                url: this.props.url,
                dataType: 'json',
                cache: false,
                success: function(data) {
                    this.setState({data: data});
                }.bind(this),
                error: function(xhr, status, err) {
                    console.error(this.props.url, status, err.toString());
                }.bind(this)
            });
        };

        this.forceLoadData = () => {
            $.ajax({
                url: this.props.forceUrl,
                dataType: 'json',
                cache: false,
                success: function(data) {
                    this.setState({data: data});
                }.bind(this),
                error: function(xhr, status, err) {
                    console.error(this.props.forceUrl, status, err.toString());
                }.bind(this)
            });
        };

        this.loadSettings = () => {
            $.ajax({
                url: this.props.settingsUrl,
                dataType: 'json',
                cache: false,
                success: function(data) {
                    this.setState({settings: data});
                }.bind(this),
                error: function(xhr, status, err) {
                    console.error(this.props.settingsUrl, status, err.toString());
                }.bind(this)
            });
        };

        this.componentDidMount = () => {
            this.loadSettings();
            this.loadData();
            setInterval(this.loadData, this.props.pollInterval);
        };

        this.handleSaveSettings = (newSettings) => {
            this.setState({ settings: newSettings });

            $.ajax({
                url: this.props.settingsUrl,
                contentType: "application/json",
                dataType: 'json',
                type: 'POST',
                data: JSON.stringify(newSettings),
                success: function() {
                    this.forceLoadData();
                }.bind(this),
                error: function(xhr, status, err) {
                    console.error(this.props.settingsUrl, status, err.toString());
                }.bind(this)
            });
        };

        this.handleRotateClick = (rotating) => {
            this.setState({rotating: rotating});
        };

        this.render = () => {
        	var dataCenterDashboard = (this.state.data) ? (
        		<div id="data-center-dashboard" className="container-fluid">
					<div id="data-center-tabs"><DataCenterTabs data={this.state.data} rotating={this.state.rotating} /></div>
					<div id="data-center-tab-content"><DataCenterDashboards data={this.state.data} /></div>
				</div>
        	) : (
        		<div className="loading-gif text-xs-center">
                  <img src="images/gears.gif" />
                </div>
        	);
            return (
                <div>
                    <div id="title-bar" className="container-fluid">
                        <TitleBar title={this.state.settings.title} onRotateClick={this.handleRotateClick}/>
                    </div>
					{dataCenterDashboard}
                    <div id="footer-bar"><FooterBar data={this.state.data} /></div>
                    <ModalSettings settings={this.state.settings} onSave={this.handleSaveSettings} />
                    <ModalStatus data={this.state.data} />
                </div>
            );
        };
    }
}

ReactDOM.render(<Parent url="/api/data" forceUrl="/api/data/force" settingsUrl="/api/settings" pollInterval={10000} />, document.getElementById('parent'));

$(document).ready(function(){
    $('[data-toggle="tooltip"]').tooltip();
});
