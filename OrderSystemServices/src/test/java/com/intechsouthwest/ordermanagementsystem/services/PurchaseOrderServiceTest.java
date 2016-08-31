package com.intechsouthwest.ordermanagementsystem.services;

import org.activiti.engine.IdentityService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.activiti.engine.test.ActivitiRule;
import org.activiti.engine.test.Deployment;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.*;
import com.intechsouthwest.ordermanagementsystem.dao.CustomerDao;
import com.intechsouthwest.ordermanagementsystem.dao.PurchaseOrderDao;
import com.intechsouthwest.ordermanagementsystem.domain.Customer;
import com.intechsouthwest.ordermanagementsystem.domain.PurchaseOrder;
import com.intechsouthwest.ordermanagementsystem.helpers.StateValidator;
import com.intechsouthwest.ordermanagementsystem.helpers.WorkflowHelper;

import javax.ws.rs.core.Response;
import java.io.StringBufferInputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Mockito.*;

/**
 * Created by gregorylaflash on 8/8/16.
 */
public class PurchaseOrderServiceTest {

    @InjectMocks
    private PurchaseOrderService purchaseOrderService;


    @Rule
    public ActivitiRule activitiRule = new ActivitiRule();
    @Mock
    private RuntimeService runtimeService;
    @Mock
    private TaskService taskService;
    @Mock
    private IdentityService identityService;
    @Mock
    private PurchaseOrderDao purchaseOrderDao;
    @Mock
    private CustomerDao customerDao;
    @Mock
    private StateValidator stateValidator;
    @Mock
    private WorkflowHelper workflowHelper;

    @Before
    public void initMocks(){
        MockitoAnnotations.initMocks(this);
        activitiRule.setRuntimeService(runtimeService);
        activitiRule.setIdentityService(identityService);
        activitiRule.setTaskService(taskService);
    }

    @Test
    public void createPurchasingOrderWhenCustomerDoesntExist() throws Exception {

        when(customerDao.findByName("nonexistent")).thenReturn(null);
        when(customerDao.exists("nonexistent")).thenReturn(false);
        Response response = purchaseOrderService.createPurchasingOrder("testCustomerOrder","nonexistent",
                new StringBufferInputStream(""), null);

        assertEquals(response.getStatus(), Response.Status.NOT_FOUND.getStatusCode());
    }

    @Test
    public void createPurchasingOrderWhenCustomerOrderIdNull() throws Exception {

        Response response = purchaseOrderService.createPurchasingOrder(null,"testCustomer",
                new StringBufferInputStream(""), null);

        assertEquals(response.getStatus(), Response.Status.NOT_FOUND.getStatusCode());
    }

    @Test
    public void createPurchasingOrderWhenCustomerNameNull() throws Exception {

        Response response = purchaseOrderService.createPurchasingOrder("testCustomerOrder",null,
                new StringBufferInputStream(""), null);

        assertEquals(response.getStatus(), Response.Status.NOT_FOUND.getStatusCode());
    }

    @Test
    @Deployment(resources = {"processes/purchaseOrderSubmission.bpmn"})
    public void createPurchasingOrderCreatesWorkflowWithCorrectVariables() throws Exception {

        Customer mockCustomer = mock(Customer.class);
        when(customerDao.findByName("testCustomer")).thenReturn(mockCustomer);
        when(customerDao.exists("testCustomer")).thenReturn(true);

        FormDataContentDisposition mockFormData = mock(FormDataContentDisposition.class);
        when(mockFormData.getFileName()).thenReturn("testFile.pdf");

        ArgumentCaptor<PurchaseOrder> purchaseOrderCaptor = ArgumentCaptor.forClass(PurchaseOrder.class);


        when(runtimeService.startProcessInstanceByKey(any(),anyMap())).thenReturn(mock(ProcessInstance.class));

        Response response = purchaseOrderService.createPurchasingOrder("testCustomerOrder","testCustomer",
                new StringBufferInputStream(""), mockFormData);

        verify(purchaseOrderDao, times(2)).save(purchaseOrderCaptor.capture());

        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put("purchaseOrder", purchaseOrderCaptor.getValue().getOrderID());
        variables.put("customer", "testCustomer");
        verify(runtimeService).startProcessInstanceByKey("purchaseOrderSubmission",variables);
    }

