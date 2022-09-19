package com.devonfw.application.jtqj.testmanagement.dataaccess.api;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Size;

import com.devonfw.application.jtqj.general.dataaccess.api.ApplicationPersistenceEntity;
import com.devonfw.application.jtqj.queuemanagement.dataaccess.api.QueueEntity;
import com.devonfw.application.jtqj.testmanagement.common.api.Test;

@Entity
@Table(name = "Test")
public class TestEntity extends ApplicationPersistenceEntity implements Test {

  private int test;

  @Size(min = 2, max = 5)
  private String ticketNumber;

  private QueueEntity queue;

  private static final long serialVersionUID = 1L;

  @Override
  @Transient
  public Long getQueueId() {

    if (this.queue == null) {
      return null;
    }
    return this.queue.getId();
  }

  @Override
  public void setQueueId(Long queueId) {

    if (queueId == null) {
      this.queue = null;
    } else {
      QueueEntity queueEntity = new QueueEntity();
      queueEntity.setId(queueId);
      this.queue = queueEntity;
    }
  }

  @Override
  public int getTest() {

    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public void setTest(int test) {

    // TODO Auto-generated method stub

  }

  @Override
  public String getTicketNumber() {

    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void setTicketNumber(String ticketNumber) {

    // TODO Auto-generated method stub

  }

}
