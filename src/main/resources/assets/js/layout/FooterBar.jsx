class FooterBar extends React.Component {

    constructor(props) {
        super(props);

        this.render = () => (
            <footer className="footer">
                <div className="pull-xs-right container">
                    <p className="text-muted">{"Time generated: " + this.props.data.timeGenerated}</p>
                </div>
            </footer>
        );
    }
}