    @Test
    public void updatePurchasingOrderMustHaveExistingPO() throws Exception {

        when(purchaseOrderDao.exists(anyString())).thenReturn(false);

        Response response = purchaseOrderService.updatePurchasingOrder("testOrderID",mock(PurchaseOrder.class));

        assertEquals(response.getStatus(), Response.Status.PRECONDITION_FAILED.getStatusCode());
    }

    @Test
    public void updatePurchasingOrderCallsSave() throws Exception {

        when(purchaseOrderDao.exists(anyString())).thenReturn(true);

        PurchaseOrder mockPuchaseOrder = mock(PurchaseOrder.class);
        Response response = purchaseOrderService.updatePurchasingOrder("testOrderID",mockPuchaseOrder);

        verify(purchaseOrderDao).save(mockPuchaseOrder);
    }

    @Test
    public void getPDFMustHaveExistingPO() throws Exception {

        when(purchaseOrderDao.exists(anyString())).thenReturn(false);

        Response response = purchaseOrderService.getPDF("testOrderID");

        assertEquals(response.getStatus(), Response.Status.PRECONDITION_FAILED.getStatusCode());
    }

    @Test
    public void rejectPurchaseOrderMustHaveExistingPO() throws Exception {

        when(purchaseOrderDao.exists(anyString())).thenReturn(false);

        Response response = purchaseOrderService.rejectPurchaseOrder("testOrderID","");

        assertEquals(response.getStatus(), Response.Status.PRECONDITION_FAILED.getStatusCode());
    }

    @Test
    public void rejectPurchaseOrderMustHaveWorkflowProcessID() throws Exception {

        PurchaseOrder mockPurchaseOrder = mock(PurchaseOrder.class);
        when(mockPurchaseOrder.getWfInstanceID()).thenReturn(null);
        when(purchaseOrderDao.exists(anyString())).thenReturn(true);
        when(purchaseOrderDao.findByOrderID(anyString())).thenReturn(mockPurchaseOrder);

        Response response = purchaseOrderService.rejectPurchaseOrder("testOrderID", "");

        assertEquals(response.getStatus(), Response.Status.PRECONDITION_FAILED.getStatusCode());
    }

    @Test
    public void rejectPurchaseOrderMustHaveAPurchaseReviewTask() throws Exception {

        PurchaseOrder mockPurchaseOrder = mock(PurchaseOrder.class);
        when(mockPurchaseOrder.getWfInstanceID()).thenReturn("123");
        when(purchaseOrderDao.exists(anyString())).thenReturn(true);
        when(purchaseOrderDao.findByOrderID(anyString())).thenReturn(mockPurchaseOrder);

        TaskQuery mockTaskQuery = mock(TaskQuery.class);
        when(mockTaskQuery.processInstanceId(any())).thenReturn(mockTaskQuery);
        when(mockTaskQuery.active()).thenReturn(mockTaskQuery);
        when(mockTaskQuery.singleResult()).thenReturn(null);

        when(taskService.createTaskQuery()).thenReturn(mockTaskQuery);
        Response response = purchaseOrderService.rejectPurchaseOrder("testOrderID","");

        assertEquals(response.getStatus(), Response.Status.PRECONDITION_FAILED.getStatusCode());

        Task mockTask = mock(Task.class);
        when(mockTask.getName()).thenReturn("notValid");
        when(mockTaskQuery.singleResult()).thenReturn(mockTask);

        response = purchaseOrderService.rejectPurchaseOrder("testOrderID", "");

        assertEquals(response.getStatus(), Response.Status.PRECONDITION_FAILED.getStatusCode());
    }

    @Test
    public void renegotiatePurchaseOrderMustHaveExistingPO() throws Exception {

        when(purchaseOrderDao.exists(anyString())).thenReturn(false);

        Response response = purchaseOrderService.renegotiatePurchaseOrder("testOrderID","");

        assertEquals(response.getStatus(), Response.Status.PRECONDITION_FAILED.getStatusCode());
    }

    @Test
    public void renegotiatePurchaseOrderMustHaveWorkflowProcessID() throws Exception {

        PurchaseOrder mockPurchaseOrder = mock(PurchaseOrder.class);
        when(mockPurchaseOrder.getWfInstanceID()).thenReturn(null);
        when(purchaseOrderDao.exists(anyString())).thenReturn(true);
        when(purchaseOrderDao.findByOrderID(anyString())).thenReturn(mockPurchaseOrder);

        Response response = purchaseOrderService.renegotiatePurchaseOrder("testOrderID", "");

        assertEquals(response.getStatus(), Response.Status.PRECONDITION_FAILED.getStatusCode());
    }

