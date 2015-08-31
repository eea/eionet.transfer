package eionet.transfer.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.UserRequestPostProcessor.*;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.junit.Test;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;


@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations = {"classpath:spring-mvctest-config.xml",
        "classpath:spring-dbtest-config.xml",
        "classpath:spring-securitytest-config.xml"})

/**
 * Test the file operations.
 *
 * @see <a href="http://docs.spring.io/spring-framework/docs/3.2.0.BUILD-SNAPSHOT/reference/htmlsingle/#spring-mvc-test-framework">MVC testing</a>
 * @see <a href="http://docs.spring.io/spring-framework/docs/3.2.0.RC2/api/org/springframework/test/web/servlet/ResultActions.html">Result Actions</a>
 */
public class FileOpsControllerTest {

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @Autowired
    private FilterChainProxy springSecurityFilterChain;

    @Before
    public void setUp() throws Exception {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac)
            .addFilters(this.springSecurityFilterChain)
            .build();
    }

    /**
     * Since it is protected, it will redirect to login.
     */
    @Test
    public void testUploadForm() throws Exception {
        this.mockMvc.perform(get("/fileupload"))
                .andExpect(status().is3xxRedirection());
    }

    /**
     * This will succeed.
     */
    @Test
    public void authenticatedUploadForm() throws Exception {
        this.mockMvc.perform(get("/fileupload").with(user("admin")))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("breadcrumbs"))
                .andExpect(view().name("fileupload"))
                .andExpect(content().contentType("text/html;charset=UTF-8"));
    }

    /**
     * No authentication.
     */
    @Test
    public void failUpload() throws Exception {
        mockMvc.perform(fileUpload("/fileupload")
                .file("file", "ABCDEF".getBytes("UTF-8"))
                .param("fileTTL", "10"))
                .andExpect(status().isForbidden());
    }

    /**
     * This upload is expected to succeed.
     */
    @Test
    public void goodUpload() throws Exception {
        mockMvc.perform(fileUpload("/fileupload")
                .file("file", "ABCDEF".getBytes("UTF-8"))
                .param("fileTTL", "10").with(csrf()).with(user("admin").roles("ADMIN")))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("uploadSuccess"))
                .andExpect(flash().attributeCount(1));
    }

    /**
     * This upload is expected to succeed.
     */
    @Test
    public void goodAjaxUpload() throws Exception {
        mockMvc.perform(fileUpload("/fileupload")
                .file("file", "ABCDEF".getBytes("UTF-8"))
                .param("ajaxupload", "1")
                .param("fileTTL", "10")
                .with(csrf()).with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/xml"));
    }
    /**
     * This upload attempt has no CSRF token.
     */
    @Test
    public void noCSRFUpload() throws Exception {
        mockMvc.perform(fileUpload("/fileupload")
                .file("file", "ABCDEF".getBytes("UTF-8"))
                .param("fileTTL", "10").with(user("admin").roles("ADMIN")))
                .andExpect(status().is4xxClientError());
    }
}
