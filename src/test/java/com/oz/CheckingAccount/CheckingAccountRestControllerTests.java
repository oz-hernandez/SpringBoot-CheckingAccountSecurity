package com.oz.CheckingAccount;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.oz.CheckingAccount.Accounts.Account;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CheckingAccountRestControllerTests {

	@Autowired
	private TestRestTemplate restTemplate;

	@Test
	void createAccount() {
		Account account = new Account("mary", BigDecimal.valueOf(100.00));
		ResponseEntity<Void> response = restTemplate
				.withBasicAuth("mary", "123987")
				.postForEntity("/accounts", account, Void.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
	}

	@Test
	void getAccount() {
		Account account = new Account("oz", BigDecimal.valueOf(350.99));
		restTemplate = restTemplate.withBasicAuth("oz", "abc123");
		ResponseEntity<Void> response = restTemplate.postForEntity("/accounts", account, Void.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

		URI location = response.getHeaders().getLocation();
		ResponseEntity<String> responseAccount = restTemplate.getForEntity(location, String.class);
		assertThat(responseAccount.getStatusCode()).isEqualTo(HttpStatus.OK);

		DocumentContext doc = JsonPath.parse(responseAccount.getBody());
		Number id = doc.read("$.id");
		assertThat(id).isNotNull();

		String name = doc.read("$.name");
		assertThat(name).isEqualTo("oz");

		Double balance = doc.read("$.balance");
		assertThat(balance).isEqualTo(350.99);
	}

	@Test
	void noAccessIfNotAuthenticated() {
		ResponseEntity<Account> res = restTemplate.getForEntity("/accounts/99", Account.class);
		assertThat(res.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
	}

	@Test
	void shouldNotAccessAccountYouDontOwn() {
		Account account = new Account("oz", BigDecimal.valueOf(350.99));
		ResponseEntity<Void> response = restTemplate
				.withBasicAuth("oz", "abc123")
				.postForEntity("/accounts", account, Void.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

		URI location = response.getHeaders().getLocation();
		ResponseEntity<String> responseAccount = restTemplate
				.withBasicAuth("mary", "123987")
				.getForEntity(location, String.class);

		assertThat(responseAccount.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

	@Test
	void updateAccountBalance() {
		Account account = new Account("tim", BigDecimal.valueOf(126.00));
		restTemplate = restTemplate.withBasicAuth("tim", "123456");
		URI location = restTemplate.postForLocation("/accounts", account);

		Account updateAccount = new Account(account.getId(), "tim", BigDecimal.valueOf(200.00));
		HttpEntity<Account> request = new HttpEntity<>(updateAccount);

		ResponseEntity<Void> response = restTemplate.exchange(location, HttpMethod.PUT, request, Void.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

		ResponseEntity<String> res = restTemplate.getForEntity(location, String.class);
		assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);

		DocumentContext doc = JsonPath.parse(res.getBody());
		Double balance = doc.read("$.balance");
		assertThat(balance).isEqualTo(326.00);

		Account updateAccountNegative = new Account(account.getId(), "tim", BigDecimal.valueOf(-200.00));
		request = new HttpEntity<>(updateAccountNegative);
		restTemplate.exchange(location, HttpMethod.PUT, request, Void.class);

		ResponseEntity<String> res2 = restTemplate.getForEntity(location, String.class);
		doc = JsonPath.parse(res2.getBody());
		balance = doc.read("$.balance");
		assertThat(balance).isEqualTo(326.00);
	}
}
