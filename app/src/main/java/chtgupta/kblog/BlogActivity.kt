package chtgupta.kblog

import android.os.Bundle
import android.text.Html
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ShareCompat
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_blog.*

class BlogActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_blog)

        val blogPost = intent.getSerializableExtra("blogPost") as BlogPost

        Glide.with(this).load(blogPost.featureImageUrl).into(preview)
        Glide.with(this).load(blogPost.authorDpUrl).into(authorDp)
        authorName.text = blogPost.authorName
        blogTitle.text = Html.fromHtml(Html.fromHtml(blogPost.title).toString())
        content.setHtml(blogPost.content)
        share.setOnClickListener {
            ShareCompat.IntentBuilder
                .from(this)
                .setText(blogPost.url)
                .setType("text/plain")
                .setChooserTitle("Refer via")
                .startChooser()
        }

    }
}
