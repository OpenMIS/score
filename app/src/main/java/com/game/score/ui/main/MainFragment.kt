package com.game.score.ui.main

import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.game.score.R
import com.game.score.databinding.MainFragmentBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.main_fragment.view.*

class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var viewModel: MainViewModel
    private lateinit var binding: MainFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = MainFragmentBinding.inflate(inflater, container, false)

        //region 挂接按钮事件
        binding.button0.setOnClickListener(_buttonListener)
        binding.button1.setOnClickListener(_buttonListener)
        binding.button2.setOnClickListener(_buttonListener)
        binding.button3.setOnClickListener(_buttonListener)
        binding.button4.setOnClickListener(_buttonListener)
        binding.button5.setOnClickListener(_buttonListener)
        binding.button6.setOnClickListener(_buttonListener)
        binding.button7.setOnClickListener(_buttonListener)
        binding.button8.setOnClickListener(_buttonListener)
        binding.button9.setOnClickListener(_buttonListener)
        binding.buttonDot.setOnClickListener(_buttonListener)
        binding.buttonX.setOnClickListener(_buttonListener)
        binding.buttonV.setOnClickListener(_buttonListener)
        binding.buttonSend.setOnClickListener(_buttonListener)
        //endregion

        binding.textViewDeviceCode.setOnLongClickListener {
            val controller = Navigation.findNavController(it)
            controller.navigate(R.id.settingsFragment)

            return@setOnLongClickListener true
        }

        return binding.root
    }
    
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        // TODO: Use the ViewModel
        binding.data = viewModel
        binding.lifecycleOwner = this

        init()
    }

    private fun init() {
        with(viewModel){
            //临时测试数据
            gameMatch.value = "盛装舞步个人赛资格赛"
            athleteNameAndTeamName.value = "贾海涛(浙江队)"
            deviceCode.value = "E"
            matchStep.value = "1 立定敬礼"
        }
    }

    /**
     * 按钮事件
     */
    private val _buttonListener = View.OnClickListener {
        var numberString = String()
        when (it.id) {
            R.id.button0 -> numberString = "0"
            R.id.button1 -> numberString = "1"
            R.id.button2 -> numberString = "2"
            R.id.button3 -> numberString = "3"
            R.id.button4 -> numberString = "4"
            R.id.button5 -> numberString = "5"
            R.id.button6 -> numberString = "6"
            R.id.button7 -> numberString = "7"
            R.id.button8 -> numberString = "8"
            R.id.button9 -> numberString = "9"
            R.id.button_dot -> numberString = "."
        }

        if (!numberString.isBlank()) {
            if (viewModel.scoreString.value.isNullOrBlank() ||
                viewModel.scoreString.value.toString().length <= 3
            )
                viewModel.scoreString.value += numberString
            else
                viewModel.scoreString.value = numberString
        } else {
            when (it.id) {
                R.id.button_X -> viewModel.scoreString.value = ""
                R.id.button_send -> {
                    val batteryStatus: Intent? =
                        IntentFilter(Intent.ACTION_BATTERY_CHANGED).let { ifilter ->
                            context?.registerReceiver(null, ifilter)
                        }

                    if (batteryStatus != null) {
                        val level = batteryStatus.getIntExtra("level", 0)
                        Toast.makeText(context, "剩余电量:$level%", Toast.LENGTH_SHORT).show()
                    }
                }
                R.id.button_V -> {
                }
            }
        }

        if (viewModel.scoreString.value == ".")
            viewModel.scoreString.value = "0."
    }
}