package org.unibl.etf.sni.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageDto {

    private int id;
    private String text;
    private Timestamp createTime;
    private int sender;
    private int recipient;

}
