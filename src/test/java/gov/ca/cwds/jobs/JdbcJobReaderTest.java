package gov.ca.cwds.jobs;

import gov.ca.cwds.data.persistence.PersistentObject;
import gov.ca.cwds.jobs.util.JobReader;
import gov.ca.cwds.jobs.util.jdbc.JdbcJobReader;
import gov.ca.cwds.jobs.util.jdbc.RowMapper;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.Assert;
import org.junit.Test;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author CWDS Elasticsearch Team
 */
public class JdbcJobReaderTest {

  @Test
  public void testRead() throws Exception {
    SessionFactory sessionFactory =
        new Configuration().configure("test-lis-hibernate.cfg.xml").buildSessionFactory();
    JobReader<Item> jobReader = new JdbcJobReader<>(sessionFactory, new ItemMapper(),
        "select * from test order by a");
    jobReader.init();
    int counter = 0;
    Item item;
    while ((item = jobReader.read()) != null) {
      counter++;
      Assert.assertEquals(counter, item.getA());
      Assert.assertEquals(String.valueOf(counter), item.getB());
    }
    Assert.assertEquals(3, counter);
  }

  private static class Item implements PersistentObject {

    private int a;
    private String b;

    public int getA() {
      return a;
    }

    public void setA(int a) {
      this.a = a;
    }

    public String getB() {
      return b;
    }

    public void setB(String b) {
      this.b = b;
    }

    @Override
    public Serializable getPrimaryKey() {
      return a;
    }
  }

  private static class ItemMapper implements RowMapper<Item> {

    @Override
    public Item mapRow(ResultSet resultSet) throws SQLException {
      Item item = new Item();
      item.setA(resultSet.getInt("a"));
      item.setB(resultSet.getString("b"));
      return item;
    }
  }
}
