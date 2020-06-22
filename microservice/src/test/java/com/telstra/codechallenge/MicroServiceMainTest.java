package com.telstra.codechallenge;

import static org.junit.Assert.assertEquals;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.telstra.codechallenge.errorHandling.CustomException;
import com.telstra.codechallenge.quotes.FindUsers;
import com.telstra.codechallenge.quotes.SpringBootQuotesService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import com.telstra.codechallenge.quotes.FindUsers.Item;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class MicroServiceMainTest {

  @LocalServerPort
  private int port;

  @Autowired
  private TestRestTemplate restTemplate;

  @Test
  public void testHealth() throws RestClientException, MalformedURLException {
    System.out.println("PORT NUMBER -"+port);
    ResponseEntity<String> response = restTemplate
        .getForEntity(new URL("http://localhost:" + port + "/actuator/health")
            .toString(), String.class);
    assertEquals("{\"status\":\"UP\"}", response
        .getBody());
  }

  @Test
  public void testGetUser() throws RestClientException {
    int number=5;
    port=8090;
    System.out.println(" checking port");
    String ROOT_URI= "http://localhost:"+port+"/user/"+number;
    ResponseEntity<String> response = restTemplate.getForEntity(ROOT_URI, String.class);
    System.out.println("status code -"+response.getStatusCode());
    System.out.println("response body -"+response.getBody());
    assertEquals(200,response.getStatusCodeValue());
  }

  @Test
  public void testGetUserNegative() throws RestClientException {
    int number=5;
    port=8090;
    String ROOT_URI= "http://localhost:"+port+"/user/";
    ResponseEntity<String> response = restTemplate.getForEntity(ROOT_URI, String.class);
    System.out.println("status code -"+response.getStatusCode());
    System.out.println("response body -"+response.getBody());
    assertEquals(404,response.getStatusCodeValue());
  }

  @Test
  public void testgetWebsitesServicePositive() throws RestClientException {
    RestTemplate restTemplate = new RestTemplate();
    SpringBootQuotesService springBootQuotesService = new SpringBootQuotesService(restTemplate);
    List<Item> userList = new ArrayList<>();
    Integer numberOfUsers = 2;
    try {
      userList = springBootQuotesService.getWebsites(numberOfUsers);
      if(userList.size()<numberOfUsers || userList.size()==0){
        throw new CustomException("Unable to get users","users not found for the criteria",204);
      }else{
        System.out.println(userList.get(0).getId());
      }

    }catch (CustomException ex){
      System.out.println("Exception - "+ex.getMessage()+ex.getDetails());
    } catch (Exception e) {
      e.printStackTrace();
    }
    assert(userList.size()>0);
  }

  @Test
  public void testgetWebsitesServiceNegative() throws RestClientException {
    RestTemplate restTemplate = new RestTemplate();
    SpringBootQuotesService springBootQuotesService = new SpringBootQuotesService(restTemplate);
    List<Item> userList = new ArrayList<>();
    Integer numberOfUsers = 2;
    try {
      userList = springBootQuotesService.getWebsites(numberOfUsers);
      userList.clear(); // to makae the list null , returning no data to user
      if(userList.size()<numberOfUsers || userList.size()==0){
        throw new CustomException("Unable to get users","users not found for the criteria",204);
      }else{
        System.out.println(userList);
      }

    }catch (CustomException ex){
      System.out.println("Exception - "+ex.getMessage());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }


}
