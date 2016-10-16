class FooterBar extends React.Component {

    constructor(props) {
        super(props);

        this.render = () => {
        	var timeGenerated = this.props.data ? "Time generated: " + this.props.data.timeGenerated : "";
        	return (
				<footer className="footer">
					<div className="pull-xs-right container">
						<p className="text-muted">{timeGenerated}</p>
					</div>
				</footer>
            );
        };
    }
}
