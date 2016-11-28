package gov.ca.cwds.dao.elasticsearch;

import java.io.File;
import java.io.IOException;

import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;
import org.junit.BeforeClass;


public class ElasticsearchDaoTest {

  private static Client client;
  private static Node node;
  private static File tempDir;


  @BeforeClass
  public static void setUp() throws IOException, InterruptedException {
    tempDir = File.createTempFile("elasticsearch-temp", Long.toString(System.nanoTime()));
    tempDir.delete();
    tempDir.mkdir();
    System.out.println("writing to: " + tempDir);
    Settings settings = Settings.settingsBuilder().put("path.home", tempDir).build();
    node = NodeBuilder.nodeBuilder().settings(settings).node();
    client = node.client();
  }

  // @Test
  // public void testCreateDocument() throws Exception {
  // ElasticsearchClient dao = new ElasticsearchClient("", "", "", "", "cms_documents_1",
  // "referral_doc");
  // dao.setClient(client);
  // String indexName = "cms_documents_1";
  // String docType = "referral_doc";
  // boolean flag = dao.createDocument("{\"id\":\"1234\",\"name\":\"xyz\"}", "1234");
  // assertThat(flag, is(Boolean.TRUE));
  //
  // GetResponse response = client.prepareGet(indexName, docType, "1234").execute().actionGet();
  // assertThat(response.getIndex(), is(indexName));
  // assertThat(response.getType(), is(docType));
  // assertThat(response.getSource().size(), is(2));
  // assertThat(response.getSource().get("id"), is("1234"));
  // assertThat(response.getSource().get("name"), is("xyz"));
  // }
  //
  // @Test(expected = RuntimeException.class)
  // public void testCreateDocument_when_indexNameNull() throws Exception {
  // ElasticsearchClient dao = new ElasticsearchClient("", "", "", "", null, "referral_doc");
  // dao.setClient(client);
  // dao.createDocument("{\"id\":\"123\"}", "123");
  // }
  //
  // @Test(expected = RuntimeException.class)
  // public void testCreateDocument_when_indexNameEmpty() throws Exception {
  // ElasticsearchClient dao = new ElasticsearchClient("", "", "", "", " ", "referral_doc");
  // dao.setClient(client);
  // dao.createDocument("{\"id\":\"123\"}", "123");
  // }
  //
  // @Test(expected = RuntimeException.class)
  // public void testCreateDocument_when_documentTypeNull() throws Exception {
  // ElasticsearchClient dao = new ElasticsearchClient("", "", "", "", "cms_documents_1", null);
  // dao.setClient(client);
  // dao.createDocument("{\"id\":\"123\"}", "123");
  // }
  //
  // @Test(expected = RuntimeException.class)
  // public void testCreateDocument_when_documentTypeEmpty() throws Exception {
  // ElasticsearchClient dao = new ElasticsearchClient("", "", "", "", "cms_documents_1", "");
  // dao.setClient(client);
  // dao.createDocument("{\"id\":\"123\"}", "123");
  // }
  //
  // @Test(expected = RuntimeException.class)
  // public void testCreateDocument_when_documentNull() throws Exception {
  // ElasticsearchClient dao = new ElasticsearchClient("", "", "", "", "cms_documents_1",
  // "referral_doc");
  // dao.setClient(client);
  // dao.createDocument(null, "123");
  // }
  //
  // @Test(expected = RuntimeException.class)
  // public void testCreateDocument_when_documentEmpty() throws Exception {
  // ElasticsearchClient dao = new ElasticsearchClient("", "", "", "", "cms_documents_1",
  // "referral_doc");
  // dao.setClient(client);
  // dao.createDocument(" ", "123");
  //
  // }
  //
  // @AfterClass
  // public static void tearDown() throws Exception {
  // System.out.println("closing client............");
  // client.close();
  // node.close();
  // removeDirectory(tempDir);
  // }
  //
  // public static void removeDirectory(File dir) throws IOException {
  // System.out.println("removeing dir..........");
  // if (dir.isDirectory()) {
  // File[] files = dir.listFiles();
  // if (files != null && files.length > 0) {
  // for (File aFile : files) {
  // removeDirectory(aFile);
  // }
  // }
  // }
  // System.out.println("...." + dir.toPath());
  // Files.delete(dir.toPath());
  // }
}
