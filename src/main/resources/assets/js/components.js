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

var ModalSettings = React.createClass ({
  getInitialState: function() {
    return {
        settings : {
            title: "",
            applications: [],
            dataCenters: [],
            primaryDataCenter: ""
        },
        titleToAdd: "",
        applicationToAdd: "",
        dataCenterToAdd: "",
        environmentToAdd: {},
        applicationWarning: "",
        dataCenterWarning: "",
        activeTab: "generalSettings",
        importAlert: "",
        importFile: ""
    };
  },
  componentWillReceiveProps: function(nextProps) {
    if(!(JSON.stringify(this.props.settings) === JSON.stringify(nextProps.settings))) {
        this.setState({settings : nextProps.settings, titleToAdd : nextProps.settings.title});
    }
  },
  handleTitleChange(event) {
    this.setState({titleToAdd : event.target.value});
  },
  handleApplicationToAddChange(event) {
    this.setState({applicationToAdd: event.target.value, applicationWarning: ''});
  },
  handleDataCenterToAddChange(event) {
    this.setState({dataCenterToAdd: event.target.value, dataCenterWarning: ''});
  },
  handleDataCenterEnvironmentChange(event) {
	var dataCenter = event.target.dataset.datacenter;
	this.state.environmentToAdd[dataCenter] = event.target.value;
	this.setState({environmentToAdd : this.state.environmentToAdd});
  },
  addApplication(event) {
    if(this.state.applicationToAdd && !this.state.settings.applications.includes(this.state.applicationToAdd)) {
      this.state.settings.applications.push(this.state.applicationToAdd);
      this.setState({ settings : this.state.settings, applicationToAdd: '' });
    } else if(this.state.settings.applications.includes(this.state.applicationToAdd)) {
      this.setState({applicationWarning: 'Application already added'})
    } else {
      this.setState({applicationWarning: 'Application name is required'});
    }
  },
  addDataCenter(event) {
    if(!this.state.dataCenterToAdd) {
        this.setState({dataCenterWarning : 'Data center name is required'});
        return;
    }

    var existingDataCenter = getByName(this.state.settings.dataCenters, this.state.dataCenterToAdd);
    if(existingDataCenter) {
        this.setState({dataCenterWarning : 'Data center already added'});
        return;
    }

    this.state.settings.dataCenters.push({name: this.state.dataCenterToAdd, environments:[]});
    this.setState({ settings : this.state.settings, dataCenters : this.state.dataCenters, dataCenterToAdd: '' });
  },
  addDataCenterEnvironment(event) {
  	var dataCenter = event.target.dataset.datacenter;
    if(this.state.environmentToAdd[dataCenter]) {
        var dataCenterObject = getByName(this.state.settings.dataCenters, dataCenter);
		dataCenterObject.environments.push({name: this.state.environmentToAdd[dataCenter]});
		this.state.environmentToAdd[dataCenter] = "";
		this.setState({ settings : this.state.settings, environmentToAdd : this.state.environmentToAdd});
    }
  },
  handlePrimaryDataCenterSelect(event) {
    this.setState({primaryDataCenter : event.target.dataset.datacenter});
  },
  removeApplication(event) {
    this.state.settings.applications.splice(event.target.dataset.index, 1);
    this.setState({settings : this.state.settings});
  },
  removeDataCenter(event) {
    delete this.state.environmentToAdd[event.target.dataset.datacenter];
    this.state.settings.dataCenters = removeByName(this.state.settings.dataCenters, event.target.dataset.datacenter);
    this.setState({settings : this.state.settings, environmentToAdd : this.state.environmentToAdd});
  },
  removeDataCenterEnvironment(event) {
	var environmentToRemove = event.target.dataset.datacenterEnvironment.split("/");
    var dataCenterObject = getByName(this.state.settings.dataCenters, environmentToRemove[0]);
    dataCenterObject.environments = removeByName(dataCenterObject.environments, environmentToRemove[1]);
	this.setState({settings : this.state.settings});
  },
  changeSettingsNav: function(event) {
    this.setState({activeTab: event.target.dataset.tab});
  },
    dragStart: function(e) {
      this.dragged = e.currentTarget;
      e.dataTransfer.effectAllowed = 'move';
      e.dataTransfer.setData("text/html", e.currentTarget);
    },
    dragEnd: function(e) {
        this.dragged.style.display = "block";
        this.dragged.parentNode.removeChild(placeholder);

        var data = this.state.settings.applications;
        var from = Number(this.dragged.dataset.id);
        var to = Number(this.over.dataset.id);
        if(from < to) to--;
        data.splice(to, 0, data.splice(from, 1)[0]);
        this.setState({settings: this.state.settings});
    },
    dragOver: function(e) {
        e.preventDefault();
        this.dragged.style.display = "none";
        if(e.target.className == "placeholder") return;
        this.over = e.target;
        e.target.parentNode.insertBefore(placeholder, e.target);
    },
  handleSave: function() {
    this.state.settings.title = this.state.titleToAdd;
    this.props.onSave(this.state.settings);
    this.setState({activeTab: "generalSettings", importAlert: ""});
  },
  handleClose: function() {
    this.setState({activeTab: "generalSettings"});
  },
  importSettings: function(event) {
	var file = event.target.files[0];
	var reader = new FileReader();

	reader.onload = function(e) {
	  var copyOfState = JSON.parse(reader.result);
	  copyOfState.importAlert = "Import successful, Save when ready";
	  copyOfState.importFile = "";
	  this.setState(copyOfState);
    }.bind(this);

    reader.readAsText(file);
  },
  render: function() {
    var applicationWarning = this.state.applicationWarning ? <div className="form-control-feedback alert alert-warning" role="alert">{this.state.applicationWarning}</div> : "";
    var dataCenterWarning = this.state.dataCenterWarning ? <div className="form-control-feedback alert alert-warning" role="alert">{this.state.dataCenterWarning}</div> : "";
    var applicationRows = this.state.settings.applications.map(function(application, index) {
	  return (
		<li className="list-group-item clearfix" data-id={index} key={index} draggable="true" onDragEnd={this.dragEnd} onDragStart={this.dragStart}>
		    <button type="button" data-id={index} data-index={index} className="btn btn-danger mega-octicon octicon-dash pull-xs-right" onClick={this.removeApplication}></button>
            {application}
        </li>
	  );
	}.bind(this));
    var dataCenterRows = this.state.settings.dataCenters.map(function(dataCenterObject) {
      var dataCenter = dataCenterObject.name;
	  return (
		<div key={dataCenter} className="input-group">
		  <input key={dataCenter} value={dataCenter} readOnly className="form-control" type="text" />
		  <span className="input-group-btn">
			<button key={dataCenter} type="button" data-datacenter={dataCenter} className="btn btn-danger mega-octicon octicon-dash" onClick={this.removeDataCenter}></button>
		  </span>
		</div>
	  );
	}.bind(this));
    var primaryDataCenterSelect = this.state.settings.dataCenters.map(function(dataCenterObject) {
        var dataCenter = dataCenterObject.name;
    	return (
			<label data-datacenter={dataCenter} key={dataCenter} className={"btn btn-primary" + (this.state.settings.primaryDataCenter === dataCenter ? " active" : "")} onClick={this.handlePrimaryDataCenterSelect} >
			  <input key={dataCenter} type="radio" name="primaryDataCenter" value={dataCenter} id={dataCenter} autoComplete="off"/> {dataCenter}
			</label>
    	);
    }.bind(this));
    var dataCenterEnvironmentRows = this.state.settings.dataCenters.map(function(dataCenterObject, index) {
        var dataCenter = dataCenterObject.name;
    	var dataCenterEnvironmentRowsAlreadyAdded = dataCenterObject.environments.map(function(environmentObject, index) {
    	  var environment = environmentObject.name;
          return (
			<div key={dataCenter + "-" + environment} className="input-group">
			  <input key={dataCenter + "-" + environment} value={environment} readOnly className="form-control" type="text" />
			  <span className="input-group-btn">
				<button key={dataCenter + "-" + environment} type="button" data-datacenter-environment={dataCenter + "/" + environment} className="btn btn-danger mega-octicon octicon-dash" onClick={this.removeDataCenterEnvironment}></button>
			  </span>
			</div>
          );
        }.bind(this));
    	return (
			<div key={dataCenter} className="panel panel-default">
			  <div className="panel-heading" role="tab" id={dataCenter}>
				  <a className={dataCenter===this.state.settings.primaryDataCenter ? "" : "collapsed"} data-toggle="collapse" data-parent="#accordion" href={"#" + dataCenter.replace(/\s+/g, '-').toLowerCase() + "-enviornments"} aria-expanded="true" aria-controls={dataCenter.replace(/\s+/g, '-').toLowerCase() + "-environments"}>
				  	<div key={dataCenter} className="input-group">
						<input key={dataCenter} value={dataCenter} readOnly className="form-control" type="text" />
						<span className="input-group-btn">
							<button key={dataCenter} type="button" className="btn btn-info mega-octicon octicon-triangle-down"></button>
						</span>
					</div>
				  </a>
			  </div>
			  <div id={dataCenter.replace(/\s+/g, '-').toLowerCase() + "-enviornments"} className={"panel-collapse collapse" + (dataCenter===this.state.settings.primaryDataCenter ? " in" : "")} role="tabpanel" aria-labelledby={dataCenter}>
				<div className={"input-group col-xs-6 environment-datacenter-row"}>
				  {dataCenterEnvironmentRowsAlreadyAdded}
				  <div key={dataCenter} className="input-group">
					  <input value={this.state.environmentToAdd[dataCenter]} data-datacenter={dataCenter} className="form-control" type="text" onChange={this.handleDataCenterEnvironmentChange} placeholder="Add an environment" />
					  <span className="input-group-btn">
						<button type="button" className="btn btn-success mega-octicon octicon-plus" data-datacenter={dataCenter} onClick={this.addDataCenterEnvironment}></button>
					  </span>
				  </div>
				</div>
			  </div>
			</div>
    	);
    }.bind(this));
    var nodeDiscoveryTabs = this.state.settings.dataCenters.map(function(dataCenterObject) {
        var dataCenter = dataCenterObject.name;
    	return (
	  		<li key={dataCenter} className="nav-item">
				<a className={"nav-link" + (dataCenter===this.state.settings.primaryDataCenter ? " active" : "")} data-toggle="pill" href={"#" + dataCenter.replace(/\s+/g, '-').toLowerCase() + "-node-discovery"}>{dataCenter}</a>
		  	</li>
    	);
    }.bind(this));
    var nodeDiscoveryPanes = this.state.settings.dataCenters.map(function(dataCenterObject) {
        var dataCenter = dataCenterObject.name;
    	var nodeDiscoveryEnvironments = dataCenterObject.environments.map(function(environmentObject) {
    	    var environment = environmentObject.name;
    		var applicationNodeDiscoveryRows = this.state.settings.applications.map(function(application, index) {
    			return (
					<div key={dataCenter + "-" + environment + "-" + application} className="form-group row">
						<label htmlFor="example-text-input" className="col-xs-4 col-form-label">{application}</label>
						<div className="col-xs-8">
							{application}
						</div>
					</div>
				);
    		}.bind(this));
    		return(
				<div key={dataCenter + "-" + environment} className="panel panel-default">
				  <div className="panel-heading" role="tab" id={dataCenter + "-" + environment}>
					  <a className="collapsed" data-toggle="collapse" data-parent="#accordion" href={"#" + dataCenter.replace(/\s+/g, '-').toLowerCase() + "-" + environment} aria-expanded="true" aria-controls={dataCenter.replace(/\s+/g, '-').toLowerCase() + "-" + environment}>
						<div className="input-group">
							<span className="input-group-btn">
								<button key={dataCenter + "-" + environment} type="button" className="btn btn-info mega-octicon octicon-triangle-down"></button>
							</span>
							<input value={environment} readOnly className="form-control" type="text" />
						</div>
					  </a>
				  </div>
				  <div id={dataCenter.replace(/\s+/g, '-').toLowerCase() + "-" + environment} className="panel-collapse collapse" role="tabpanel" aria-labelledby={dataCenter + "-" + environment}>
					<div className={"input-group col-xs-6 environment-datacenter-row"}>
						{applicationNodeDiscoveryRows}
					</div>
				  </div>
				</div>
			);
    	}.bind(this));
		return (
			<div key={dataCenter} className={"tab-pane" + (dataCenter===this.state.settings.primaryDataCenter ? " active" : "")} id={dataCenter.replace(/\s+/g, '-').toLowerCase() + "-node-discovery"} role="tabpanel">
				<div id="accordion" role="tablist" aria-multiselectable="true">
					{nodeDiscoveryEnvironments}
				</div>
			</div>
		);
	}.bind(this));
    return (
        <div className="modal fade" id="settings-modal" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
          <div className="modal-dialog modal-lg" role="document">
            <div className="modal-content">
              <div className="modal-header">
                <nav className="navbar navbar-dark bg-inverse">
                  <a className="navbar-brand" href="#"><b>Settings</b></a>
                  <ul className="nav navbar-nav">
                    <li className={"nav-item" + (this.state.activeTab === "generalSettings" ? " active" : "")}>
                      <a className="nav-link" href="#generalSettings" data-tab="generalSettings" onClick={this.changeSettingsNav}>General</a>
                    </li>
                    <li className={"nav-item" + (this.state.activeTab === "applicationSettings" ? " active" : "")}>
                      <a className="nav-link" href="#applicationSettings" data-tab="applicationSettings" onClick={this.changeSettingsNav}>Applications</a>
                    </li>
                    <li className={"nav-item" + (this.state.activeTab === "dataCenterSettings" ? " active" : "")}>
                      <a className="nav-link" href="#dataCenterSettings" data-tab="dataCenterSettings" onClick={this.changeSettingsNav}>Data Centers</a>
                    </li>
                    <li className={"nav-item" + (this.state.activeTab === "environmentSettings" ? " active" : "")}>
                      <a className="nav-link" href="#environmentSettings" data-tab="environmentSettings" onClick={this.changeSettingsNav}>Environments</a>
                    </li>
                    <li className={"nav-item" + (this.state.activeTab === "nodeDiscovery" ? " active" : "")}>
                      <a className="nav-link" href="#nodeDiscovery" data-tab="nodeDiscovery" onClick={this.changeSettingsNav}>Node Discovery</a>
                    </li>
                  </ul>
                </nav>
              </div>
              <div className="modal-body">
              	<form>
                <div style={(this.state.activeTab == "generalSettings" ? {display: 'inline'} : {display: 'none'})}>
                    <div className="form-group row">
                      <label htmlFor="example-text-input" className="col-xs-4 col-form-label">Dashboard Title</label>
                      <div className="col-xs-8">
                        <input value={this.state.titleToAdd} className="form-control" type="text" onChange={this.handleTitleChange} />
                      </div>
                    </div>
                </div>
                <div style={(this.state.activeTab == "applicationSettings" ? {display: 'inline'} : {display: 'none'})}>
                    <fieldset className="form-group row">
                        <legend className="col-form-legend col-xs-4">Applications <span data-toggle="tooltip" title="Drag applications to re-order" data-placement="bottom" className="mega-octicon octicon-question"></span></legend>
                        <div className="col-xs-8">
                          <ul className="list-group" onDragOver={this.dragOver}>
                          {applicationRows}
                          </ul>
                          <div className={"application-input input-group" + (this.state.applicationWarning ? " has-warning" : "")}>
                            <input value={this.state.applicationToAdd} className={"form-control" + (this.state.applicationWarning ? " form-control-warning" : "")} type="text" onChange={this.handleApplicationToAddChange} placeholder="Add an application" />
                            <span className="input-group-btn">
                              <button type="button" className="btn btn-success mega-octicon octicon-plus" onClick={this.addApplication}></button>
                            </span>
                          </div>
                          {applicationWarning}
                        </div>
                    </fieldset>
                </div>
				<div style={(this.state.activeTab == "dataCenterSettings" ? {display: 'inline'} : {display: 'none'})}>
					<fieldset className="form-group row">
						<legend className="col-form-legend col-xs-4">Data Centers</legend>
						<div className="col-xs-8">
						  {dataCenterRows}
						  <div className={"input-group" + (this.state.dataCenterWarning ? " has-warning" : "")}>
							<input value={this.state.dataCenterToAdd} className={"form-control" + (this.state.dataCenterWarning ? " form-control-warning" : "")} type="text" onChange={this.handleDataCenterToAddChange} placeholder="Add a data center" />
							<span className="input-group-btn">
							  <button type="button" className="btn btn-success mega-octicon octicon-plus" onClick={this.addDataCenter}></button>
							</span>
						  </div>
						  {dataCenterWarning}
						</div>
					</fieldset>
					<fieldset className="form-group row">
						<legend className="col-form-legend col-xs-4">Select Primary <span data-toggle="tooltip" title="Selected data center will appear by default when dashboard loads" data-placement="bottom" className="mega-octicon octicon-question"></span></legend>
						<div className="col-xs-8">
						  <div className="btn-group" data-toggle="buttons">
						  	{primaryDataCenterSelect}
                          </div>
						</div>
					</fieldset>
				</div>
                <div style={(this.state.activeTab == "environmentSettings" ? {display: 'inline'} : {display: 'none'})}>
                    <fieldset className="form-group row">
                        <legend className="col-form-legend col-xs-4">Environments</legend>
                        <div className="col-xs-8">
                            <div id="accordion" role="tablist" aria-multiselectable="true">
								{dataCenterEnvironmentRows}
                            </div>
                        </div>
                    </fieldset>
                </div>
                <div style={(this.state.activeTab == "nodeDiscovery" ? {display: 'inline'} : {display: 'none'})}>
					<ul className="nav nav-pills">
					  {nodeDiscoveryTabs}
					</ul>
					<div className="tab-content">
					  {nodeDiscoveryPanes}
					</div>
                </div>
                </form>
              </div>
              <div className="modal-footer">
              	<a href="/api/data/settingsJson" download="dashboard-settings.json"><button type="button" className="btn btn-secondary pull-xs-left mega-octicon octicon-cloud-download" data-toggle="tooltip" title="Export Settings" data-placement="bottom" /></a>
              	<label className="btn btn-secondary pull-xs-left mega-octicon octicon-cloud-upload" data-toggle="tooltip" title="Import Settings" data-placement="bottom">
                    <input type="file" style={{display: 'none'}} onChange={this.importSettings} value={this.state.importFile} />
                </label>
                <div style={(this.state.importAlert ? {display: 'inline'} : {display: 'none'})} className="alert alert-info pull-xs-left fade in" role="alert">{this.state.importAlert}</div>
                <button type="button" className="btn btn-secondary" data-dismiss="modal" onClick={this.handleClose}>Close</button>
                <button type="button" className="btn btn-primary" data-dismiss="modal" onClick={this.handleSave}>Save</button>
              </div>
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
        <div className="pull-xs-right" data-toggle="tooltip" data-placement="left" title="Settings">
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
          <a className={"nav-link" + (dataCenter===this.props.primaryDataCenter ? ' active' : '')} data-toggle="tab" href={'#' + dataCenter.replace(/\s+/g, '-').toLowerCase()} role="tab"><h5>{dataCenter}</h5></a>
        </li>
      );
    }.bind(this));
    return (
      <ul className="nav nav-tabs">{dataCenterTabs}</ul>
    );
  }
});

