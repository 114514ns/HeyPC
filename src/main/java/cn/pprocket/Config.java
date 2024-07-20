package cn.pprocket;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Config {
    List<String> blackWords = new ArrayList<String>();
    String cookies = "";
}
