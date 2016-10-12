class ApplicationEnvironmentStatus extends React.Component {

    constructor(props) {
        super(props);

        this.render = () => {
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
        };
    }
}