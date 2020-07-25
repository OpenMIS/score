package com.game.score.ui.main

import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.game.score.R
import com.game.score.databinding.MainFragmentBinding

class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    //region 字段
    /**
     * 视图模型
     */
    private lateinit var _viewModel: MainViewModel

    /**
     * 数据绑定
     */
    private lateinit var _binding: MainFragmentBinding
    //endregion

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = MainFragmentBinding.inflate(inflater, container, false)

        //region 挂接按钮事件
        _binding.button0.setOnClickListener(_buttonListener)
        _binding.button1.setOnClickListener(_buttonListener)
        _binding.button2.setOnClickListener(_buttonListener)
        _binding.button3.setOnClickListener(_buttonListener)
        _binding.button4.setOnClickListener(_buttonListener)
        _binding.button5.setOnClickListener(_buttonListener)
        _binding.button6.setOnClickListener(_buttonListener)
        _binding.button7.setOnClickListener(_buttonListener)
        _binding.button8.setOnClickListener(_buttonListener)
        _binding.button9.setOnClickListener(_buttonListener)
        _binding.buttonDot.setOnClickListener(_buttonListener)
        _binding.imageButtonX.setOnClickListener(_buttonListener)
        _binding.buttonV.setOnClickListener(_buttonListener)
        _binding.buttonSend.setOnClickListener(_buttonListener)
        //endregion

        //region 长按“设备代码”打开“设置”页面
        _binding.textViewDeviceCode.setOnLongClickListener {
            val controller = Navigation.findNavController(it)
            controller.navigate(R.id.settingsFragment)

            return@setOnLongClickListener true
        }
        //endregion

        return _binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        //region 数据版本与数据模型关联
        _viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        _binding.data = _viewModel
        _binding.lifecycleOwner = this
        //endregion

        init()
    }

    private fun init() {
        with(_viewModel) {
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
            if (_viewModel.scoreString.value.isNullOrBlank() ||
                _viewModel.scoreString.value.toString().length <= 3
            )
                _viewModel.scoreString.value += numberString
            else
                _viewModel.scoreString.value = numberString
        } else {
            when (it.id) {
                R.id.imageButton_X -> {
                    if (!_viewModel.scoreString.value.isNullOrEmpty()) {
                        val temp = _viewModel.scoreString.value.toString()
                        _viewModel.scoreString.value = temp.substring(
                            0, temp.length - 1
                        )
                    }
                }
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

        if (_viewModel.scoreString.value == ".")
            _viewModel.scoreString.value = "0."
    }
}