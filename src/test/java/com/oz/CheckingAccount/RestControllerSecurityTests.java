package com.oz.CheckingAccount;


import com.oz.CheckingAccount.Controllers.AccountController;
import com.oz.CheckingAccount.Security.SecurityConfig;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;

@WebMvcTest(AccountController.class)
@ContextConfiguration(classes = {CheckingAccountApplication.class, SecurityConfig.class})
public class RestControllerSecurityTests {

}
