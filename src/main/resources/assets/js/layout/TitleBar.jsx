class TitleBar extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            rotate: false
        };

        this.rotateClick = (event) => {
            var newRotateValue = !this.state.rotate;
            this.setState({rotate: newRotateValue});
            this.props.onRotateClick(newRotateValue);
        };

        this.render = () => {
            var rotateButtonClass = this.state.rotate ? "rotate-true " : "rotate-false ";
            return (
              <h1>{this.props.title}
                <div className="pull-xs-right" data-toggle="tooltip" data-placement="bottom" title="Settings">
                  <button id="settings-button" type="button" className="btn btn-secondary btn-md active" data-toggle="modal" data-target="#settings-modal">
                    <img src="images/gear.png" width="35" />
                  </button>
                </div>
                <div className="pull-xs-right" data-toggle="tooltip" data-placement="bottom" title="Rotate">
                  <button type="button" className={rotateButtonClass + "rotate-button btn mega-octicon octicon-sync"} onClick={this.rotateClick}></button>
                </div>
              </h1>
            );
        };
    }
}