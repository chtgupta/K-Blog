package chtgupta.kblog

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), FetchBlogPosts.Listener {

    private val allPosts = ArrayList<BlogPost?>()
    private val adapter = BlogPostAdapter(allPosts)
    private var currentPage = 1
    private var isLoading = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.addItemDecoration(ItemDecoration(this, 16))

//        progressBar.visibility = View.GONE
        recyclerView.adapter = adapter

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val linearLayoutManager =
                    recyclerView.layoutManager as LinearLayoutManager?
                if (!isLoading) {
                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == allPosts.size - 1) { //bottom of list
                        loadData()
                        isLoading = true
                    }
                }
            }
        })

        loadData()
    }

    private fun loadData() {
        FetchBlogPosts(currentPage, this).execute()
    }

    override fun onSuccess(blogPosts: List<BlogPost>) {

        if (progressBar.visibility != View.GONE) {
            progressBar.visibility = View.GONE
        }

        if (allPosts.size != 0 && allPosts[allPosts.size - 1] == null) {
            allPosts.removeAt(allPosts.size - 1)
        }

        allPosts.addAll(blogPosts)

        if (blogPosts.size == 10) {
            allPosts.add(null)
        }

        adapter.notifyDataSetChanged()

        currentPage++
        isLoading = false
    }

    override fun onFailure() {
        if (allPosts.size != 0 && allPosts[allPosts.size - 1] == null) {
            allPosts.removeAt(allPosts.size - 1)
        }
    }

    inner class BlogPostAdapter(private var blogPosts: List<BlogPost?>) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        private val viewTypeItem = 0
        private val viewTypeLoading = 1

        inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val preview: ImageView = itemView.findViewById(R.id.preview)
            val authorDp: CircleImageView = itemView.findViewById(R.id.authorDp)
            val title: TextView = itemView.findViewById(R.id.title)
            val subtitle: TextView = itemView.findViewById(R.id.subtitle)
        }

        inner class LoadingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

        override fun getItemViewType(position: Int): Int = if (blogPosts[position] == null) {
            viewTypeLoading
        } else {
            viewTypeItem
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return if (viewType == viewTypeItem) {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.recycler_item_blog_post, parent, false)
                ItemViewHolder(view)
            } else {
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.recycler_item_loading, parent, false)
                LoadingViewHolder(
                    view
                )
            }
        }

        override fun getItemCount(): Int = blogPosts.size

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

            if (holder is ItemViewHolder) {
                val blogPost = blogPosts[position]!!
                Glide.with(this@MainActivity).load(blogPost.featureImageUrl).into(holder.preview)
                Glide.with(this@MainActivity).load(blogPost.authorDpUrl).into(holder.authorDp)
                holder.title.text =
                    Html.fromHtml(Html.fromHtml(blogPost.title).toString())
                holder.subtitle.text =
                    Html.fromHtml(Html.fromHtml(blogPost.subtitle).toString())

                holder.itemView.setOnClickListener {
                    startActivity(Intent(this@MainActivity, BlogActivity::class.java)
                        .putExtra("blogPost", blogPost))
                }

            }

        }
    }

    inner class ItemDecoration (private val context: Context, private val spacing: Int) :
        RecyclerView.ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            super.getItemOffsets(outRect, view, parent, state)
            val count = state.itemCount
            val position = parent.getChildAdapterPosition(view)
            val spacing = pxFromDp(context, spacing).toInt()
            outRect.left = spacing
            outRect.right = spacing
            outRect.top = if (position == 0) spacing else spacing / 2
            outRect.bottom = if (position == count - 1) spacing else spacing / 2
        }

        private fun pxFromDp(context: Context, dp: Int): Float {
            return dp * context.resources.displayMetrics.density
        }

    }

}
