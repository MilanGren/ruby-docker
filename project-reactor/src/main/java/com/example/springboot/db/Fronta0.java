package com.example.springboot.db;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table
public class Fronta0 {

    @Id
    private Long no;

    private String noderef;

    private String edid;

    private String davkaid;

    private String status;


}
