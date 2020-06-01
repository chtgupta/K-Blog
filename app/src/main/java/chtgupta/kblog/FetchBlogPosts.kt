package chtgupta.kblog

import android.os.AsyncTask
import android.util.Log
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class FetchBlogPosts(private val pageNumber: Int, private val listener: Listener) : AsyncTask<Void, Void, String?>() {

    override fun doInBackground(vararg p0: Void?): String? {

        var result: String? = null

        try {

            val url = URL("https://techcrunch.com/wp-json/wp/v2/posts?per_page=10&page=$pageNumber&orderby=date&order=desc&_embed")

            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            val responseCode = connection.responseCode

            if (responseCode == HttpURLConnection.HTTP_OK) {

                val reader = BufferedReader(
                    InputStreamReader(
                        connection.inputStream
                    )
                )

                reader.use {
                    val response = StringBuilder()

                    var inputLine = it.readLine()
                    while (inputLine != null) {
                        response.append(inputLine)
                        inputLine = it.readLine()
                    }

                    result = response.toString()
                }

            }

        } catch (e : Exception) {
            return null
        }

        return result

    }

    override fun onPostExecute(result: String?) {
        super.onPostExecute(result)

        if (result == null) listener.onFailure()
        else {

            try {

                val blogPosts = ArrayList<BlogPost>()
                val array = JSONArray(result)

                for (i in 0 until array.length()) {

                    val jsonObject = array.get(i) as JSONObject
                    val blogPost = BlogPost()

                    blogPost.date = jsonObject.getString("date_gmt")
                    blogPost.url = jsonObject.getString("link")
                    blogPost.title = jsonObject.getJSONObject("title").getString("rendered")
                    blogPost.subtitle = jsonObject.getJSONObject("excerpt").getString("rendered")
                    blogPost.content = jsonObject.getJSONObject("content").getString("rendered")
                    blogPost.featureImageUrl = jsonObject.getJSONObject("_embedded").getJSONArray("wp:featuredmedia").getJSONObject(0).getString("source_url")
                    blogPost.authorName = jsonObject.getJSONObject("_embedded").getJSONArray("author").getJSONObject(0).getString("name")
                    blogPost.authorDpUrl = jsonObject.getJSONObject("_embedded").getJSONArray("author").getJSONObject(0).getJSONObject("avatar_urls").getString("96")

                    blogPosts.add(blogPost)
                }

                listener.onSuccess(blogPosts)


            } catch (e : Exception) {
                listener.onFailure()
            }

        }
    }

    interface Listener {
        fun onSuccess(blogPosts: List<BlogPost>)
        fun onFailure()
    }

}