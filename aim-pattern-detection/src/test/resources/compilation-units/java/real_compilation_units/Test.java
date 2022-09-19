package com.devonfw.application.jtqj.testmanagement.common.api;

import com.devonfw.application.jtqj.general.common.api.ApplicationEntity;

public interface Test extends ApplicationEntity {

  /**
   * @return testId
   */

  public int getTest();

  /**
   * @param test setter for test attribute
   */

  public void setTest(int test);

  /**
   * @return ticketNumberId
   */

  public String getTicketNumber();

  /**
   * @param ticketNumber setter for ticketNumber attribute
   */

  public void setTicketNumber(String ticketNumber);

  /**
   * getter for queueId attribute
   * 
   * @return queueId
   */

  public Long getQueueId();

  /**
   * @param queue setter for queue attribute
   */

  public void setQueueId(Long queueId);

}
