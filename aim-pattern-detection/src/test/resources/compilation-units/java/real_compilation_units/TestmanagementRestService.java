package com.devonfw.application.jtqj.testmanagement.service.api.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.springframework.data.domain.Page;

import com.devonfw.application.jtqj.testmanagement.logic.api.Testmanagement;
import com.devonfw.application.jtqj.testmanagement.logic.api.to.TestEto;
import com.devonfw.application.jtqj.testmanagement.logic.api.to.TestSearchCriteriaTo;

/**
 * The service interface for REST calls in order to execute the logic of component {@link Testmanagement}.
 */
@Path("/testmanagement/v1")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface TestmanagementRestService {

  /**
   * Delegates to {@link Testmanagement#findTest}.
   *
   * @param id the ID of the {@link TestEto}
   * @return the {@link TestEto}
   */
  @GET
  @Path("/test/{id}/")
  public TestEto getTest(@PathParam("id") long id);

  /**
   * Delegates to {@link Testmanagement#saveTest}.
   *
   * @param test the {@link TestEto} to be saved
   * @return the recently created {@link TestEto}
   */
  @POST
  @Path("/test/")
  public TestEto saveTest(TestEto test);

  /**
   * Delegates to {@link Testmanagement#deleteTest}.
   *
   * @param id ID of the {@link TestEto} to be deleted
   */
  @DELETE
  @Path("/test/{id}/")
  public void deleteTest(@PathParam("id") long id);

  /**
   * Delegates to {@link Testmanagement#findTestEtos}.
   *
   * @param searchCriteriaTo the pagination and search criteria to be used for finding tests.
   * @return the {@link Page list} of matching {@link TestEto}s.
   */
  @Path("/test/search")
  @POST
  public Page<TestEto> findTests(TestSearchCriteriaTo searchCriteriaTo);

}