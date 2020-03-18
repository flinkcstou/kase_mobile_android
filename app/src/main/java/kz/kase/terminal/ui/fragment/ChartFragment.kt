package kz.kase.terminal.ui.fragment

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class ChartFragment: Fragment(){

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        Log.d("Fragment", "New copy fragment "+ this.javaClass.simpleName)
        val binding = kz.kase.terminal.databinding.FragmentChartBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
    override fun onDestroyView() {
        super.onDestroyView()
    }

    override fun onResume() {
        super.onResume()
    }
}