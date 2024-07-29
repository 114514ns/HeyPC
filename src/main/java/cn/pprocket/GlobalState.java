package cn.pprocket;

import androidx.compose.runtime.MutableState;
import cn.pprocket.items.Post;
import cn.pprocket.items.Topic;
import cn.pprocket.items.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GlobalState {
    public static Map<String, Post> map = new HashMap<>();
    public static Map<String, User> users = new HashMap<>();
    public static Config config;
    public static String subCommentId = "-1";
    public static boolean started = false;
    public static List<Topic> topicList = new ArrayList<>();
    public static Object feeds = null;
}