var DataCenterDashboards = React.createClass({
  render: function() {
    var dataCenterDashboards = $.map(this.props.data.dataCenters, function(value, dataCenter) {
      return (
        <div key={dataCenter} className={"tab-pane" + (dataCenter===this.props.primaryDataCenter ? ' active' : '')} id={dataCenter.replace(/\s+/g, '-').toLowerCase()} role="tabpanel">
          <DataCenterDashboard dataCenter={value} />
        </div>
      );
    }.bind(this));
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
    return {data: {}, settings: {}};
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
                <div id="data-center-tabs"><DataCenterTabs data={this.state.data} primaryDataCenter={this.state.settings.primaryDataCenter} /></div>
                <div id="data-center-tab-content"><DataCenterDashboards data={this.state.data} primaryDataCenter={this.state.settings.primaryDataCenter} /></div>
            </div>
            <div id="footer-bar"><FooterBar data={this.state.data} /></div>
            <ModalSettings settings={this.state.settings} onSave={this.handleSaveSettings} />
        </div>
    );
  }
});

ReactDOM.render(<Parent url="/api/data" settingsUrl="/api/data/settings" pollInterval={10000} />, document.getElementById('parent'));
$(document).ready(function(){
    $('[data-toggle="tooltip"]').tooltip();
});