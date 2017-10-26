package hangxu.skiresortserver;

import com.fasterxml.jackson.databind.ObjectMapper;
import data.MyVert;
import data.RFIDLiftData;
import java.io.IOException;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.POST;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;

@Path("myserver")
public class GenericResource {
    
    TaskManager taskManager = TaskManager.getTaskManagerInstance();
  
    @GET
    @Path("test_get")
    @Produces(MediaType.TEXT_PLAIN)
    public String getTest() {
        return "Get Successfully!";
    }
  
    @POST
    @Path("test_post")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public String loadTest(String testString) {
        return "test result: " + testString;
    }
    
    @POST
    @Path("loaddata")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public String postRecord(String dataInJson) {
        return dataInJson;
    }
    
    @POST
    @Path("load")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public String loadRecord(String dataInJson) {
        try {
            // change dataInJson to RFIDLiftData object
            ObjectMapper mapper = new ObjectMapper();
            RFIDLiftData data = mapper.readValue(dataInJson, RFIDLiftData.class);
            
            taskManager.insertRecordToDB(data);           
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ">> loading: " + dataInJson;
    }
    
    @GET
    @Path("myvert/{skierId}&{dayNum}")
    @Produces(MediaType.APPLICATION_JSON)
    public MyVert getRecord(@PathParam("skierId") int skierId, 
                            @PathParam("dayNum") int dayNum) {
        return taskManager.getMyVertFromDB(skierId, dayNum);
    }
}
