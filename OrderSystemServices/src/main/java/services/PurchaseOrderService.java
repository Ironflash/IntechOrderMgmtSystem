package services;

import io.swagger.annotations.*;
import org.activiti.engine.RuntimeService;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import services.dao.CustomerDao;
import services.domain.Customer;
import services.domain.PurchaseOrder;
import services.dao.PurchaseOrderDao;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by gregorylaflash on 7/31/16.
 */
@Component
@Path("/purchaseOrder")
@Api(value = "PurchaseOrder", produces = "application/json")
public class PurchaseOrderService {


    private final String SERVER_UPLOAD_LOCATION_FOLDER = "fileUploads";

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private PurchaseOrderDao purchaseOrderDao;
    @Autowired
    private CustomerDao customerDao;

    @POST
    @Path("/v1/create")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @ApiOperation(value = "Creates a new purchase order for a customer", response = PurchaseOrder.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Purchase order created"),
            @ApiResponse(code = 404, message = "Customer not found")
    })
    public Response createPurchasingOrder( @QueryParam("customerOrderID")
                                               @ApiParam(required = true) String customerOrderID,
                                           @QueryParam("customer")
                                                @ApiParam(required = true)  String customerName,
                                           @FormDataParam("file") InputStream fileInputStream,
                                           @FormDataParam("file") FormDataContentDisposition contentDispositionHeader){

        //check that customer exists
        if(!customerDao.exists(customerName)){
            return Response.status(Response.Status.NOT_FOUND).entity("Customer not found").build();
        }

        // save purchase order file
        String filePath = SERVER_UPLOAD_LOCATION_FOLDER + contentDispositionHeader.getFileName();
        String fileName = null;
        try {
            fileName = getFileName(customerName,filePath,SERVER_UPLOAD_LOCATION_FOLDER);
            // save the file to the server
            saveFile(fileInputStream, fileName);
        } catch (Exception e) {
            e.printStackTrace();//TODO
        }
        logger.debug("File saved to server location : " + filePath);

        // create purchase order
        PurchaseOrder po = new PurchaseOrder(fileName, customerOrderID);
        purchaseOrderDao.save(po);

        // add purchase order to customers list
        Customer customer = customerDao.findByName(customerName);
        customer.addPurchaseOrder(po);
        customerDao.save(customer);

        // create task for purchasing
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put("purchaseOrder", po.getOrderID());
        variables.put("customer", customerName);
        runtimeService.startProcessInstanceByKey("purchaseOrderSubmission",variables);

        // create task for Junior

        return Response.ok(po,MediaType.APPLICATION_JSON_TYPE).build();
    }

    @PUT
    @Path("/v1/{orderID}")
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Update a purchase order", response = PurchaseOrder.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Purchase order updated"),
            @ApiResponse(code = 404, message = "Order not found")
    })
    public Response updatePurchasingOrder( @ApiParam("Order to update") @PathParam("orderID") String orderID,PurchaseOrder purchaseOrder){

        if(!purchaseOrderDao.exists(orderID)){
            return Response.status(Response.Status.NOT_FOUND).entity("Order not found").build();
        }
        // this will create/merge based on if the order already exists
        purchaseOrderDao.save(purchaseOrder);
        return Response.ok(purchaseOrder,MediaType.APPLICATION_JSON_TYPE).build();
    }

    // save uploaded file to a defined location on the server
    private void saveFile(InputStream uploadedInputStream,
                          String serverLocation) {
        try {
            Pattern dirPattern = Pattern.compile("(.*)\\/[^\\/]+.pdf");
            Matcher matcher = dirPattern.matcher(serverLocation);
            if(matcher.find())
                new File(matcher.group(1)).mkdirs();
            OutputStream outpuStream = new FileOutputStream(new File(serverLocation));
            int read = 0;
            byte[] bytes = new byte[1024];

            outpuStream = new FileOutputStream(new File(serverLocation));
            while ((read = uploadedInputStream.read(bytes)) != -1) {
                outpuStream.write(bytes, 0, read);
            }
            outpuStream.flush();
            outpuStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Path("v1/{orderID}/download")
    @GET
    @Produces({"application/pdf"})
    @ApiOperation(value = "Download pdf for purchase order")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Purchase order downloaded"),
            @ApiResponse(code = 404, message = "Order not found")
    })
    public Response getPDF(@ApiParam("Order to download pdf") @PathParam("orderID") String orderID) throws Exception {

        if(!purchaseOrderDao.exists(orderID)){
            return Response.status(Response.Status.NOT_FOUND).entity("Order not found").build();
        }

        return Response.ok(new StreamingOutput() {
            public void write(OutputStream output) throws IOException, WebApplicationException {
                try {
                    File file = new File(purchaseOrderDao.findByOrderID(orderID).getScannedPDFLocation());
                    FileInputStream fis = new FileInputStream(file);
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = fis.read(buffer)) != -1)
                    {
                        output.write(buffer, 0, bytesRead);
                    }
                } catch (Exception e) {
                    throw new WebApplicationException(e);
                }
            }
        }).build();
    }


    public static String getFileName(String customer, String fileName, String location) throws Exception {
        // Create calendar and formatters needed to create PO url
        Calendar calendar = new GregorianCalendar();
        SimpleDateFormat pathFormat = new SimpleDateFormat("yyyy/MM/dd");
        SimpleDateFormat fileNameFormat = new SimpleDateFormat("HHmmss");

        // Retrieve customer name
        String customerName = customer;

        // generate the file location and name
        String customerPOLocation = String.format("%s/%s/%s/",location,
                pathFormat.format(calendar.getTime()),customerName);

        Pattern p = Pattern.compile("(\\..+$)");
        Matcher matcher = p.matcher(fileName);

        if(!matcher.find()){
//                registerMessage.reply(JsonHelper.getErrorReply("Failed to get file extension from " + fileName));
            throw new Exception("Failed to get file extension from " + fileName);
        }


        String pdfFileName = String.format("%s%s%s",customerName,fileNameFormat.format(calendar.getTime()),matcher.group(0));

        return customerPOLocation + pdfFileName;
    }


}
