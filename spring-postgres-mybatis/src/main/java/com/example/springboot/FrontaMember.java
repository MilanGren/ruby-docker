package com.example.springboot;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;


@Data
public class FrontaMember {

    private Long no;
    private String noderef;
    private String edid;
    private String davkaid;
    private String status;

    public FrontaMember(String noderef, String edid, String davkaid, String status) {
        this.noderef = noderef;
        this.edid = edid;
        this.davkaid = davkaid;
        this.status = status;
     }

}
