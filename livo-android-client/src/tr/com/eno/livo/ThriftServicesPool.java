package tr.com.eno.livo;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TMultiplexedProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFastFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import android.util.Log;
import tr.com.eno.livo.thrift.analytics.AnalyticsService;
import tr.com.eno.livo.thrift.authc.AuthenticationService;
import tr.com.eno.livo.thrift.file.FileTransferService;
import tr.com.eno.livo.thrift.notification.NotificationRegistrationService;
import tr.com.eno.livo.thrift.provision.ProvisioningService;
import tr.com.eno.livo.thrift.serviceobjects.ServiceObjectProxyService;

public class ThriftServicesPool {

    private static final String AUTHENTICATIONSERVICE = "AuthenticationService";
    private static final String FILETRANSFERSERVICE = "FileTransferService";
    private static final String PROVISIONINGSERVICE = "ProvisioningService";
    private static final String SERVOBJPROXYSERVICE = "ServiceObjectProxyService";
    private static final String ANALYTICSSERVICE = "AnalyticsService";
    private static final String NOTIFICATIONREGISTRATIONSERVICE = "NotificationRegistrationService";
    private TProtocol protocol;
    private TTransport transport;

    /**
     * @param serverAddress External ip address or uniform resource locator.
     * @param serverPort    The port address default is 2366 for servers if not configured.
     */
    public ThriftServicesPool(String serverAddress, int serverPort) {
        this.transport = new TFastFramedTransport(new TSocket(serverAddress, serverPort));

        this.protocol = new TBinaryProtocol(this.transport);
    }

    public ThriftServicesPool(String serverAddress, int serverPort, int timeout) {
        this.transport = new TFastFramedTransport(new TSocket(serverAddress, serverPort, timeout));
        this.protocol = new TBinaryProtocol(this.transport);
    }

    public ThriftServicesPool(TTransport paramTTransport) {
        this.transport = paramTTransport;

        this.protocol = new TBinaryProtocol(this.transport);
    }

    public void closeTransport() {
        if (this.isOpen()) {
            this.transport.close();
        }
    }

    public AuthenticationService.Client getAuthService() throws TException {
        if (this.isOpen())
            return new AuthenticationService.Client(new TMultiplexedProtocol(this.protocol, ThriftServicesPool.AUTHENTICATIONSERVICE));

        throw new TException("ThriftServicesPool->getAuthService transport is not open.");
    }

    public ServiceObjectProxyService.Client getServObjProxyService() throws TException {

        if (this.isOpen())
            return new ServiceObjectProxyService.Client(new TMultiplexedProtocol(this.protocol, ThriftServicesPool.SERVOBJPROXYSERVICE));

        throw new TException("ThriftServicesPool-> getServObj transport is not open.");
    }

    public AnalyticsService.Client getAnalyticsService() throws TException {

        if (this.isOpen())
            return new AnalyticsService.Client(new TMultiplexedProtocol(this.protocol, ThriftServicesPool.ANALYTICSSERVICE));

        throw new TException(
                "ThriftServicesPool-> getAnalyticsService transport is not open.");
    }

    public FileTransferService.Client getFileTransferService() throws TException {

        if (this.isOpen())
            return new FileTransferService.Client(new TMultiplexedProtocol(this.protocol, ThriftServicesPool.FILETRANSFERSERVICE));

        throw new TException("ThriftServicesPool->getFileTransferService transport is not open");
    }

    public ProvisioningService.Client getProvisionService() throws TException {

        if (this.isOpen())
            return new ProvisioningService.Client(new TMultiplexedProtocol(this.protocol, ThriftServicesPool.PROVISIONINGSERVICE));

        throw new TException("ThriftServicesPool->getProvisionService transport is not open");
    }


    public NotificationRegistrationService.Client getNotificationRegistrationService() throws TException {

        if (this.isOpen())
            return new NotificationRegistrationService.Client(new TMultiplexedProtocol(this.protocol, ThriftServicesPool.NOTIFICATIONREGISTRATIONSERVICE));

        throw new TException("ThriftServicesPool->getNotificationRegistrationService transport is not open");
    }

    public boolean isOpen() {
        return this.transport.isOpen();
    }

    public void openTransport()
            throws TException {
        if (!this.isOpen()) {
            try {
                Log.d("ThriftServicesPool", "Trying to open transport.");
                this.transport.open();
                Log.d("ThriftServicesPool", "Transport is open.");
            } catch (TTransportException localTTransportException) {
                this.closeTransport();
                throw new TException("Server is down.", localTTransportException);
            }
        }
    }
}
