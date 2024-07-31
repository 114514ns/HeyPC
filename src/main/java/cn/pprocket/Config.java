package cn.pprocket;

import cn.pprocket.items.User;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Config {
    List<String> blackWords = new ArrayList<String>();
    String cookies = "";
    boolean isLogin = false;
    User user = new User();
    boolean blockCube = true;
    boolean showTab = false;
    boolean originImage = true;
    int colorRed;
    int colorGreen;
    int colorBlue;
}
