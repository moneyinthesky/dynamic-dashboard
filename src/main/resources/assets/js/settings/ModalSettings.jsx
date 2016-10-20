class ModalSettings extends React.Component {

    constructor(props) {
        super(props);

        this.state = {
            settings : {
                title: "",
                applications: [],
                applicationConfig: {},
                dataCenters: [],
                plugins: {
                	fleet: {
                		restApiUrls: []
                	},
                	aws: {}
                }
            },
            applicationToAdd: "",
            dataCenterToAdd: "",
            environmentToAdd: {},
            fleetRestApiUrlToAdd: "",
            applicationWarning: "",
            dataCenterWarning: "",
            fleetRestApiUrlWarning: "",
            activeTab: "basicConfiguration",
            importAlert: "",
            importFile: ""
        };

        this.componentWillReceiveProps = (nextProps) => {
            if(!(JSON.stringify(this.props.settings) === JSON.stringify(nextProps.settings))) {
                this.setState({settings : nextProps.settings});
            }
        };

        this.handleTitleChange = (event) => {
        	this.state.settings.title = event.target.value;
            this.setState({settings : this.state.settings});
        };

        this.handleApplicationToAddChange = (event) => {
            this.setState({applicationToAdd: event.target.value, applicationWarning: ''});
        };


        this.handleDataCenterToAddChange = (event) => {
            this.setState({dataCenterToAdd: event.target.value, dataCenterWarning: ''});
        };

        this.handleEnvironmentChange = (event) => {
            var dataCenter = event.target.dataset.datacenter;
            this.state.environmentToAdd[dataCenter] = event.target.value;
            this.setState({environmentToAdd : this.state.environmentToAdd, environmentWarning: ''});
        };

		this.handleFleetRestApiUrlToAddChange = (event) => {
			this.setState({fleetRestApiUrlToAdd: event.target.value, fleetRestApiUrlWarning: ''});
		};

		this.handleNodeDiscoveryMethodSelect = (event) => {
			var dataCenterObject = getByName(this.state.settings.dataCenters, event.target.dataset.datacenter);
			var environmentObject = getByName(dataCenterObject.environments, event.target.dataset.environment);
			environmentObject.nodeDiscoveryMethod = event.target.value;
			environmentObject.applicationConfig = {};

			this.setState({settings : this.state.settings});
		};

		this.handleApplicationConfigChange = (event) => {
			var dataCenterObject = getByName(this.state.settings.dataCenters, event.target.dataset.datacenter);
			var environmentObject = getByName(dataCenterObject.environments, event.target.dataset.environment);
			var application = event.target.dataset.application;

			if(!environmentObject.applicationConfig) environmentObject.applicationConfig={};
			if(!environmentObject.applicationConfig[application]) environmentObject.applicationConfig[application]={};

			environmentObject.applicationConfig[application][event.target.dataset.field] = event.target.value;
			this.setState({settings: this.state.settings});
		};

        this.handleApplicationConfigUpdate = (event) => {
            if(!this.state.settings.applicationConfig) {
                this.state.settings.applicationConfig = {};
            }

            var applicationConfig = this.state.settings.applicationConfig;
            if(!applicationConfig[event.target.dataset.application]) {
                applicationConfig[event.target.dataset.application] = {};
            }

            applicationConfig[event.target.dataset.application][event.target.dataset.field] = event.target.value;

            this.setState({settings: this.state.settings});
        };

        this.addApplication = (event) => {
            if(this.state.applicationToAdd && !this.state.settings.applications.includes(this.state.applicationToAdd)) {
                this.state.settings.applications.push(this.state.applicationToAdd);
                this.setState({ settings : this.state.settings, applicationToAdd: '' });
            } else if(this.state.settings.applications.includes(this.state.applicationToAdd)) {
                this.setState({applicationWarning: 'Application already added'})
            } else {
                this.setState({applicationWarning: 'Application name is required'});
            }
        };

        this.addDataCenter = (event) => {
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
        };

        this.addEnvironment = (event) => {
            var dataCenter = event.target.dataset.datacenter;
            var environment = this.state.environmentToAdd[dataCenter];
            if(!environment) {
                this.setState({environmentWarning : 'Environment name is required'});
                return;
            }

            var dataCenterObject = getByName(this.state.settings.dataCenters, dataCenter);
            var existingEnvironment = getByName(dataCenterObject.environments, environment);
            if(existingEnvironment) {
                this.setState({environmentWarning : 'Environment already added'});
                return;
            }

            dataCenterObject.environments.push({name: environment});
            this.state.environmentToAdd[dataCenter] = "";
            this.setState({ settings : this.state.settings, environmentToAdd : this.state.environmentToAdd});
        };

        this.removeApplication = (event) => {
            this.state.settings.applications.splice(event.target.dataset.index, 1);
            this.setState({settings : this.state.settings});
        };

        this.removeDataCenter = (event) => {
            delete this.state.environmentToAdd[event.target.dataset.datacenter];
            this.state.settings.dataCenters = removeByName(this.state.settings.dataCenters, event.target.dataset.datacenter);
            this.setState({settings : this.state.settings, environmentToAdd : this.state.environmentToAdd});
        };

        this.removeEnvironment = (event) => {
            var dataCenter = getByName(this.state.settings.dataCenters, event.target.dataset.datacenter);
            dataCenter.environments = removeByName(dataCenter.environments, event.target.dataset.environment);
            this.setState({settings : this.state.settings});
        };

        this.removeDataCenterEnvironment = (event) => {
            var environmentToRemove = event.target.dataset.datacenterEnvironment.split("/");
            var dataCenterObject = getByName(this.state.settings.dataCenters, environmentToRemove[0]);
            dataCenterObject.environments = removeByName(dataCenterObject.environments, environmentToRemove[1]);
            this.setState({settings : this.state.settings});
        };

		this.generateDataCenterEnvironmentList = () => {
			var dataCenterEnvironmentList = [];
			this.state.settings.dataCenters.map((dataCenterObject) => {
				dataCenterObject.environments.map((environmentObject) => {
					 dataCenterEnvironmentList.push(dataCenterObject.name + " / " + environmentObject.name);
				});
			});
			return dataCenterEnvironmentList;
		}

		this.getDiscoveryMethodForDataCenterEnvironment = (dataCenterEnvironment) => {
			var dataCenterEnvironmentArray = dataCenterEnvironment.split('/');
			var dataCenter = dataCenterEnvironmentArray[0].trim();
			var environment = dataCenterEnvironmentArray[1].trim();

			var dataCenterObject = getByName(this.state.settings.dataCenters, dataCenter);
			var environmentObject = getByName(dataCenterObject.environments, environment);
			return environmentObject.nodeDiscoveryMethod ? environmentObject.nodeDiscoveryMethod : "";
		}

		this.isNodeDiscoverConfigComplete = (dataCenterEnvironment) => {
			var dataCenterEnvironmentArray = dataCenterEnvironment.split('/');
			var dataCenter = dataCenterEnvironmentArray[0].trim();
			var environment = dataCenterEnvironmentArray[1].trim();

            var dataCenterObject = getByName(this.state.settings.dataCenters, dataCenter);
            var environmentObject = getByName(dataCenterObject.environments, environment);

            if(!environmentObject.applicationConfig) return false;

            if(environmentObject.nodeDiscoveryMethod==="urlPattern") {
            	var applicationConfigPresent = (this.state.settings.applications.map((application) => {
                    if(!environmentObject.applicationConfig[application]) return false;

                    if((!environmentObject.applicationConfig[application].urlPattern) ||
                    	(environmentObject.applicationConfig[application].urlPattern === "")) return false;

                    return true;
            	}));

            	if(applicationConfigPresent.indexOf(false) !== -1) return false;
            } else if(environmentObject.nodeDiscoveryMethod==="") {
            	return false;
            }

            return true;
		}

        this.getApplicationConfig = (dataCenter, environment, application, field) => {
            var dataCenterObject = getByName(this.state.settings.dataCenters, dataCenter);
            var environmentObject = getByName(dataCenterObject.environments, environment);

            if(environmentObject.applicationConfig) {
                if(environmentObject.applicationConfig[application]) {
                    if(environmentObject.applicationConfig[application][field]) {
                        return environmentObject.applicationConfig[application][field];
                    }
                }
            }

            return "";
        };

        this.isFirstDataCenter = (dataCenter) => {
            return this.state.settings.dataCenters[0].name === dataCenter;
        };

        this.changeSettingsNav = (event) => {
            this.setState({activeTab: event.target.dataset.tab});
        };

        this.dragStart = (e) => {
            this.dragged = e.currentTarget;
            e.dataTransfer.effectAllowed = 'move';
            e.dataTransfer.setData("text/html", e.currentTarget);
        };

        this.dragEndApplications = (e) => {
            this.dragged.style.display = "block";
            this.dragged.parentNode.removeChild(placeholder);

            var data = this.state.settings.applications;
            var from = Number(this.dragged.dataset.id);
            var to = Number(this.over.dataset.id);

            if(from < to) to--;
            if(this.nodePlacement == "after") to++;
            data.splice(to, 0, data.splice(from, 1)[0]);

            this.setState({data: data});
        };

        this.dragEndDataCenters = (e) => {
            this.dragged.style.display = "block";
            this.dragged.parentNode.removeChild(placeholder);

            var data = this.state.settings.dataCenters;
            var from = Number(this.dragged.dataset.id);
            var to = Number(this.over.dataset.id);

            if(from < to) to--;
            if(this.nodePlacement == "after") to++;
            data.splice(to, 0, data.splice(from, 1)[0]);

            this.setState({data: data});
        }

        this.dragEndEnvironments = (e) => {
            this.dragged.style.display = "block";
            this.dragged.parentNode.removeChild(placeholder);

            var dataCenter = e.target.dataset.datacenter;
            var data = getByName(this.state.settings.dataCenters, dataCenter).environments;
            var from = Number(this.dragged.dataset.id);
            var to = Number(this.over.dataset.id);

            if(from < to) to--;
            if(this.nodePlacement == "after") to++;
            data.splice(to, 0, data.splice(from, 1)[0]);

            this.setState({data: data});
        }

        this.dragOver = (e) => {
            var nodeDraggedOver = e.target.nodeName !== "LI" ? e.target.parentNode : e.target;

            e.preventDefault();
            this.dragged.style.display = "none";
            if(nodeDraggedOver.className == "placeholder") return;
            this.over = nodeDraggedOver;

            var relY = e.clientY - this.over.offsetTop;
            var height = this.over.offsetHeight / 2;
            var parent = nodeDraggedOver.parentNode;

            if(relY > height) {
                this.nodePlacement = "after";
                parent.insertBefore(placeholder, nodeDraggedOver.nextElementSibling);
            }
            else if(relY < height) {
                if(nodeDraggedOver.dataset.id !== 'title') {
                    this.nodePlacement = "before"
                    parent.insertBefore(placeholder, nodeDraggedOver);
                }
            }
        };

        this.handleSave = () => {
            this.props.onSave(this.state.settings);
            this.setState({activeTab: "basicConfiguration", importAlert: ""});
        };

        this.handleClose = () => {
            this.setState({activeTab: "basicConfiguration"});
        };

        this.importSettings = (event) => {
            var file = event.target.files[0];
            var reader = new FileReader();

            reader.onload = (e) => {
                var copyOfState = JSON.parse(reader.result);
                this.setState({settings: copyOfState, importAlert: "Import successful, Save when ready", importFile: ""});
            };

            reader.readAsText(file);
        };

        this.render = () => {
            var applicationWarning = this.state.applicationWarning ? <div className="form-control-feedback alert alert-warning" role="alert">{this.state.applicationWarning}</div> : "";
            var dataCenterWarning = this.state.dataCenterWarning ? <div className="form-control-feedback alert alert-warning" role="alert">{this.state.dataCenterWarning}</div> : "";
            var environmentWarning = this.state.environmentWarning ? <div className="form-control-feedback alert alert-warning" role="alert">{this.state.environmentWarning}</div> : "";
            var fleetRestApiUrlWarning = this.state.fleetRestApiUrlWarning ? <div className="form-control-feedback alert alert-warning" role="alert">{this.state.fleetRestApiUrlWarning}</div> : "";
            var applicationRows = this.state.settings.applications.map((application, index) => {
        	  return (
        		<li className="list-group-item clearfix" data-id={index} key={index} draggable="true" onDragEnd={this.dragEndApplications} onDragStart={this.dragStart}>
        		    <button type="button" className="btn mega-octicon octicon-three-bars pull-xs-right" data-toggle="tooltip" title="Drag to re-order" data-placement="bottom"></button>
        		    <button type="button" data-index={index} className="btn btn-danger mega-octicon octicon-dash pull-xs-right" onClick={this.removeApplication}></button>
                    {application}
                </li>
        	  );
        	});
            var dataCenterRows = this.state.settings.dataCenters.map((dataCenterObject, index) => {
              var dataCenter = dataCenterObject.name;
        	  return (
        		<li className="list-group-item clearfix" data-id={index} key={index} draggable="true" onDragEnd={this.dragEndDataCenters} onDragStart={this.dragStart}>
        		    <button type="button" className="btn mega-octicon octicon-three-bars pull-xs-right" data-toggle="tooltip" title="Drag to re-order" data-placement="bottom"></button>
        		    <button type="button" data-datacenter={dataCenter} className="btn btn-danger mega-octicon octicon-dash pull-xs-right" onClick={this.removeDataCenter}></button>
        		    <button type="button" className="environment-count btn btn-info pull-xs-right">{dataCenterObject.environments.length} Env</button>
                    {dataCenter}
                </li>
        	  );
        	});
        	var dataCenterConfigurationTabs = this.state.settings.dataCenters.map((dataCenterObject, index) => {
        	    var dataCenter = dataCenterObject.name;
        	    return (
                    <li key={index} className="nav-item">
                      <a className={"nav-link" + (index===0 ? " active" : "")} data-toggle="tab" href={"#" + dataCenter.replace(/\s+/g, '-').toLowerCase() + "-environment-config"} role="tab">{dataCenter}</a>
                    </li>
        	    );
        	});
        	var dataCenterConfigurationContent = this.state.settings.dataCenters.map((dataCenterObject, index) => {
        	    var dataCenter = dataCenterObject.name;
        	    var environmentRows = dataCenterObject.environments.map((environmentObject, index) => {
        	        var environment = environmentObject.name;
                    return (
                        <li key={index} className="list-group-item clearfix" data-datacenter={dataCenter} data-id={index} draggable="true" onDragEnd={this.dragEndEnvironments} onDragStart={this.dragStart}>
                            <button type="button" className="btn mega-octicon octicon-three-bars pull-xs-right" data-toggle="tooltip" title="Drag to re-order" data-placement="bottom"></button>
                            <button type="button" data-datacenter={dataCenter} data-environment={environment} className="btn btn-danger mega-octicon octicon-dash pull-xs-right" onClick={this.removeEnvironment}></button>
                            {environment}
                        </li>
                    );
        	    });
        	    return (
        	        <div key={index} className={"tab-pane" + (index===0 ? " active" : "")} id={dataCenter.replace(/\s+/g, '-').toLowerCase() + "-environment-config"} role="tabpanel">
        	            <ul className="list-group" onDragOver={this.dragOver}>
                          <li className="list-group-item list-group-item-action list-group-item-info heading-bar clearfix" data-id="title"></li>
                          {environmentRows}
                        </ul>
                        <div className={"environment-input input-group" + (this.state.environmentWarning ? " has-warning" : "")}>
                          <input value={this.state.environmentToAdd[dataCenter]} data-datacenter={dataCenter} className={"form-control" + (this.state.environmentWarning ? " form-control-warning" : "")} type="text" onChange={this.handleEnvironmentChange} placeholder="Add an environment" />
                          <span className="input-group-btn">
                            <button type="button" className="btn btn-success mega-octicon octicon-plus" data-datacenter={dataCenter} onClick={this.addEnvironment}></button>
                          </span>
                        </div>
                        {environmentWarning}
        	        </div>
        	    );
        	});
        	var applicationConfigurationTabs = this.state.settings.applications.map((application, index) => {
        	    return (
                    <li key={index} className="nav-item">
                        <a className={"nav-link" + ((index===0) ? " active" : "")} data-toggle="tab" href={"#" + application + "-application-configuration"} role="tab">{application}</a>
                    </li>
                );
        	});
        	var applicationConfigurationContent = this.state.settings.applications.map((application, index) => {
        	    var currentAppStatusUri = this.state.settings.applicationConfig ?
        	        (this.state.settings.applicationConfig[application] ? this.state.settings.applicationConfig[application].statusUri : "") : "";
                var currentAppInfoUri = this.state.settings.applicationConfig ?
        	        (this.state.settings.applicationConfig[application] ? this.state.settings.applicationConfig[application].infoUri : "") : "";
        	    return (
                    <div key={index} className={"tab-pane" + (index===0 ? " active" : "")} id={application + "-application-configuration"} role="tabpanel">
						<div className="form-group row">
							<label className="col-xs-4 col-form-label">Status URI</label>
							<div className="col-xs-8">
								<input value={currentAppStatusUri} data-application={application} data-field="statusUri" className="form-control" type="text" onChange={this.handleApplicationConfigUpdate} placeholder="Add status URI" />
							</div>
						</div>
						<div className="form-group row">
							<label className="col-xs-4 col-form-label">Info URI</label>
							<div className="col-xs-8">
								<input value={currentAppInfoUri} data-application={application} data-field="infoUri" className="form-control" type="text" onChange={this.handleApplicationConfigUpdate} placeholder="Add info URI" />
							</div>
						</div>
					</div>
        	    );
        	});
        	var nodeDiscoveryTabs = this.generateDataCenterEnvironmentList().map((dataCenterEnvironment, index) => {
        		var nodeDiscoveryTagSpan = this.isNodeDiscoverConfigComplete(dataCenterEnvironment) ? (
        			<span className="node-discovery-tag-complete tag tag-default tag-pill pull-xs-right mega-octicon octicon-check" data-toggle="tooltip" data-placement="top" title="Configuration complete"> </span>
        		) : (
        			<span className="node-discovery-tag-incomplete tag tag-default tag-pill pull-xs-right mega-octicon octicon-alert" data-toggle="tooltip" data-placement="top" title="Configuration incomplete"> </span>
        		);
				return (
					<li key={index} className="nav-item">
						{nodeDiscoveryTagSpan}
						<a className={"nav-link" + ((index===0) ? " active" : "")} data-toggle="tab" href={"#" + dataCenterEnvironment.replace('/','').replace(/\s+/g, '-').toLowerCase() + "-node-discovery"} role="tab">{dataCenterEnvironment}</a>
					</li>
				);
        	});
        	var nodeDiscoveryContent = this.generateDataCenterEnvironmentList().map((dataCenterEnvironment, index) => {
        		var dataCenterEnvironmentArray = dataCenterEnvironment.split('/');
        		var dataCenter = dataCenterEnvironmentArray[0].trim();
        		var environment = dataCenterEnvironmentArray[1].trim();
        		var nodeDiscoveryConfigForm = this.getDiscoveryMethodForDataCenterEnvironment(dataCenterEnvironment)==="urlPattern" ? (
        			this.state.settings.applications.map((application, index) => {
        				var currentUrlPattern = this.getApplicationConfig(dataCenter, environment, application, "urlPattern");
        				return (
	        				<div key={index} className="form-group row">
								<label>{application}</label>
                                <div className="input-group">
                                    <span className="input-group-addon" id="basic-addon1">URL Pattern: </span>
                                    <input value={currentUrlPattern} data-field="urlPattern" data-datacenter={dataCenter} data-environment={environment} data-application={application} className="form-control" type="text" onChange={this.handleApplicationConfigChange} placeholder="Add a URL pattern" />
                                </div>
							</div>
        				);
        			})
        		) : (this.getDiscoveryMethodForDataCenterEnvironment(dataCenterEnvironment)==="fleet" ? (
                    this.state.settings.applications.map((application, index) => {
        		        var fleetRestUrl = this.getApplicationConfig(dataCenter, environment, application, "fleetRestUrl");
        		        var appId = this.getApplicationConfig(dataCenter, environment, application, "appId");
        		        var envId = this.getApplicationConfig(dataCenter, environment, application, "envId");
        		        var dataCenterId = this.getApplicationConfig(dataCenter, environment, application, "dataCenterId");
        		        var roleId = this.getApplicationConfig(dataCenter, environment, application, "roleId");
                        return (
                            <div key={index} className="form-group row">
                                <label>{application}</label>
                                <div className="input-group">
                                    <span className="input-group-addon" id="basic-addon1">Fleet Rest URL: </span>
                                    <input value={fleetRestUrl} data-field="fleetRestUrl" data-datacenter={dataCenter} data-environment={environment} data-application={application} className="form-control" type="text" onChange={this.handleApplicationConfigChange} placeholder="Add Fleet Rest URL" />
                                </div>
                                <div className="input-group">
                                    <span className="input-group-addon" id="basic-addon1">Application ID: </span>
                                    <input value={appId} data-field="appId" data-datacenter={dataCenter} data-environment={environment} data-application={application} className="form-control" type="text" onChange={this.handleApplicationConfigChange} placeholder="Add Application ID" />
                                </div>
                                <div className="input-group">
                                    <span className="input-group-addon" id="basic-addon1">Environment ID: </span>
                                    <input value={envId} data-field="envId" data-datacenter={dataCenter} data-environment={environment} data-application={application} className="form-control" type="text" onChange={this.handleApplicationConfigChange} placeholder="Add Environment ID" />
                                </div>
                                <div className="input-group">
									<span className="input-group-addon" id="basic-addon1">Data Center ID: </span>
									<input value={dataCenterId} data-field="dataCenterId" data-datacenter={dataCenter} data-environment={environment} data-application={application} className="form-control" type="text" onChange={this.handleApplicationConfigChange} placeholder="Add Data Center ID" />
								</div>
                                <div className="input-group">
                                    <span className="input-group-addon" id="basic-addon1">Role ID: </span>
                                    <input value={roleId} data-field="roleId" data-datacenter={dataCenter} data-environment={environment} data-application={application} className="form-control" type="text" onChange={this.handleApplicationConfigChange} placeholder="Add Role ID" />
                                </div>
                            </div>
                        );
                    })
        		) : "");
				return (
					<div key={index} className={"tab-pane" + (index===0 ? " active" : "")} id={dataCenterEnvironment.replace('/','').replace(/\s+/g, '-').toLowerCase() + "-node-discovery"} role="tabpanel">
						<div className="form-group row">
							<label className="col-xs-6 col-form-label">Node Discovery Method</label>
							<div className="col-xs-6">
								<select value={this.getDiscoveryMethodForDataCenterEnvironment(dataCenterEnvironment)} className="custom-select" data-datacenter={dataCenter} data-environment={environment} onChange={this.handleNodeDiscoveryMethodSelect}>
									<option value="">Select method</option>
									<option value="urlPattern">URL Pattern</option>
									<option value="fleet">Fleet</option>
									<option value="route53">AWS Route 53</option>
								</select>
							</div>
						</div>
						<hr/>
						{nodeDiscoveryConfigForm}
					</div>
				);
			});
			var fleetRestUrlRows = this.state.settings.plugins.fleet.restApiUrls.map((url, index) => {
				return (
					<li className="list-group-item clearfix" key={index}>
						<button type="button" data-index={index} className="btn btn-danger mega-octicon octicon-dash pull-xs-right" onClick={this.removeFleetRestApiUrl}></button>
						{url}
					</li>
				);
			});
            return (
                <div className="modal fade" id="settings-modal" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
                  <div className="modal-dialog modal-lg" role="document">
                    <div className="modal-content">
                      <div className="modal-header">
                        <nav className="navbar navbar-dark bg-inverse">
                          <a className="navbar-brand" href="#"><b>Settings</b></a>
                          <ul className="nav navbar-nav">
                            <li className={"nav-item" + (this.state.activeTab === "basicConfiguration" ? " active" : "")}>
                              <a className="nav-link" href="#basicConfiguration" data-tab="basicConfiguration" onClick={this.changeSettingsNav}>Basic</a>
                            </li>
                            <li className={"nav-item" + (this.state.activeTab === "applicationConfiguration" ? " active" : "")}>
                              <a className="nav-link" href="#applicationConfiguration" data-tab="applicationConfiguration" onClick={this.changeSettingsNav}>Applications</a>
                            </li>
                            <li className={"nav-item" + (this.state.activeTab === "nodeDiscovery" ? " active" : "")}>
                              <a className="nav-link" href="#nodeDiscovery" data-tab="nodeDiscovery" onClick={this.changeSettingsNav}>Node Discovery</a>
                            </li>
                            <li className={"nav-item" + (this.state.activeTab === "plugins" ? " active" : "")}>
							  <a className="nav-link" href="#plugins" data-tab="plugins" onClick={this.changeSettingsNav}>Plugins</a>
							</li>
                            <li className={"nav-item" + (this.state.activeTab === "generalSettings" ? " active" : "")}>
                              <a className="nav-link" href="#generalSettings" data-tab="generalSettings" onClick={this.changeSettingsNav}>Miscellaneous</a>
                            </li>
                          </ul>
                        </nav>
                      </div>
                      <div className="modal-body">
                      	<form>
                        <div style={(this.state.activeTab == "basicConfiguration" ? {display: 'inline'} : {display: 'none'})}>
                            <div id="accordion" role="tablist" aria-multiselectable="true">
                              <div className="panel panel-default">
                                <div className="configuration-heading panel-heading" role="tab" id="headingOne" data-parent="#accordion" data-toggle="collapse" data-target="#collapseOne">
                                  <div className="panel-title-config panel-title">
                                      <span className="mega-octicon octicon-primitive-dot"> </span>
                                      Applications
                                  </div>
                                </div>
                                <div id="collapseOne" className="panel-collapse collapse in" role="tabpanel" aria-labelledby="headingOne">
                                    <div className="container-fluid">
                                    <div className="row">
                                    <div className="col-xs-8">
                                    <ul className="list-group" onDragOver={this.dragOver}>
                                      <li className="list-group-item list-group-item-action list-group-item-info heading-bar clearfix" data-id="title"></li>
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
                                    </div>
                                    </div>
                                </div>
                              </div>
                              <div className="panel panel-default">
                                <div className="configuration-heading panel-heading" role="tab" id="headingTwo" data-parent="#accordion" data-toggle="collapse" data-target="#collapseTwo">
                                  <div className="panel-title-config panel-title">
                                      <span className="mega-octicon octicon-primitive-dot"> </span>
                                      Data Centers
                                  </div>
                                </div>
                                <div id="collapseTwo" className="panel-collapse collapse" role="tabpanel" aria-labelledby="headingTwo">
                                  <div className="container-fluid">
                                  <div className="row">
                                  <div className="col-xs-8">
                                  <ul className="list-group" onDragOver={this.dragOver}>
                                      <li className="list-group-item list-group-item-action list-group-item-info heading-bar clearfix" data-id="title"></li>
                                      {dataCenterRows}
                                  </ul>
                                  <div className={"datacenter-input input-group" + (this.state.dataCenterWarning ? " has-warning" : "")}>
                                    <input value={this.state.dataCenterToAdd} className={"form-control" + (this.state.dataCenterWarning ? " form-control-warning" : "")} type="text" onChange={this.handleDataCenterToAddChange} placeholder="Add a data center" />
                                    <span className="input-group-btn">
                                      <button type="button" className="btn btn-success mega-octicon octicon-plus" onClick={this.addDataCenter}></button>
                                    </span>
                                  </div>
                                  {dataCenterWarning}
                                  </div>
                                  </div>
                                  </div>
                                </div>
                              </div>
                              <div className="panel panel-default">
                                <div className="configuration-heading panel-heading" role="tab" id="headingThree" data-parent="#accordion" data-toggle="collapse" data-target="#collapseThree">
                                  <div className="panel-title-config panel-title">
                                      <span className="mega-octicon octicon-primitive-dot"> </span>
                                      Environments
                                  </div>
                                </div>
                                <div id="collapseThree" className="panel-collapse collapse" role="tabpanel" aria-labelledby="headingThree">
                                  <div className="container-fluid">
                                  <div className="row">
                                  <div className="col-xs-4">
                                      <ul className="nav nav-pills nav-stacked" role="tablist">
                                        {dataCenterConfigurationTabs}
                                      </ul>
                                  </div>
                                  <div className="col-xs-8">
                                      <div className="tab-content">
                                        {dataCenterConfigurationContent}
                                      </div>
                                  </div>
                                  </div>
                                  </div>
                                </div>
                              </div>
                            </div>
                        </div>
                        <div style={(this.state.activeTab == "applicationConfiguration" ? {display: 'inline'} : {display: 'none'})}>
							<div className="container-fluid">
                            	<div className="row">
                                	<div className="col-xs-4">
                                    	<ul className="nav nav-pills nav-stacked" role="tablist">
                                        	{applicationConfigurationTabs}
                                    	</ul>
                                  	</div>
                                  	<div className="col-xs-8">
                                    	<div className="tab-content">
                                    		{applicationConfigurationContent}
                                    	</div>
                                	</div>
                            	</div>
                            </div>
                        </div>
                        <div style={(this.state.activeTab == "nodeDiscovery" ? {display: 'inline'} : {display: 'none'})}>
							<div className="container-fluid">
                            	<div className="row">
                                	<div className="col-xs-4">
                                    	<ul className="nav nav-pills nav-stacked" role="tablist">
                                        	{nodeDiscoveryTabs}
                                    	</ul>
                                  	</div>
                                  	<div className="col-xs-8">
                                    	<div className="tab-content">
                                    		{nodeDiscoveryContent}
                                    	</div>
                                	</div>
                            	</div>
                            </div>
                        </div>
                        <div style={(this.state.activeTab == "plugins" ? {display: 'inline'} : {display: 'none'})}>
							<div className="container-fluid">
                            	<div className="row">
                                	<div className="col-xs-4">
                                    	<ul className="nav nav-pills nav-stacked" role="tablist">
											<li key="fleet" className="nav-item">
												<a className="nav-link active" data-toggle="tab" href="#fleetPlugin" role="tab">Fleet</a>
											</li>
											<li key="aws" className="nav-item">
												<a className="nav-link" data-toggle="tab" href="#awsPlugin" role="tab">AWS Route 53</a>
											</li>
                                    	</ul>
                                  	</div>
                                  	<div className="col-xs-8">
                                    	<div className="tab-content">
											<div key="fleet" className="tab-pane active" id="fleetPlugin" role="tabpanel">
												<div className="form-group row">
													<label className="col-xs-6 col-form-label">Fleet REST API URLs</label>
													<div className="col-xs-6">
														<ul className="list-group">
														  {fleetRestUrlRows}
														</ul>
														<div className="application-input input-group">
														  <input value={this.state.fleetRestApiUrlToAdd} className="form-control" + (this.state.fleetRestUrlWarning ? " form-control-warning" : "")} type="text" onChange={this.handleFleetRestApiUrlToAddChange} placeholder="Add a Fleet REST API URL" />
														  <span className="input-group-btn">
															<button type="button" className="btn btn-success mega-octicon octicon-plus" onClick={this.addFleetRestApiUrl}></button>
														  </span>
														</div>
														{fleetRestApiUrlWarning}
													</div>
												</div>
											</div>
											<div key="aws" className="tab-pane" id="awsPlugin" role="tabpanel">
												<div className="form-group row">
													<label className="col-xs-6 col-form-label">AWS Credentials</label>
													<div className="col-xs-6">

													</div>
												</div>
											</div>
                                    	</div>
                                	</div>
                            	</div>
                            </div>
                        </div>
                        <div style={(this.state.activeTab == "generalSettings" ? {display: 'inline'} : {display: 'none'})}>
                          <div className="form-group row">
                            <label htmlFor="example-text-input" className="col-xs-4 col-form-label">Dashboard Title</label>
                            <div className="col-xs-8">
                              <input value={this.state.settings.title} className="form-control" type="text" onChange={this.handleTitleChange} />
                            </div>
                          </div>
                        </div>
                        </form>
                      </div>
                      <div className="modal-footer">
                      	<a href="/api/settings" download="dashboard-settings.json"><button type="button" className="btn btn-secondary pull-xs-left mega-octicon octicon-cloud-download" data-toggle="tooltip" title="Export Settings" data-placement="bottom" /></a>
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
        };
    }
}
