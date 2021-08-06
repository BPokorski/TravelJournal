package com.example.traveljournal.presentation.view.recyclerView

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.ViewGroup
import android.widget.Toast
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.traveljournal.R
import com.example.traveljournal.data.GlideApp
import com.example.traveljournal.data.SessionManager
import com.example.traveljournal.data.model.response.PhotoDescriptionResponse
import com.example.traveljournal.presentation.view.Navigator
import com.example.traveljournal.utils.DescriptionUtils

class PhotoDescriptionAdapter(private val context: Context):
        PagedListAdapter<PhotoDescriptionResponse,RecyclerView.ViewHolder>(PhotoDescriptionDiffCallback) {


    private var sessionManager = SessionManager(context)
    private var descriptionUtils = DescriptionUtils(context)

    var cardClickListener:CardClickListener? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):RecyclerView.ViewHolder {
        return PhotoDescriptionViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
       var item = getItem(position)

        if (item != null) {
            (holder as PhotoDescriptionViewHolder).cardView.rotation = item.rotateAngle?.toFloat()!!
            holder.galleryTitle.text = item.country

            var circularProgressDrawable = CircularProgressDrawable(context)
            circularProgressDrawable.backgroundColor = Color.TRANSPARENT
            circularProgressDrawable.setStartEndTrim(0.toFloat(), 1.toFloat())
            circularProgressDrawable.setStyle(CircularProgressDrawable.LARGE)
            circularProgressDrawable.setColorSchemeColors(Color.WHITE)

            circularProgressDrawable.start()

            var serverName = context.getString(R.string.server_name)
            var photoUrl = serverName + sessionManager.fetchLogin()  + "/photo/"  + item.photoId
            var glideUrl: GlideUrl = GlideUrl(photoUrl, LazyHeaders.Builder()
                    .addHeader("Authorization", "Bearer " + sessionManager.fetchAuthToken())
                    .build())

            GlideApp.with(context)
                    .load(glideUrl)
                    .timeout(10000)
                    .placeholder(circularProgressDrawable)
                    .transform(RoundedCorners(16))
                    .into(holder.galleryImage)

        } else {
            Toast.makeText(context, "Item is null", Toast.LENGTH_SHORT).show()
        }
        holder.itemView.setOnClickListener {
            cardClickListener?.onCardClickListener(item, position)
            descriptionUtils.saveAddressInfo(item)
        }
    }
    fun setOnCardClickListener(cardClickListener: CardClickListener) {
        this.cardClickListener = cardClickListener
    }
    companion object {
        val PhotoDescriptionDiffCallback = object: DiffUtil.ItemCallback<PhotoDescriptionResponse>() {
            override fun areItemsTheSame(oldItem: PhotoDescriptionResponse, newItem: PhotoDescriptionResponse): Boolean {
                return oldItem.photoId == newItem.photoId
            }

            override fun areContentsTheSame(oldItem: PhotoDescriptionResponse, newItem: PhotoDescriptionResponse): Boolean {
                return oldItem.equals(newItem)
            }
        }
    }
}