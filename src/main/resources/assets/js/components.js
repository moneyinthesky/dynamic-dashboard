var staticData = {
  title: "Dynamic Dashboard"
}

var TitleBar = React.createClass({
  render: function() {
    return (
      <h1>{this.props.title}
        <div className="pull-xs-right">
          <button id="settings-button" type="button" className="btn btn-secondary btn-md active">Settings</button>
        </div>
      </h1>
    );
  }
});

var FooterBar = React.createClass({
  getInitialState: function() {
    return {data: []};
  },
  loadData: function() {
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
  },
  componentDidMount: function() {
    this.loadData();
    setInterval(this.loadData, this.props.pollInterval);
  },
  render: function() {
    return (
      <footer className="footer">
          <div className="pull-xs-right container">
              <p className="text-muted">{"Time generated: " + this.state.data.timeGenerated}</p>
          </div>
      </footer>
    );
  }
});

var DataCenterTabs = React.createClass({
  getInitialState: function() {
    return {data: []};
  },
  loadData: function() {
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
  },
  componentDidMount: function() {
    this.loadData();
    setInterval(this.loadData, this.props.pollInterval);
  },
  render: function() {
    var dataCenterTabs = $.map(this.state.data.dataCenters, function(value, dataCenter) {
      return (
        <li key={dataCenter} className="nav-item">
          <a className={"nav-link" + (value.primary==true ? ' active' : '')} data-toggle="tab" href={'#' + dataCenter.replace(/\s+/g, '-').toLowerCase()} role="tab"><h5>{dataCenter}</h5></a>
        </li>
      );
    });
    return (
      <ul className="nav nav-tabs">{dataCenterTabs}</ul>
    );
  }
});

var DataCenterDashboards = React.createClass({
  getInitialState: function() {
    return {data: []};
  },
  loadData: function() {
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
  },
  componentDidMount: function() {
    this.loadData();
    setInterval(this.loadData, this.props.pollInterval);
  },
  render: function() {
    var dataCenterDashboards = $.map(this.state.data.dataCenters, function(value, dataCenter) {
      return (
        <div key={dataCenter} className={"tab-pane" + (value.primary==true ? ' active' : '')} id={dataCenter.replace(/\s+/g, '-').toLowerCase()} role="tabpanel">
          <DataCenterDashboard dataCenter={value} />
        </div>
      );
    });
    return (
      <div className="tab-content">{dataCenterDashboards}</div>
    );
  }
});

var DataCenterDashboard = React.createClass({
  render: function() {
    var environmentHeaders = $.map(this.props.dataCenter.environments, function(environment) {
      return (
        <th key={environment}><h4>{environment}</h4></th>
      );
    });
    var applicationStatusRows = $.map(this.props.dataCenter.applications, function(value, application) {
      return (
        <ApplicationStatus key={application} applicationStatus={value} applicationName={application} />
      );
    });
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
  }
});

var ApplicationStatus = React.createClass({
  render: function() {
    var applicationEnvironmentStatusCells = $.map(this.props.applicationStatus.environments, function(value, environment) {
      return (
        <ApplicationEnvironmentStatus key={environment} applicationEnvironmentStatus={value} environmentName={environment} />
      );
    });
    return (
      <tr>
        <th className="table-active" scope="row"><h4>{this.props.applicationName}</h4></th>
        {applicationEnvironmentStatusCells}
      </tr>
    );
  }
});

var ApplicationEnvironmentStatus = React.createClass({
  render: function() {
    var optionalDownStatus = "";
    if(this.props.applicationEnvironmentStatus.nodesDown > 0) {
      optionalDownStatus = <button type="button" className="btn btn-danger">{this.props.applicationEnvironmentStatus.nodesDown} Down</button>;
    }
    return (
      <td>
        <button type="button" className="btn btn-success">{this.props.applicationEnvironmentStatus.nodesUp} Up</button>
        {optionalDownStatus}
      </td>
    );
  }
});


ReactDOM.render(<TitleBar title={staticData.title} />, document.getElementById('title-bar'));
ReactDOM.render(<DataCenterTabs url="/api/data" pollInterval={2000} />, document.getElementById('data-center-tabs'));
ReactDOM.render(<DataCenterDashboards url="/api/data" pollInterval={2000} />, document.getElementById('data-center-tab-content'));
ReactDOM.render(<FooterBar url="/api/data" pollInterval={2000} />, document.getElementById('footer-bar'));
