package cn.pprocket

import cn.pprocket.items.Post
import cn.pprocket.items.Topic
import cn.pprocket.items.User

object GlobalState {
    var map= mutableMapOf<String,Post>()
    var users = mutableMapOf<String,User>()
    var config: Config = Config()
    var subCommentId: String = "-1"
    var started: Boolean = false
    var topicList: List<Topic> = mutableListOf()
    var feeds: Any? = null
    var isFlowing: Boolean = false
}
