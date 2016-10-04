var staticData = {
  title: "Dynamic Dashboard"
}
var data = {
  dataCenters: {
    "AWS" : {
      primary: true,
      environments: ["d1euw1", "ueuw1", "u3euw1", "s1euw1", "peuw1"],
      applications: {
        "DCM" : {
          environments: {
            "d1euw1": {
              nodesUp: 2,
              nodesDown: 2
            },
            "ueuw1": {
              nodesUp: 2,
              nodesDown: 0
            },
            "u3euw1": {
              nodesUp: 20,
              nodesDown: 0
            },
            "s1euw1": {
              nodesUp: 15,
              nodesDown: 5
            },
            "peuw1": {
              nodesUp: 20,
              nodesDown: 0
            }
          }
        },
        "CRM" : {
          environments: {
            "d1euw1": {
              nodesUp: 2,
              nodesDown: 0
            },
            "ueuw1": {
              nodesUp: 2,
              nodesDown: 0
            },
            "u3euw1": {
              nodesUp: 10,
              nodesDown: 0
            },
            "s1euw1": {
              nodesUp: 10,
              nodesDown: 0
            },
            "peuw1": {
              nodesUp: 10,
              nodesDown: 0
            }
          }
        },
        "HKM" : {
          environments: {
            "d1euw1": {
              nodesUp: 2,
              nodesDown: 0
            },
            "ueuw1": {
              nodesUp: 2,
              nodesDown: 0
            },
            "u3euw1": {
              nodesUp: 2,
              nodesDown: 0
            },
            "s1euw1": {
              nodesUp: 2,
              nodesDown: 0
            },
            "peuw1": {
              nodesUp: 2,
              nodesDown: 0
            }
          }
        }
      }
    },
    "M25 Hemel": {
      primary: false,
      environments: []
    },
    "M25 Slough": {
      primary: false,
      environments: []
    }
  }
};

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

ReactDOM.render(<TitleBar title={staticData.title} />, document.getElementById('title-bar'));
ReactDOM.render(<DataCenterTabs data={data} />, document.getElementById('data-center-tabs'));
ReactDOM.render(<DataCenterDashboards data={data} />, document.getElementById('data-center-tab-content'));
