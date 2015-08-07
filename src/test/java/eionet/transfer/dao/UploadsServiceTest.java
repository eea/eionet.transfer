package eionet.transfer.dao;
 
import java.util.List;
 
import org.springframework.context.support.ClassPathXmlApplicationContext;
 
import eionet.transfer.model.Upload;
import org.junit.Test;
import org.junit.Ignore;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;
 
// See http://www.journaldev.com/2593/spring-jdbc-and-jdbctemplate-crud-with-datasource-example-tutorial
public class UploadsServiceTest {
 
    @Ignore @Test
    public void simpleTest() {
        //Get the Spring Context
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("spring-dbtest-config.xml");
         
        //Get the UploadsService Bean from the context.
        UploadsService uploadsService = ctx.getBean("uploadsService", UploadsService.class);
         
        //Run some tests for JDBC CRUD operations
        Upload doc = new Upload("b1dd4c8e-18b4-445c-bc75-d373dad22c40", "testfile.txt");
        //doc.setContent("Java Developer");
        uploadsService.save(doc);

        Upload doc2 = new Upload("62c8a681-bf6f-4d88-878c-a2e92ea310e1", "testfile1.txt");
        uploadsService.save(doc2);

        //Read
        Upload doc1 = uploadsService.getById("b1dd4c8e-18b4-445c-bc75-d373dad22c40");
        assertNotNull(doc1);
         
        //Get All
        List<Upload> docList = uploadsService.getAll();
        assertTrue(docList.size() > 0);

        //Close Spring Context
        ctx.close();
    }
 
}
