class TitleBar extends React.Component {
    constructor(props) {
        super(props);

        this.render = () => {
            return (
              <h1>{this.props.title}
                <div className="pull-xs-right" data-toggle="tooltip" data-placement="left" title="Settings">
                  <button id="settings-button" type="button" className="btn btn-secondary btn-md active" data-toggle="modal" data-target="#settings-modal">
                    <img src="images/gear.png" width="35" />
                  </button>
                </div>
              </h1>
            );
        };
    }
}