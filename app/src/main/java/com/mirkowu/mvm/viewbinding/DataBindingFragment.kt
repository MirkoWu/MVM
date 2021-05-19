package com.mirkowu.mvm.viewbinding

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.mirkowu.mvm.R
import com.mirkowu.mvm.base.BaseFragment
import com.mirkowu.mvm.databinding.FragmentDatabindingBinding
import com.mirkowu.mvm.mvvm.MVVMMediator

class DataBindingFragment : BaseFragment<MVVMMediator>() {
    companion object {

        val TAG = "DataBindingFragment"
        fun newInstance(): DataBindingFragment {
            val args = Bundle()

            val fragment = DataBindingFragment()
            fragment.arguments = args
            return fragment
        }
    }
//    val binding  :FragmentDatabindingBinding  by binding()

    val binding by binding { FragmentDatabindingBinding.bind(view!!) }

//    var _binding: FragmentDatabindingBinding? = null
//    val binding get() = _binding!!

    override fun getLayoutId() = R.layout.fragment_databinding
    override fun initialize() {
        binding.btnText.text = "这是fragment databinding"
        binding.btnText.setOnClickListener {
            binding.btnText.text = "点击了${System.currentTimeMillis()}"
            DataBindingDialog(context!!).show()
            showLoadingDialog("Toast测试")
        }
        val list = mutableListOf("", "", "", "", "", "", "", "", "", "")
        val listAdapter = DataBindingAdapter(list)
        binding.rvList.apply {
            adapter = listAdapter
            layoutManager = LinearLayoutManager(context)
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate: $position")
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d(TAG, "onCreateView: $position")
        return super.onCreateView(inflater, container, savedInstanceState)

//        _binding = FragmentDatabindingBinding.inflate(inflater, container, false)
//        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(TAG, "onViewCreated: $position")
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        Log.d(TAG, "onActivityCreated: $position")
        super.onActivityCreated(savedInstanceState)
    }

    override fun onAttach(context: Context) {
        Log.d(TAG, "onAttach: $position")
        super.onAttach(context)
    }

    override fun onAttachFragment(childFragment: Fragment) {
        Log.d(TAG, "onAttachFragment: $position")
        super.onAttachFragment(childFragment)
    }

    override fun onResume() {
        Log.d(TAG, "onResume: $position")
        super.onResume()
    }

    override fun onPause() {
        Log.d(TAG, "onPause: $position")
        super.onPause()
    }

    override fun onStop() {
        Log.d(TAG, "onStop: $position")
        super.onStop()
    }
    override fun onDestroyView() {
        Log.d(TAG, "onDestroyView: $position")
//        _binding = null
        super.onDestroyView()
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy: $position")
        super.onDestroy()
    }

    override fun onDetach() {
        Log.d(TAG, "onDetach: $position")
        super.onDetach()
    }
}