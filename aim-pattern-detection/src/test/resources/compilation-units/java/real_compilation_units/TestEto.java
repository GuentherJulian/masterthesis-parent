package com.devonfw.application.jtqj.testmanagement.logic.api.to;

import com.devonfw.application.jtqj.testmanagement.common.api.Test;
import com.devonfw.module.basic.common.api.to.AbstractEto;

/**
 * Entity transport object of Test
 */
public class TestEto extends AbstractEto implements Test {

  private static final long serialVersionUID = 1L;

  private int test;

  private String ticketNumber;

  private Long queueId;

  @Override
  public int getTest() {

    return test;
  }

  @Override
  public void setTest(int test) {

    this.test = test;
  }

  @Override
  public String getTicketNumber() {

    return ticketNumber;
  }

  @Override
  public void setTicketNumber(String ticketNumber) {

    this.ticketNumber = ticketNumber;
  }

  @Override
  public Long getQueueId() {

    return queueId;
  }

  @Override
  public void setQueueId(Long queueId) {

    this.queueId = queueId;
  }

  @Override
  public int hashCode() {

    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((Integer) test).hashCode();
    result = prime * result + ((this.ticketNumber == null) ? 0 : this.ticketNumber.hashCode());

    result = prime * result + ((this.queueId == null) ? 0 : this.queueId.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {

    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    // class check will be done by super type EntityTo!
    if (!super.equals(obj)) {
      return false;
    }
    TestEto other = (TestEto) obj;
    if (this.test != other.test) {
      return false;
    }
    if (this.ticketNumber == null) {
      if (other.ticketNumber != null) {
        return false;
      }
    } else if (!this.ticketNumber.equals(other.ticketNumber)) {
      return false;
    }

    if (this.queueId == null) {
      if (other.queueId != null) {
        return false;
      }
    } else if (!this.queueId.equals(other.queueId)) {
      return false;
    }
    return true;
  }
}