    @Test
    public void renegotiatePurchaseOrderMustHaveARenegotiatePurchaseOrderTask() throws Exception {

        PurchaseOrder mockPurchaseOrder = mock(PurchaseOrder.class);
        when(mockPurchaseOrder.getWfInstanceID()).thenReturn("123");
        when(purchaseOrderDao.exists(anyString())).thenReturn(true);
        when(purchaseOrderDao.findByOrderID(anyString())).thenReturn(mockPurchaseOrder);

        TaskQuery mockTaskQuery = mock(TaskQuery.class);
        when(mockTaskQuery.processInstanceId(any())).thenReturn(mockTaskQuery);
        when(mockTaskQuery.active()).thenReturn(mockTaskQuery);
        when(mockTaskQuery.singleResult()).thenReturn(null);

        when(taskService.createTaskQuery()).thenReturn(mockTaskQuery);
        Response response = purchaseOrderService.renegotiatePurchaseOrder("testOrderID","");

        assertEquals(response.getStatus(), Response.Status.PRECONDITION_FAILED.getStatusCode());

        Task mockTask = mock(Task.class);
        when(mockTask.getName()).thenReturn("notValid");
        when(mockTaskQuery.singleResult()).thenReturn(mockTask);

        response = purchaseOrderService.renegotiatePurchaseOrder("testOrderID", "");

        assertEquals(response.getStatus(), Response.Status.PRECONDITION_FAILED.getStatusCode());
    }

    @Test
    public void getItemsMustHaveExistingPO() throws Exception {

        when(purchaseOrderDao.exists(anyString())).thenReturn(false);

        Response response = purchaseOrderService.getItems("testOrderID");

        assertEquals(response.getStatus(), Response.Status.PRECONDITION_FAILED.getStatusCode());
    }

    @Test
    public void upsertItemsMustHaveExistingPO() throws Exception {

        when(purchaseOrderDao.exists(anyString())).thenReturn(false);

        Response response = purchaseOrderService.upsertItems("testOrderID", new HashSet<>());

        assertEquals(response.getStatus(), Response.Status.PRECONDITION_FAILED.getStatusCode());
    }

    @Test
    public void upsertItemsMustHaveWorkflowProcessID() throws Exception {

        PurchaseOrder mockPurchaseOrder = mock(PurchaseOrder.class);
        when(mockPurchaseOrder.getWfInstanceID()).thenReturn(null);
        when(purchaseOrderDao.exists(anyString())).thenReturn(true);
        when(purchaseOrderDao.findByOrderID(anyString())).thenReturn(mockPurchaseOrder);

        Response response = purchaseOrderService.upsertItems("testOrderID", new HashSet<>());

        assertEquals(response.getStatus(), Response.Status.PRECONDITION_FAILED.getStatusCode());
    }

    @Test
    public void upsertItemsMustHaveAPurchaseReviewTask() throws Exception {

        PurchaseOrder mockPurchaseOrder = mock(PurchaseOrder.class);
        when(mockPurchaseOrder.getWfInstanceID()).thenReturn("123");
        when(purchaseOrderDao.exists(anyString())).thenReturn(true);
        when(purchaseOrderDao.findByOrderID(anyString())).thenReturn(mockPurchaseOrder);

        TaskQuery mockTaskQuery = mock(TaskQuery.class);
        when(mockTaskQuery.processInstanceId(any())).thenReturn(mockTaskQuery);
        when(mockTaskQuery.active()).thenReturn(mockTaskQuery);
        when(mockTaskQuery.singleResult()).thenReturn(null);

        when(taskService.createTaskQuery()).thenReturn(mockTaskQuery);
        Response response = purchaseOrderService.upsertItems("testOrderID",new HashSet<>());

        assertEquals(response.getStatus(), Response.Status.PRECONDITION_FAILED.getStatusCode());

        Task mockTask = mock(Task.class);
        when(mockTask.getName()).thenReturn("notValid");
        when(mockTaskQuery.singleResult()).thenReturn(mockTask);

        response = purchaseOrderService.upsertItems("testOrderID", new HashSet<>());

        assertEquals(response.getStatus(), Response.Status.PRECONDITION_FAILED.getStatusCode());
    }

    @Test
    public void upsertItemsMustNotDeleteExistingItems() throws Exception {

    }

    @Test
    public void upsertItemsMustUpdateExistingItems() throws Exception {

    }

    @Test
    public void upsertItemsMustInsertNewItems() throws Exception {

    }

}