package uz.fido.universaldigital.ui.fragments.products.widgets.notifications.news

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import uz.fido.network.domain.model.news.News
import uz.fido.universaldigital.base.BaseInterface
import uz.fido.universaldigital.databinding.ItemNewsBinding

class NewsAdapter(
    private var baseInterface: BaseInterface,
    private var list: ArrayList<News>
) : RecyclerView.Adapter<NewsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemNewsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    fun setList(news: ArrayList<News>) {
        list = news
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class ViewHolder(private val binding: ItemNewsBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: News) {
            binding.title.text = item.title
            binding.description.text = item.content
            binding.date.text = item.date
            binding.description.setTypeface(null, Typeface.NORMAL)
            binding.father.setOnClickListener {
                baseInterface.openNews(item, item.img_url)
                notifyDataSetChanged()
            }
        }
    }

}