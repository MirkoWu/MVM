package com.mirkowu.mvm.viewbinding

import android.app.Activity
import android.app.Dialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.viewbinding.ViewBinding
//import com.google.android.material.tabs.TabLayout
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * @author Dylan Cai https://github.com/DylanCaiCoding/ViewBindingKTX
 */
inline fun <reified VB : ViewBinding> Activity.inflate() = lazy {
    inflateBinding<VB>(layoutInflater).apply { setContentView(root) }
}

inline fun <reified VB : ViewBinding> Dialog.inflate() = lazy {
    inflateBinding<VB>(layoutInflater).apply { setContentView(root) }
}


fun <VB : ViewBinding> ComponentActivity.binding(inflate: (LayoutInflater) -> VB) = lazy {
    inflate(layoutInflater).also {
        setContentView(it.root)
        if (this is ViewDataBinding) lifecycleOwner = this@binding
    }
}

fun <VB : ViewBinding> Fragment.binding(bind: (View) -> VB) =
        FragmentBindingDelegate(bind)

fun <VB : ViewBinding> Dialog.binding(inflate: (LayoutInflater) -> VB) = lazy {
    inflate(layoutInflater).also { setContentView(it.root) }
}

fun <VB : ViewBinding> ViewGroup.binding(
        inflate: (LayoutInflater, ViewGroup?, Boolean) -> VB,
        attachToParent: Boolean = true
) = lazy {
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

    @Suppress("UNCHECKED_CAST")
    override fun getValue(thisRef: Fragment, property: KProperty<*>): VB {
        if (binding == null) {
            binding = bind(thisRef.requireView()).also {
                if (it is ViewDataBinding) it.lifecycleOwner = thisRef.viewLifecycleOwner
            }
            thisRef.viewLifecycleOwner.lifecycle.addObserver(object : LifecycleObserver {
                @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
                fun onDestroyView() {
                    binding = null
                }
            })
        }
        return binding!!
    }
}