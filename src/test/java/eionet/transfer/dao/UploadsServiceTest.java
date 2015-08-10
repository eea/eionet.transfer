package eionet.transfer.dao;
 
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
 
import org.springframework.context.support.ClassPathXmlApplicationContext;
 
import eionet.transfer.model.Upload;
import org.junit.Test;
import org.junit.Ignore;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;
 
// See http://www.journaldev.com/2593/spring-jdbc-and-jdbctemplate-crud-with-datasource-example-tutorial
public class UploadsServiceTest {
 
    @Test
    public void simpleTest() throws Exception {
        //Get the Spring Context
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("spring-dbtest-config.xml");
         
        //Get the UploadsService Bean from the context.
        UploadsService uploadsService = ctx.getBean("uploadsService", UploadsService.class);

        uploadsService.deleteAll();

        //Run some tests for JDBC CRUD operations
        Upload doc = new Upload("b1dd4c8e-18b4-445c-bc75-d373dad22c40", "testfile.txt");
        Date expirationDate = new Date(stringDatetimeToTimestamp("2013-05-01 19:00:01"));
        doc.setExpires(expirationDate);
        doc.setUploader("testperson");
        uploadsService.save(doc);

        Upload doc2 = new Upload("62c8a681-bf6f-4d88-878c-a2e92ea310e1", "testfile1.txt");
        doc2.setExpires(expirationDate);
        doc2.setUploader("testperson");
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
 
    /**
     * Mock function that converts a datetime value in string format into a timestamp
     * value, in the same way that the MySql jdbc driver does.
     * 
     * @param datetime The datetime value in yyyy-MM-dd HH:mm:ss format
     * @return The corresponding unix timestamp
     * @throws ParseException 
     */
    private long stringDatetimeToTimestamp(String datetime) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long millis = format.parse(datetime).getTime();

        return millis;
    }

}
