package cn.pprocket;

import cn.pprocket.items.Post;
import cn.pprocket.items.User;

import java.util.HashMap;
import java.util.Map;

public class GlobalState {
    public static Map<String, Post> map = new HashMap<>();
    public static Map<String, User> users = new HashMap<>();
    public static Config config;
}
