class ModalStatus extends React.Component {

    constructor(props) {
        super(props);

        this.render = () => {
            var dataCenters = this.props.data.dataCenters.map((dataCenterObject, index) => {
                var applications = dataCenterObject.applications.map((applicationObject, index) => {
                    var environments = $.map(applicationObject.environmentStatusMap, (environmentObject, environmentName) => {
                        var modalId = (dataCenterObject.name + applicationObject.name + environmentName).replace(/\s+/g, '-').toLowerCase();
                        return (
                            <div className="modal fade" id={modalId} tabIndex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
                                <div className="modal-dialog" role="document">
                                    <div className="modal-content">
                                      <div className="modal-header">
                                        <button type="button" className="close" data-dismiss="modal" aria-label="Close">
                                          <span aria-hidden="true">&times;</span>
                                        </button>
                                        <h4 className="modal-title" id="myModalLabel">Status Summary</h4>
                                      </div>
                                      <div className="modal-body">
                                        {modalId}
                                      </div>
                                      <div className="modal-footer">
                                        <button type="button" className="btn btn-secondary" data-dismiss="modal">Close</button>
                                      </div>
                                    </div>
                                </div>
                            </div>
                        );
                    });
                    return <div>{environments}</div>;
                });
                return <div>{applications}</div>;
            });
            return (
                <div>{dataCenters}</div>
            );
        };
    }
}
