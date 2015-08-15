package eionet.transfer.dao;
 
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
 
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.mock.web.MockMultipartFile;
 
import eionet.transfer.dao.StorageServiceFiles;
import org.junit.Test;
import org.junit.Ignore;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;
 
public class StorageServiceTest {
 
    @Test
    public void simpleTest() throws Exception {
        //Get the Spring Context
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("spring-dbtest-config.xml");
         
        //Get the StorageService Bean from the context.
        StorageService storageService = ctx.getBean("storageService", StorageService.class);

        MultipartFile file = new MockMultipartFile("Testfile.txt", "ABCDEF".getBytes());

        String newId = storageService.save(file);
        assertNotNull(newId);
        //System.out.println(newId);

        //Read
        //InputStream infp = storageService.getById(newId);
         
        //Close Spring Context
        ctx.close();
    }
}
