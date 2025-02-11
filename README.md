# SpringBoot-CheckingAccountSecurity
builds off of SpringBoot-CheckingAccountControllerTest

* adds security filters for post, put and get requests on account endpoints
* basic http authentication/authorization

security config can be found [here](src/main/java/com/oz/CheckingAccount/Security/SecurityConfig.java)\
end to end tests can be found [here](src/test/java/com/oz/CheckingAccount/CheckingAccountRestControllerTests.java)