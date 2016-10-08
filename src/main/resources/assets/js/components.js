var ModalSettings = React.createClass ({
  getInitialState: function() {
    return {
        title: this.props.settings.title,
        applications: [],
        applicationToAdd: "",
        showEmptyApplicationWarning: false,
        activeTab: "generalSettings"
        };
  },
  componentWillReceiveProps: function(nextProps) {
    if(nextProps.settings.title != this.props.settings.title) {
        this.setState({title: nextProps.settings.title});
    }

    var array1 = this.props.settings.applications;
    var array2 = nextProps.settings.applications;
    var applicationsEqual = (array1) && (array2) && (array1.length == array2.length) && array1.every(function(element, index) {
        return element === array2[index];
    });

    if(array2 && !applicationsEqual) {
        this.setState({ applications: array2});
    }
  },
  handleTitleChange(event) {
    this.setState({title: event.target.value});
  },
  handleApplicationToAddChange(event) {
    this.setState({applicationToAdd: event.target.value, showEmptyApplicationWarning: false});
  },
  addApplication(event) {
    if(this.state.applicationToAdd) {
      this.state.applications.push(this.state.applicationToAdd);
      this.setState({ applications: this.state.applications, applicationToAdd: '' });
    } else {
      this.setState({showEmptyApplicationWarning : true})
    }
  },
  removeApplication(event) {
    this.state.applications.splice(event.target.dataset.index, 1);
    this.setState({applications: this.state.applications});
  },
  changeSettingsNav: function(event) {
    this.setState({activeTab: event.target.dataset.tab});
  },
  handleSave: function() {
    this.props.onSave({ title : this.state.title,
                        applications : this.state.applications
                      });
    this.setState({activeTab: "generalSettings"});
  },
  handleClose: function() {
    this.setState({activeTab: "generalSettings"});
  },
  render: function() {
    var emptyApplicationWarning = this.state.showEmptyApplicationWarning ? <div className="form-control-feedback">Application name is required</div> : "";
    var applicationRows = this.state.applications.map(function(application, index) {
          return (
            <div key={application} className="input-group">
              <input key={application} value={application} readOnly className="form-control" type="text" />
              <span className="input-group-btn">
                <button key={application} type="button" data-index={index} className="btn btn-danger mega-octicon octicon-dash" onClick={this.removeApplication}></button>
              </span>
            </div>
          );
        }.bind(this));
    return (
        <div className="modal fade" id="settings-modal" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
          <div className="modal-dialog modal-lg" role="document">
            <div className="modal-content">
              <form>
              <div className="modal-header">
                <nav className="navbar navbar-dark bg-inverse">
                  <a className="navbar-brand" href="#">Settings</a>
                  <ul className="nav navbar-nav">
                    <li className={"nav-item" + (this.state.activeTab === "generalSettings" ? " active" : "")}>
                      <a className="nav-link" href="#generalSettings" data-tab="generalSettings" onClick={this.changeSettingsNav}>General</a>
                    </li>
                    <li className={"nav-item" + (this.state.activeTab === "applicationSettings" ? " active" : "")}>
                      <a className="nav-link" href="#applicationSettings" data-tab="applicationSettings" onClick={this.changeSettingsNav}>Applications</a>
                    </li>
                  </ul>
                </nav>
              </div>
              <div className="modal-body">
                <div style={(this.state.activeTab == "generalSettings" ? {display: 'inline'} : {display: 'none'})}>
                    <div className="form-group row">
                      <label htmlFor="example-text-input" className="col-xs-4 col-form-label">Dashboard Title</label>
                      <div className="col-xs-8">
                        <input value={this.state.title} className="form-control" type="text" onChange={this.handleTitleChange} />
                      </div>
                    </div>
                </div>
                <div style={(this.state.activeTab == "applicationSettings" ? {display: 'inline'} : {display: 'none'})}>
                    <fieldset className="form-group row">
                        <legend className="col-form-legend col-xs-4">Applications</legend>
                        <div className="col-xs-8">
                          {applicationRows}
                          <div className={"input-group" + (this.state.showEmptyApplicationWarning ? " has-warning" : "")}>
                            <input value={this.state.applicationToAdd} className={"form-control" + (this.state.showEmptyApplicationWarning ? " form-control-warning" : "")} type="text" onChange={this.handleApplicationToAddChange} />
                            <span className="input-group-btn">
                              <button type="button" className="btn btn-success mega-octicon octicon-plus" onClick={this.addApplication}></button>
                            </span>
                          </div>
                          {emptyApplicationWarning}
                        </div>
                    </fieldset>
                </div>
              </div>
              <div className="modal-footer">
                <button type="button" className="btn btn-secondary" data-dismiss="modal" onClick={this.handleClose}>Close</button>
                <button type="button" className="btn btn-primary" data-dismiss="modal" onClick={this.handleSave}>Save</button>
              </div>
              </form>
            </div>
          </div>
        </div>
    );
  }
});

var TitleBar = React.createClass({
  render: function() {
    return (
      <h1>{this.props.title}
        <div className="pull-xs-right">
          <button id="settings-button" type="button" className="btn btn-secondary btn-md active" data-toggle="modal" data-target="#settings-modal">
            <img src="images/gear.png" width="35" />
          </button>
        </div>
      </h1>
    );
  }
});

var FooterBar = React.createClass({
  render: function() {
    return (
      <footer className="footer">
          <div className="pull-xs-right container">
              <p className="text-muted">{"Time generated: " + this.props.data.timeGenerated}</p>
          </div>
      </footer>
    );
  }
});

var DataCenterTabs = React.createClass({
  render: function() {
    var dataCenterTabs = $.map(this.props.data.dataCenters, function(value, dataCenter) {
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
  render: function() {
    var dataCenterDashboards = $.map(this.props.data.dataCenters, function(value, dataCenter) {
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

var Parent = React.createClass({
  getInitialState: function() {
    return {data: [], settings: {}};
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
  loadSettings: function() {
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
  },
  componentDidMount: function() {
    this.loadSettings();
    this.loadData();
    setInterval(this.loadData, this.props.pollInterval);
  },
  handleSaveSettings: function(newSettings) {
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
  },
  render: function() {
    return (
        <div>
            <div id="title-bar" className="container-fluid">
                <TitleBar title={this.state.settings.title} />
            </div>
            <div id="data-center-dashboard" className="container-fluid">
                <div id="data-center-tabs"><DataCenterTabs data={this.state.data} /></div>
                <div id="data-center-tab-content"><DataCenterDashboards data={this.state.data} /></div>
            </div>
            <div id="footer-bar"><FooterBar data={this.state.data} /></div>
            <ModalSettings settings={this.state.settings} onSave={this.handleSaveSettings} />
        </div>
    );
  }
});

ReactDOM.render(<Parent url="/api/data" settingsUrl="/api/data/settings" pollInterval={10000} />, document.getElementById('parent'));
