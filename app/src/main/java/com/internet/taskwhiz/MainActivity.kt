package com.internet.taskwhiz

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
// view binding import
import com.internet.taskwhiz.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var count = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // view binding setup code insertion. I hope I remembered everything,
        // because it's been a while since I've done this.
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        enableEdgeToEdge()
        setContentView(view)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Set up the listener for the button to know when it's clicked
        binding.button.setOnClickListener{
            if (binding.button.text.equals("Restart!")){
                // If the user pressed the restart button before, then we reset
                // the count to be 0 so that the next time they short press the button,
                // the count gets reset to "1" when the button text is updated.
                count = 0
            }
            binding.button.text = (++count).toString()
        }

        binding.button.setOnLongClickListener{
            binding.button.text = "Restart!"
            // If we return true, then we let Kotlin know that the long press action
            // has been handled. If we return false, then we let Kotlin know that
            // the long press action hasn't been handled, and so it will look for the
            // next thing to handle it, which is usually the normal onClickListener
            // you set up.
            true
        }


    }


}