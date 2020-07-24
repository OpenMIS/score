package com.game.score.ui.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
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

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        // TODO: Use the ViewModel
        binding.data = viewModel
        binding.lifecycleOwner = this
    }

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
            }
        }

        if (viewModel.scoreString.value == ".")
            viewModel.scoreString.value = "0."

        Snackbar.make(it, viewModel.scoreString.value.toString(), Snackbar.LENGTH_LONG)
            .setAction("Action", null).show()

    }
}