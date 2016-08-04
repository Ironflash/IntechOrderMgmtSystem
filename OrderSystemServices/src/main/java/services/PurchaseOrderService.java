package services;

import io.swagger.annotations.*;
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
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
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
    private PurchaseOrderDao purchaseOrderDao;
    @Autowired
    private CustomerDao customerDao;

    @POST
    @Path("/v1/create")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @ApiOperation(value = "Creates a new purchase order for a customer", response = PurchaseOrder.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Purchase order created")
    })
    public Response createPurchasingOrder( @QueryParam("customerOrderID")
                                               @ApiParam(required = true) String customerOrderID,
                                           @QueryParam("customer")
                                                @ApiParam(required = true)  String customerName,
                                           @FormDataParam("file") InputStream fileInputStream,
                                           @FormDataParam("file") FormDataContentDisposition contentDispositionHeader){

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



        return Response.ok(po,MediaType.APPLICATION_JSON_TYPE).build();
    }

    @PUT
    @Path("/v1/{orderID}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updatePurchasingOrder(@PathParam("orderID") String orderID,PurchaseOrder purchaseOrder){

        // TODO check that the purchase order exists
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
