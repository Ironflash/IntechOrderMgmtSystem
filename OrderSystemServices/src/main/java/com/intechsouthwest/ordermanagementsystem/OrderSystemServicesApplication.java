package com.intechsouthwest.ordermanagementsystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(exclude = {
        org.activiti.spring.boot.RestApiAutoConfiguration.class,
        org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration.class,
        org.activiti.spring.boot.SecurityAutoConfiguration.class,
        org.springframework.boot.actuate.autoconfigure.ManagementWebSecurityAutoConfiguration.class})
public class OrderSystemServicesApplication {

	public static void main(String[] args) {
		SpringApplication.run(OrderSystemServicesApplication.class, args);
	}

//	// This is for testing
//    @Bean
//    InitializingBean usersAndGroupsInitializer(final IdentityService identityService,final RepositoryService repositoryService,
//                                               final RuntimeService runtimeService,
//                                               final TaskService taskService) {
//
//        return new InitializingBean() {
//            public void afterPropertiesSet() throws Exception {
//                System.out.println("Number of process definitions : "
//                        + repositoryService.createProcessDefinitionQuery().count());
//                System.out.println("Number of tasks : " + taskService.createTaskQuery().count());
////                Group group = identityService.newGroup("user");
////                group.setName("puchasing");
////                group.setType("security-role");
////                identityService.saveGroup(group);
////
////                User user1 = identityService.newUser("purchaser1");
////                user1.setPassword("password");
////                identityService.saveUser(user1);
////
////                User user2 = identityService.newUser("purchaser2");
////                user2.setPassword("password");
////                identityService.saveUser(user2);
////
////                User user3 = identityService.newUser("purchaser3");
////                user3.setPassword("password");
////                identityService.saveUser(user3);
////
////                identityService.createMembership("purchaser1", "purchasing");
////                identityService.createMembership("purchaser2", "purchasing");
////                identityService.createMembership("purchaser3", "purchasing");
//
////
////                User admin = identityService.newUser("admin");
////                admin.setPassword("admin");
////                identityService.saveUser(admin);
//
////                Group adminGroup = identityService.newGroup("admin");
////                adminGroup.setName("admin");
////                adminGroup.setType("security-role");
////                identityService.saveGroup(adminGroup);
////                identityService.createMembership("admin","admin");
//            }
//        };
//    }
}
