package media.dee.dcms.admin.services;


public interface ComponentService {
    void bindCommunicationHandler(AdminWebsocketDispatcher websocketDispatcher);

    void unbindCommunicationHandler(AdminWebsocketDispatcher websocketDispatcher);
}
