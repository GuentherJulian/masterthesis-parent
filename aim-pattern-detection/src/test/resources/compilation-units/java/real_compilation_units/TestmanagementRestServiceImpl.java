package com.devonfw.application.jtqj.testmanagement.service.impl.rest;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.data.domain.Page;

import com.devonfw.application.jtqj.testmanagement.logic.api.Testmanagement;
import com.devonfw.application.jtqj.testmanagement.logic.api.to.TestEto;
import com.devonfw.application.jtqj.testmanagement.logic.api.to.TestSearchCriteriaTo;
import com.devonfw.application.jtqj.testmanagement.service.api.rest.TestmanagementRestService;

/**
 * The service implementation for REST calls in order to execute the logic of component {@link Testmanagement}.
 */
@Named("TestmanagementRestService")
public class TestmanagementRestServiceImpl implements TestmanagementRestService {

  @Inject
  private Testmanagement testmanagement;

  @Override
  public TestEto getTest(long id) {

    return this.testmanagement.findTest(id);
  }

  @Override
  public TestEto saveTest(TestEto test) {

    return this.testmanagement.saveTest(test);
  }

  @Override
  public void deleteTest(long id) {

    this.testmanagement.deleteTest(id);
  }

  @Override
  public Page<TestEto> findTests(TestSearchCriteriaTo searchCriteriaTo) {

    return this.testmanagement.findTests(searchCriteriaTo);
  }
}