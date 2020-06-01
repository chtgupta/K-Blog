package chtgupta.kblog

import java.io.Serializable

data class BlogPost(
    var title: String,
    var subtitle: String,
    var content: String,
    var featureImageUrl: String,
    var date: String,
    var authorName: String,
    var authorDpUrl: String,
    var url: String
) : Serializable {
    constructor() : this("", "", "", "", "", "", "", "")
}