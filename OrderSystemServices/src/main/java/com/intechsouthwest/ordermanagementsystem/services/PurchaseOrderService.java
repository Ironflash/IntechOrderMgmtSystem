package com.intechsouthwest.ordermanagementsystem.services;

import io.swagger.annotations.*;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.engine.IdentityService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.image.impl.DefaultProcessDiagramGenerator;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.intechsouthwest.ordermanagementsystem.dao.CustomerDao;
import com.intechsouthwest.ordermanagementsystem.dao.ItemDao;
import com.intechsouthwest.ordermanagementsystem.domain.Customer;
import com.intechsouthwest.ordermanagementsystem.domain.Item;
import com.intechsouthwest.ordermanagementsystem.domain.PurchaseOrder;
import com.intechsouthwest.ordermanagementsystem.dao.PurchaseOrderDao;
import com.intechsouthwest.ordermanagementsystem.helpers.HelperException;
import com.intechsouthwest.ordermanagementsystem.helpers.StateValidator;
import com.intechsouthwest.ordermanagementsystem.helpers.WorkflowHelper;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.intechsouthwest.ordermanagementsystem.helpers.WorkflowHelper.CommentType.*;
import static com.intechsouthwest.ordermanagementsystem.helpers.WorkflowHelper.ProcessType.PURCHASE_ORDER_SUBMISSION;
import static com.intechsouthwest.ordermanagementsystem.helpers.WorkflowHelper.TaskAction.*;
import static com.intechsouthwest.ordermanagementsystem.helpers.WorkflowHelper.TaskType.*;

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
    private TaskService taskService;
    @Autowired
    private IdentityService identityService;
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private PurchaseOrderDao purchaseOrderDao;
    @Autowired
    private CustomerDao customerDao;
    @Autowired
    private ItemDao itemDao;
    @Autowired
    private StateValidator stateValidator;
    @Autowired
    private WorkflowHelper workflowHelper;

    @POST
    @Path("/v1/create")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @ApiOperation(value = "Creates a new purchase order for a customer", response = PurchaseOrder.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Purchase order created"),
            @ApiResponse(code = 412, message = "Pre-condition not met")
    })
    public Response createPurchasingOrder( @QueryParam("customerOrderID")
                                               @ApiParam(required = true) String customerOrderID,
                                           @QueryParam("customer")
                                                @ApiParam(required = true)  String customerName,
                                           @FormDataParam("file") InputStream fileInputStream,
                                           @FormDataParam("file") FormDataContentDisposition contentDispositionHeader){

        stateValidator.customerExists(customerName);

        if(customerOrderID == null || customerOrderID.isEmpty())
            throw new HelperException("Customer Order ID is null or empty");

        // save purchase order file
        String filePath = SERVER_UPLOAD_LOCATION_FOLDER + contentDispositionHeader.getFileName();
        String fileName = null;
        try {
            fileName = getFileName(customerName,filePath,SERVER_UPLOAD_LOCATION_FOLDER);
            // save the file to the server
            saveFile(fileInputStream, fileName);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(Marker.ANY_MARKER, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error saving file, please contact support").build();
        }
        logger.debug("File saved to server location : " + filePath);


        //TODO what happens when customer order already exists for a given customerID and customerOrderID
        // create purchase order
        PurchaseOrder po = new PurchaseOrder(fileName, customerOrderID);
        purchaseOrderDao.save(po);

        // add purchase order to customers list
        Customer customer = customerDao.findByName(customerName);
        customer.addPurchaseOrder(po);
        customerDao.save(customer);

        // if the customer account is frozen then create workflow for sales
        if(customer.getFrozen()){
            //TODO
        }else {
            // create task for purchasing
            Map<String, Object> variables = new HashMap<String, Object>();
            variables.put("purchaseOrder", po.getOrderID());
            variables.put("customer", customerName);
            //TODO is this the user we want as initiator?
            identityService.setAuthenticatedUserId("admin");
            ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("purchaseOrderSubmission", variables);

            po.setWfInstanceID(processInstance.getId());
            purchaseOrderDao.save(po);
        }

        return Response.ok(po,MediaType.APPLICATION_JSON_TYPE).build();
    }

    @PUT
    @Path("/v1/{orderID}")
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Update a purchase order", response = PurchaseOrder.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Purchase order updated"),
            @ApiResponse(code = 412, message = "Pre-condition not met")
    })
    public Response updatePurchasingOrder( @ApiParam("Order to update") @PathParam("orderID") String orderID,PurchaseOrder purchaseOrder){

        stateValidator.purchaseOrderExists(orderID);


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

    @GET
    @Path("v1/{orderID}/download")
    @Produces({"application/pdf"})
    @ApiOperation(value = "Download pdf for purchase order")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Purchase order downloaded"),
            @ApiResponse(code = 412, message = "Pre-condition not met")
    })
    public Response getPDF(@ApiParam("Order to download pdf") @PathParam("orderID") String orderID) throws Exception {

        stateValidator.purchaseOrderExists(orderID);

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

    @POST
    @Path("v1/{orderID}/reject")
    @Consumes("application/text")
    @ApiOperation(value = "Reject the current purchase order")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Purchase order marked as rejected"),
            @ApiResponse(code = 412, message = "Pre-condition not met")
    })
    public Response rejectPurchaseOrder(@ApiParam("Order to reject") @PathParam("orderID") String orderID, String comment) throws Exception {
        return commentAndComplete(orderID, comment, PURCHASING_REVIEW, REJECT_PO, PURCHASING, "admin");
    }

    @POST
    @Path("v1/{orderID}/itemsOrdered")
    @Consumes("application/text")
    @ApiOperation(value = "Mark items as ordered")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Item Workflows created"),
            @ApiResponse(code = 412, message = "Pre-condition not met")
    })
    public Response itemsOrdered(@ApiParam("Order to with items") @PathParam("orderID") String orderID) throws Exception {
        stateValidator.purchaseOrderExists(orderID);

        PurchaseOrder po = purchaseOrderDao.findByOrderID(orderID);

        workflowHelper.setUser("admin")
                        .setInstanceID(po.getWfInstanceID())
                        .setTask(PURCHASING_REVIEW)
                        .checkTaskIsClaimedByUser()
                        .completeWithAction(ORDER_ITEMS);

        return Response.ok().build();
    }

    @POST
    @Path("v1/{orderID}/renegotiate")
    @Consumes("application/text")
    @ApiOperation(value = "Offer a renegotiated purchase order")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Renegotiation submitted"),
            @ApiResponse(code = 412, message = "Pre-condition not met")
    })
    public Response renegotiatePurchaseOrder(@ApiParam("Order to renegotiate") @PathParam("orderID") String orderID, String comment) throws Exception {

        return commentAndComplete(orderID, comment, RENEGOTIATE_QUOTE, OFFER_CHANGE, SALES, "admin");
    }

    private Response commentAndComplete(String orderID, String comment, WorkflowHelper.TaskType taskType,
                                           WorkflowHelper.TaskAction taskAction, WorkflowHelper.CommentType commentType, String user){
        stateValidator.purchaseOrderExists(orderID)
                        .purchaseOrderHasWF(orderID);

        PurchaseOrder po = purchaseOrderDao.findByOrderID(orderID);

        workflowHelper.setUser(user)
                        .setInstanceID(po.getWfInstanceID())
                        .setTask(taskType)
                        .checkTaskIsClaimedByUser()
                        .addComment(comment, commentType)
                        .completeWithAction(taskAction);

        return Response.ok().build();
    }

    @GET
    @Path("v1/{orderID}/processDiagram")
    @Produces({"application/text"})
    @ApiOperation(value = "Download process diagram for purchase order")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Purchase order diagram downloaded"),
            @ApiResponse(code = 412, message = "Pre-condition not met")
    })
    public Response getProcessDiagram(@ApiParam("Order to get process diagram for") @PathParam("orderID") String orderID) throws Exception {

        stateValidator.purchaseOrderExists(orderID)
                        .purchaseOrderHasWF(orderID);

        PurchaseOrder po = purchaseOrderDao.findByOrderID(orderID);

        ProcessDefinitionEntity pde = (ProcessDefinitionEntity) repositoryService.createProcessDefinitionQuery()
                                                                                    .processDefinitionKey(PURCHASE_ORDER_SUBMISSION.toString())
                                                                                    .orderByDeploymentId()
                                                                                    .singleResult();

        BpmnModel bpmnModel = repositoryService.getBpmnModel(pde.getId());
        InputStream resource = new DefaultProcessDiagramGenerator().generateDiagram(bpmnModel, "png", runtimeService.getActiveActivityIds(po.getWfInstanceID()));
        return Response.ok(new StreamingOutput() {
            public void write(OutputStream output) throws IOException, WebApplicationException {
                try {
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = resource.read(buffer)) != -1)
                    {
                        output.write(buffer, 0, bytesRead);
                    }
                } catch (Exception e) {
                    throw new WebApplicationException(e);
                }
            }
        }).build();
    }

    @GET
    @Path("v1/{orderID}/items")
    @Consumes("application/json")
    @ApiOperation(value = "Get items for an order")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Items returned"),
            @ApiResponse(code = 412, message = "Pre-condition not met")
    })
    public Response getItems(@ApiParam("Order to get items for") @PathParam("orderID") String orderID) throws Exception {

        stateValidator.purchaseOrderExists(orderID);

        PurchaseOrder po = purchaseOrderDao.findByOrderID(orderID);

        return Response.ok().entity(po.getItems()).build();
    }

    @POST
    @Path("v1/{orderID}/items")
    @Consumes("application/json")
    @ApiOperation(value = "Update/Insert items")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Items upserted"),
            @ApiResponse(code = 412, message = "Pre-condition not met")
    })
    public Response upsertItems(@ApiParam("Order to upsert items on") @PathParam("orderID") String orderID, Set<Item> items) throws Exception {

        stateValidator.purchaseOrderExists(orderID)
                        .purchaseOrderHasWF(orderID);

        PurchaseOrder po = purchaseOrderDao.findByOrderID(orderID);

        workflowHelper.setUser("admin")
                        .setInstanceID(po.getWfInstanceID())
                        .setTask(PURCHASING_REVIEW)
                        .checkTaskIsClaimedByUser();

        itemDao.save(items);

        po.setItems(items);
        purchaseOrderDao.save(po);

            // generate a task for each item
            //TODO

        return Response.ok("Items upserted").build();
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

    @GET
    @Path("v1/{processID}/activeTasks")
    @Consumes("application/json")
    @ApiOperation(value = "Get active tasks for a process")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Active tasks"),
            @ApiResponse(code = 412, message = "Pre-condition not met")
    })
    public Response getActiveTasks(@ApiParam("Process to get active tasks for") @PathParam("processID") String processID){
        return Response.ok(workflowHelper.setInstanceID(processID).getActiveTasks()).build();
    }

    @POST
    @Path("v1/{taskID}")
    @Consumes("application/json")
    @ApiOperation(value = "Claim a task")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Task Claimed"),
            @ApiResponse(code = 412, message = "Pre-condition not met")
    })
    public Response claimTask(@ApiParam("Task to claim") @PathParam("taskID") String taskID) throws Exception {

        workflowHelper.setUser("admin")
                        .claimTask(taskID);

        return Response.ok("Task Claimed").build();
    }
}
