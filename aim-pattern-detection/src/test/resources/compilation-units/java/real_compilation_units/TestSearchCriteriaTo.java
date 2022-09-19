package com.devonfw.application.jtqj.testmanagement.logic.api.to;

import com.devonfw.application.jtqj.general.common.api.to.AbstractSearchCriteriaTo;
import com.devonfw.module.basic.common.api.query.StringSearchConfigTo;

/**
 * {@link SearchCriteriaTo} to find instances of {@link com.devonfw.application.jtqj.testmanagement.common.api.Test}s.
 */
public class TestSearchCriteriaTo extends AbstractSearchCriteriaTo {

  private static final long serialVersionUID = 1L;

  private Integer test;

  private String ticketNumber;

  private Long queueId;

  private StringSearchConfigTo ticketNumberOption;

  /**
   * @return testId
   */

  public Integer getTest() {

    return test;
  }

  /**
   * @param test setter for test attribute
   */

  public void setTest(Integer test) {

    this.test = test;
  }

  /**
   * @return ticketNumberId
   */

  public String getTicketNumber() {

    return ticketNumber;
  }

  /**
   * @param ticketNumber setter for ticketNumber attribute
   */

  public void setTicketNumber(String ticketNumber) {

    this.ticketNumber = ticketNumber;
  }

  /**
   * getter for queueId attribute
   * 
   * @return queueId
   */

  public Long getQueueId() {

    return queueId;
  }

  /**
   * @param queue setter for queue attribute
   */

  public void setQueueId(Long queueId) {

    this.queueId = queueId;
  }

  /**
   * @return the {@link StringSearchConfigTo} used to search for {@link #getTicketNumber() ticketNumber}.
   */
  public StringSearchConfigTo getTicketNumberOption() {

    return this.ticketNumberOption;
  }

  /**
   * @param ticketNumberOption new value of {@link #getTicketNumberOption()}.
   */
  public void setTicketNumberOption(StringSearchConfigTo ticketNumberOption) {

    this.ticketNumberOption = ticketNumberOption;
  }

}
