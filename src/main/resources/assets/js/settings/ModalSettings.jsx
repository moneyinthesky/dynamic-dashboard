class ModalSettings extends React.Component {

    constructor(props) {
        super(props);

        this.state = {
            settings : {
                title: "",
                applications: [],
                dataCenters: []
            },
            titleToAdd: "",
            applicationToAdd: "",
            dataCenterToAdd: "",
            environmentToAdd: {},
            applicationWarning: "",
            dataCenterWarning: "",
            activeTab: "applicationSettings",
            importAlert: "",
            importFile: ""
        };

        this.componentWillReceiveProps = (nextProps) => {
            if(!(JSON.stringify(this.props.settings) === JSON.stringify(nextProps.settings))) {
                this.setState({settings : nextProps.settings, titleToAdd : nextProps.settings.title});
            }
        };

        this.handleTitleChange = (event) => {
            this.setState({titleToAdd : event.target.value});
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
            this.state.settings.title = this.state.titleToAdd;
            this.props.onSave(this.state.settings);
            this.setState({activeTab: "applicaitonSettings", importAlert: ""});
        };

        this.handleClose = () => {
            this.setState({activeTab: "applicationSettings"});
        };

        this.importSettings = (event) => {
            var file = event.target.files[0];
            var reader = new FileReader();

            reader.onload = (e) => {
                var copyOfState = JSON.parse(reader.result);
                copyOfState.importAlert = "Import successful, Save when ready";
                copyOfState.importFile = "";
                this.setState(copyOfState);
            };

            reader.readAsText(file);
        };

        this.render = () => {
            var applicationWarning = this.state.applicationWarning ? <div className="form-control-feedback alert alert-warning" role="alert">{this.state.applicationWarning}</div> : "";
            var dataCenterWarning = this.state.dataCenterWarning ? <div className="form-control-feedback alert alert-warning" role="alert">{this.state.dataCenterWarning}</div> : "";
            var environmentWarning = this.state.environmentWarning ? <div className="form-control-feedback alert alert-warning" role="alert">{this.state.environmentWarning}</div> : "";
            var applicationRows = this.state.settings.applications.map(function(application, index) {
        	  return (
        		<li className="list-group-item clearfix" data-id={index} key={index} draggable="true" onDragEnd={this.dragEndApplications} onDragStart={this.dragStart}>
        		    <button type="button" className="btn mega-octicon octicon-three-bars pull-xs-right" data-toggle="tooltip" title="Drag to re-order" data-placement="bottom"></button>
        		    <button type="button" data-index={index} className="btn btn-danger mega-octicon octicon-dash pull-xs-right" onClick={this.removeApplication}></button>
                    {application}
                </li>
        	  );
        	}.bind(this));
            var dataCenterRows = this.state.settings.dataCenters.map(function(dataCenterObject, index) {
              var dataCenter = dataCenterObject.name;
        	  return (
        		<li className="list-group-item clearfix" data-id={index} key={index} draggable="true" onDragEnd={this.dragEndDataCenters} onDragStart={this.dragStart}>
        		    <button type="button" className="btn mega-octicon octicon-three-bars pull-xs-right" data-toggle="tooltip" title="Drag to re-order" data-placement="bottom"></button>
        		    <button type="button" data-datacenter={dataCenter} className="btn btn-danger mega-octicon octicon-dash pull-xs-right" onClick={this.removeDataCenter}></button>
        		    <button type="button" className="environment-count btn btn-info pull-xs-right">{dataCenterObject.environments.length} Env</button>
                    {dataCenter}
                </li>
        	  );
        	}.bind(this));
        	var dataCenterConfigurationTabs = this.state.settings.dataCenters.map(function(dataCenterObject, index) {
        	    var dataCenter = dataCenterObject.name;
        	    return (
                    <li key={index} className="nav-item">
                      <a className={"nav-link" + (index===0 ? " active" : "")} data-toggle="tab" href={"#" + dataCenter.replace(/\s+/g, '-').toLowerCase() + "-environment-config"} role="tab">{dataCenter}</a>
                    </li>
        	    );
        	});
        	var dataCenterConfigurationContent = this.state.settings.dataCenters.map(function(dataCenterObject, index) {
        	    var dataCenter = dataCenterObject.name;
        	    var environmentRows = dataCenterObject.environments.map(function(environmentObject, index) {
        	        var environment = environmentObject.name;
                    return (
                        <li key={index} className="list-group-item clearfix" data-datacenter={dataCenter} data-id={index} draggable="true" onDragEnd={this.dragEndEnvironments} onDragStart={this.dragStart}>
                            <button type="button" className="btn mega-octicon octicon-three-bars pull-xs-right" data-toggle="tooltip" title="Drag to re-order" data-placement="bottom"></button>
                            <button type="button" data-datacenter={dataCenter} data-environment={environment} className="btn btn-danger mega-octicon octicon-dash pull-xs-right" onClick={this.removeEnvironment}></button>
                            {environment}
                        </li>
                    );
        	    }.bind(this));
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
        	}.bind(this));
            var nodeDiscoveryTabs = this.state.settings.dataCenters.map(function(dataCenterObject) {
                var dataCenter = dataCenterObject.name;
            	return (
        	  		<li key={dataCenter} className="nav-item">
        				<a className={"nav-link" + (this.isFirstDataCenter(dataCenter) ? " active" : "")} data-toggle="pill" href={"#" + dataCenter.replace(/\s+/g, '-').toLowerCase() + "-node-discovery"}>{dataCenter}</a>
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
        			<div key={dataCenter} className={"tab-pane" + (this.isFirstDataCenter(dataCenter) ? " active" : "")} id={dataCenter.replace(/\s+/g, '-').toLowerCase() + "-node-discovery"} role="tabpanel">
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
                            <li className={"nav-item" + (this.state.activeTab === "applicationSettings" ? " active" : "")}>
                              <a className="nav-link" href="#applicationSettings" data-tab="applicationSettings" onClick={this.changeSettingsNav}>Basic Configuration</a>
                            </li>
                            <li className={"nav-item" + (this.state.activeTab === "nodeDiscovery" ? " active" : "")}>
                              <a className="nav-link" href="#nodeDiscovery" data-tab="nodeDiscovery" onClick={this.changeSettingsNav}>Node Discovery</a>
                            </li>
                            <li className={"nav-item" + (this.state.activeTab === "generalSettings" ? " active" : "")}>
                              <a className="nav-link" href="#generalSettings" data-tab="generalSettings" onClick={this.changeSettingsNav}>Miscellaneous</a>
                            </li>
                          </ul>
                        </nav>
                      </div>
                      <div className="modal-body">
                      	<form>
                        <div style={(this.state.activeTab == "applicationSettings" ? {display: 'inline'} : {display: 'none'})}>
                            <div id="accordion" role="tablist" aria-multiselectable="true">
                              <div className="panel panel-default">
                                <div className="configuration-heading panel-heading" role="tab" id="headingOne">
                                  <div className="panel-title">
                                    <a className="configuration-heading-text" data-toggle="collapse" data-parent="#accordion" href="#collapseOne" aria-expanded="true" aria-controls="collapseOne">
                                      Applications
                                    </a>
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
                                <div className="configuration-heading panel-heading" role="tab" id="headingTwo">
                                  <div className="panel-title">
                                    <a className="configuration-heading-text collapsed" data-toggle="collapse" data-parent="#accordion" href="#collapseTwo" aria-expanded="false" aria-controls="collapseTwo">
                                      Data Centers
                                    </a>
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
                                <div className="configuration-heading panel-heading" role="tab" id="headingThree">
                                  <div className="panel-title">
                                    <a className="configuration-heading-text collapsed" data-toggle="collapse" data-parent="#accordion" href="#collapseThree" aria-expanded="false" aria-controls="collapseThree">
                                      Environments
                                    </a>
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
                        <div style={(this.state.activeTab == "nodeDiscovery" ? {display: 'inline'} : {display: 'none'})}>
        					<ul className="nav nav-pills">
        					  {nodeDiscoveryTabs}
        					</ul>
        					<div className="tab-content">
        					  {nodeDiscoveryPanes}
        					</div>
                        </div>
                        <div style={(this.state.activeTab == "generalSettings" ? {display: 'inline'} : {display: 'none'})}>
                          <div className="form-group row">
                            <label htmlFor="example-text-input" className="col-xs-4 col-form-label">Dashboard Title</label>
                            <div className="col-xs-8">
                              <input value={this.state.titleToAdd} className="form-control" type="text" onChange={this.handleTitleChange} />
                            </div>
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
        };
    }
}
