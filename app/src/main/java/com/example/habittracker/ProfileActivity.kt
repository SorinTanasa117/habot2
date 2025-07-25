package com.example.habittracker

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.habittracker.data.User
import com.example.habittracker.databinding.ActivityProfileBinding
import com.example.habittracker.db.FirestoreRepository
import kotlinx.coroutines.launch

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private val firestoreRepository = FirestoreRepository()
    private lateinit var user: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycleScope.launch {
            user = firestoreRepository.getUser(firestoreRepository.getCurrentUser()!!.uid)!!
            binding.name.text = user.name
            binding.email.text = user.email
            if (user.avatarUrl.isNotEmpty()) {
                Glide.with(this@ProfileActivity).load(user.avatarUrl).into(binding.avatar)
            }
        }

        val avatars = getAnimalAvatars()
        binding.avatarGrid.adapter = AvatarAdapter(this, avatars)
        binding.avatarGrid.setOnItemClickListener { _, _, position, _ ->
            lifecycleScope.launch {
                user.avatarUrl = avatars[position]
                firestoreRepository.saveUser(user)
                Glide.with(this@ProfileActivity).load(user.avatarUrl).into(binding.avatar)
            }
        }
    }

    private fun getAnimalAvatars(): List<String> {
        // In a real app, you would get these from a server or a local database
        return listOf(
            "https://i.pravatar.cc/150?img=1",
            "https://i.pravatar.cc/150?img=2",
            "https://i.pravatar.cc/150?img=3",
            "https://i.pravatar.cc/150?img=4",
            "https://i.pravatar.cc/150?img=5",
            "https://i.pravatar.cc/150?img=6",
            "https://i.pravatar.cc/150?img=7",
            "https://i.pravatar.cc/150?img=8",
            "https://i.pravatar.cc/150?img=9",
            "https://i.pravatar.cc/150?img=10",
            "https://i.pravatar.cc/150?img=11",
            "https://i.pravatar.cc/150?img=12",
            "https://i.pravatar.cc/150?img=13",
            "https://i.pravatar.cc/150?img=14",
            "https://i.pravatar.cc/150?img=15",
            "https://i.pravatar.cc/150?img=16",
            "https://i.pravatar.cc/150?img=17",
            "https://i.pravatar.cc/150?img=18",
            "https://i.pravatar.cc/150?img=19",
            "https://i.pravatar.cc/150?img=20",
            "https://i.pravatar.cc/150?img=21",
            "https://i.pravatar.cc/150?img=22",
            "https://i.pravatar.cc/150?img=23",
            "https://i.pravatar.cc/150?img=24",
            "https://i.pravatar.cc/150?img=25",
            "https://i.pravatar.cc/150?img=26",
            "https://i.pravatar.cc/150?img=27",
            "https://i.pravatar.cc/150?img=28",
            "https://i.pravatar.cc/150?img=29",
            "https://i.pravatar.cc/150?img=30",
            "https://i.pravatar.cc/150?img=31",
            "https://i.pravatar.cc/150?img=32",
            "https://i.pravatar.cc/150?img=33",
            "https://i.pravatar.cc/150?img=34",
            "https://i.pravatar.cc/150?img=35",
            "https://i.pravatar.cc/150?img=36",
            "https://i.pravatar.cc/150?img=37",
            "https://i.pravatar.cc/150?img=38",
            "https://i.pravatar.cc/150?img=39",
            "https://i.pravatar.cc.com/150?img=40",
            "https://i.pravatar.cc/150?img=41",
            "https://i.pravatar.cc/150?img=42",
            "https://i.pravatar.cc/150?img=43",
            "https://i.pravatar.cc/150?img=44",
            "https://i.pravatar.cc/150?img=45",
            "https://i.pravatar.cc/150?img=46",
            "https://i.pravatar.cc/150?img=47",
            "https://i.pravatar.cc/150?img=48",
            "https://i.pravatar.cc/150?img=49",
            "https://i.pravatar.cc/150?img=50"
        )
    }

    class AvatarAdapter(private val context: Context, private val avatars: List<String>) : BaseAdapter() {
        override fun getCount() = avatars.size
        override fun getItem(position: Int) = avatars[position]
        override fun getItemId(position: Int) = position.toLong()
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val imageView = if (convertView == null) {
                ImageView(context).apply {
                    layoutParams = ViewGroup.LayoutParams(150, 150)
                    scaleType = ImageView.ScaleType.CENTER_CROP
                }
            } else {
                convertView as ImageView
            }
            Glide.with(context).load(avatars[position]).into(imageView)
            return imageView
        }
    }
}
