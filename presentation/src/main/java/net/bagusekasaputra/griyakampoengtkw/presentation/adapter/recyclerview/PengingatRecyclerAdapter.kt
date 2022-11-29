package net.bagusekasaputra.griyakampoengtkw.presentation.adapter.recyclerview

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Pengingat
import net.bagusekasaputra.griyakampoengtkw.presentation.R
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.LayoutItemPengingatBinding

class PengingatRecyclerAdapter(
    private val listPengingat: List<Pengingat>,
    private val onImgNotifClick: (position: Int) -> Boolean,
    private val onItemLongClick: (position: Int) -> Unit,
): RecyclerView.Adapter<PengingatRecyclerAdapter.PengingatViewHolder>() {

    private lateinit var mContext: Context

    class PengingatViewHolder(binding: LayoutItemPengingatBinding): RecyclerView.ViewHolder(binding.root) {
        val container = binding.cardRootPengingat
        val imgNotif = binding.imgNotificationIcon
        val tvTitle = binding.tvTitlePengingat
        val tvDate = binding.tvDatePengingat
        val tvTime = binding.tvTimePengingat
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PengingatViewHolder {
        mContext = parent.context

        return LayoutInflater.from(parent.context).let { layoutInflater ->
            LayoutItemPengingatBinding.inflate(layoutInflater, parent, false).let { binding ->
                PengingatViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: PengingatViewHolder, position: Int) {
        val pengingat = listPengingat[position]

        // Set notification off icon when isActive is false
        if (pengingat.isActive.not()) {
            changeImgNotif(holder.imgNotif, false)
        }
        holder.tvTitle.text = pengingat.title
        holder.tvDate.text = pengingat.date
        holder.tvTime.text = pengingat.time

        holder.container.setOnLongClickListener {
            onItemLongClick(position)

            true
        }

        holder.imgNotif.setOnClickListener {
            val isActive = onImgNotifClick(position)

            changeImgNotif(holder.imgNotif, isActive)
        }
    }

    private fun changeImgNotif(imgNotif: ImageView, isActive: Boolean) {
        if (isActive) {
            imgNotif.setImageDrawable(
                ContextCompat.getDrawable(mContext, R.drawable.ic_baseline_notifications_active_48)
            )
        } else {
            imgNotif.setImageDrawable(
                ContextCompat.getDrawable(mContext, R.drawable.ic_baseline_notifications_off_48)
            )
        }
    }

    override fun getItemCount(): Int {
        return listPengingat.size
    }
}