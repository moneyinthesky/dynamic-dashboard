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

        this.state = {data: {}, settings: {}};

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
                error: function(xhr, status, err) {
                    console.error(this.props.settingsUrl, status, err.toString());
                }.bind(this)
            });
        };

        this.render = () => {
            return (
                <div>
                    <div id="title-bar" className="container-fluid">
                        <TitleBar title={this.state.settings.title} />
                    </div>
                    <div id="data-center-dashboard" className="container-fluid">
                        <div id="data-center-tabs"><DataCenterTabs data={this.state.data} primaryDataCenter={this.state.settings.primaryDataCenter} /></div>
                        <div id="data-center-tab-content"><DataCenterDashboards data={this.state.data} primaryDataCenter={this.state.settings.primaryDataCenter} /></div>
                    </div>
                    <div id="footer-bar"><FooterBar data={this.state.data} /></div>
                    <ModalSettings settings={this.state.settings} onSave={this.handleSaveSettings} />
                </div>
            );
        };
    }
}

ReactDOM.render(<Parent url="/api/data" settingsUrl="/api/data/settings" pollInterval={10000} />, document.getElementById('parent'));

$(document).ready(function(){
    $('[data-toggle="tooltip"]').tooltip();
});
