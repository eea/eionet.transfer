package eionet.transfer.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;

import eionet.transfer.model.Upload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

public class UploadsServiceJdbc implements UploadsService {

    private DataSource dataSource;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void save(Upload upload) {
        String query = "INSERT INTO uploads (id, expires, filename, uploader) VALUES (?, ?, ?, ?)";
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.update(query, upload.getId(), upload.getExpires(), upload.getFilename(), upload.getUploader());
    }

    @Override
    public Upload getById(String id) {
        String query = "SELECT id, expires, filename, uploader FROM uploads WHERE id = ?";
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

        //using RowMapper anonymous class, we can create a separate RowMapper for reuse
        Upload uploadRec = jdbcTemplate.queryForObject(query, new Object[]{id}, new RowMapper<Upload>() {

            @Override
            public Upload mapRow(ResultSet rs, int rowNum)
                    throws SQLException {
                Upload uploadRec = new Upload(rs.getString("id"), rs.getString("filename"));
                return uploadRec;
            }});

        return uploadRec;
    }

    @Override
    public List<Upload> getAll() {
        String query = "SELECT id, expires, filename, uploader FROM uploads";
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        List<Upload> uploadList = new ArrayList<Upload>();

        List<Map<String, Object>> uploadRows = jdbcTemplate.queryForList(query);

        for(Map<String, Object> uploadRow : uploadRows){
            Upload uploadRec = new Upload(String.valueOf(uploadRow.get("id")), String.valueOf(uploadRow.get("filename")));
            uploadList.add(uploadRec);
        }
        return uploadList;
    }

}
