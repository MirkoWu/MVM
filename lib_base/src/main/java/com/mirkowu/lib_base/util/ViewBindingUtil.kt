package com.mirkowu.lib_base.util

import android.app.Dialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.viewbinding.ViewBinding
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * @author Dylan Cai https://github.com/DylanCaiCoding/ViewBindingKTX
 */


/**
 * 绑定Activity  eg.
 * val binding by binding(ActivityMainBinding::inflate)
 */
inline fun <VB : ViewBinding> ComponentActivity.bindingView(crossinline inflate: (LayoutInflater) -> VB) = lazy {
    inflate(layoutInflater).also {
        setContentView(it.root)
        /*if (this is ViewDataBinding) {
            Log.d("TAG", "binding: this is ViewDataBinding" )
            lifecycleOwner = this@binding
        }*/
    }
}

/**
 * 绑定Fragment  eg.
 * val binding by binding { FragmentHomeBinding.bind(view!!) }
 */
inline  fun <VB : ViewBinding> Fragment.bindingView(crossinline bind: (View) -> VB) = lazy {
    bind(requireView())
}

/**
 * 绑定Dialog  eg.
 * val binding by binding(DialogHintBinding::inflate)
 */
inline fun <VB : ViewBinding> Dialog.bindingView(crossinline inflate: (LayoutInflater) -> VB) = lazy {
    inflate(layoutInflater).also { setContentView(it.root) }
}

/**
 * 绑定ViewGroup
 */
fun <VB : ViewBinding> ViewGroup.bindingView(inflate: (LayoutInflater, ViewGroup?, Boolean) -> VB,
                                             attachToParent: Boolean = true) = lazy {
    inflate(LayoutInflater.from(context), if (attachToParent) this else null, attachToParent)
}

//fun <VB : ViewBinding> TabLayout.Tab.setCustomView(
//        inflate: (LayoutInflater) -> VB,
//        onBindView: VB.() -> Unit
//) {
//    customView = inflate(LayoutInflater.from(parent!!.context)).apply(onBindView).root
//}
//
//inline fun <reified VB : ViewBinding> TabLayout.Tab.bindCustomView(bind: (View) -> VB, onBindView: VB.() -> Unit) =
//        customView?.let { bind(it).run(onBindView) }

class FragmentBindingDelegate<VB : ViewBinding>(
    private val bind: (View) -> VB
) : ReadOnlyProperty<Fragment, VB> {

    private var binding: VB? = null

    override fun getValue(fragment: Fragment, property: KProperty<*>): VB {
        if (binding == null) {
            binding = bind(fragment.requireView())/*.also {
                if (it is ViewDataBinding) {
                    it.lifecycleOwner = thisRef.viewLifecycleOwner
                }
            }*/
            fragment.viewLifecycleOwner.lifecycle.addObserver(object : LifecycleObserver {
                @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
                fun onDestroyView() {
                    binding = null
                }
            })
        }
        return binding!!
    }
}