package tr.com.eno.livo.cloud.viewBean;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.ec2.AmazonEC2AsyncClient;
import com.amazonaws.services.ec2.model.AssociateAddressRequest;
import com.amazonaws.services.ec2.model.DescribeInstanceStatusRequest;
import com.amazonaws.services.ec2.model.RunInstancesRequest;

public class AWSBean {

    private static final String IMAGE_ID = "ami-b3d0fdc4";
    private static final Logger LOGGER = LoggerFactory.getLogger(AWSBean.class);
    private AmazonEC2AsyncClient client;

    @PostConstruct
    public void init() {

        LOGGER.debug("Initializing AWS client...");

        client = new AmazonEC2AsyncClient(new BasicAWSCredentials("AKIAJ3IUBXE575SV56DQ", "QCmbKPLnxVb841Z6tSvQ0b0ON+V4Nzr917d03yjE"));
        client.setRegion(Region.getRegion(Regions.EU_WEST_1));
    }

    public String createInstance() throws InterruptedException {

        LOGGER.debug("Allocating IP address...");

        String ipAddress = this.client.allocateAddress().getPublicIp();

        LOGGER.debug("Creating instance run request...");

        RunInstancesRequest request = new RunInstancesRequest();

        request.withImageId(IMAGE_ID)
                .withInstanceType("t2.micro")
                .withMinCount(1)
                .withMaxCount(1)
                .withSecurityGroups("livo");

        LOGGER.debug("Running instance...");

        String instanceId = this.client.runInstances(request).getReservation().getInstances().get(0).getInstanceId();

        LOGGER.debug("Successfully created instance '{}'...", instanceId);
        
        while (this.client.describeInstanceStatus(new DescribeInstanceStatusRequest().withInstanceIds(instanceId)).getInstanceStatuses().isEmpty()) {
            
            LOGGER.debug("Waiting for instance to run...");
            Thread.currentThread().sleep(5000L);
        }

        LOGGER.debug("Final state of instance '{}' is '{}'.", instanceId, this.client.describeInstanceStatus(new DescribeInstanceStatusRequest().withInstanceIds(instanceId)).getInstanceStatuses().get(0).getInstanceState().getName());
        
        LOGGER.debug("Associating IP address '{}' with instance '{}'...", ipAddress, instanceId);
        
        this.client.associateAddress(new AssociateAddressRequest(instanceId, ipAddress));

        return ipAddress;
    }
}
