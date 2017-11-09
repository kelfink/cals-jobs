package gov.ca.cwds.jobs.test;

import static com.github.tlrx.elasticsearch.test.EsSetup.createIndex;
import static com.github.tlrx.elasticsearch.test.EsSetup.createTemplate;
import static com.github.tlrx.elasticsearch.test.EsSetup.delete;
import static com.github.tlrx.elasticsearch.test.EsSetup.deleteAll;
import static com.github.tlrx.elasticsearch.test.EsSetup.fromClassPath;
import static com.github.tlrx.elasticsearch.test.EsSetup.index;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;

import com.github.tlrx.elasticsearch.test.EsSetup;

public class SimpleEsTest {

  EsSetup esSetup;

  @Before
  public void setUp() throws Exception {
    // Instantiate a local node & client.
    esSetup = new EsSetup();
    // Clean all, create indices.
    esSetup.execute(deleteAll(), createIndex("my_index_1"),
        createIndex("my_index_2").withSettings(fromClassPath("path/to/settings.json"))
            .withMapping("type1", fromClassPath("path/to/mapping/of/type1.json"))
            .withData(fromClassPath("path/to/bulk.json")),
        createTemplate("template-1").withSource(fromClassPath("path/to/template1.json")));
  }

  @After
  public void tearDown() throws Exception {
    // stop and clean the local node.
    esSetup.terminate();
  }

  /**
   * Missing required class, org.elasticsearch.common.Preconditions. :-(
   */
  // @Test
  public void testMethod() {
    // check if the index exists
    assertTrue(esSetup.exists("my_index_2"));
    // Index a new document
    esSetup.execute(index("my_index_2", "type1", "1").withSource("{ \"field1\" : \"value1\" }"));
    // Count the number of documents
    Long nb = esSetup.countAll();
    // Delete a document
    esSetup.execute(delete("my_index_2", "type1", "1"));
    // Clean all indices
    esSetup.execute(deleteAll());
  }

}
