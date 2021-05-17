//package com.mirkowu.mvm.viewbinding
//
//import android.app.Dialog
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.core.app.ComponentActivity
//import androidx.databinding.ViewDataBinding
//import androidx.fragment.app.Fragment
//import androidx.lifecycle.Lifecycle
//import androidx.lifecycle.LifecycleObserver
//import androidx.lifecycle.OnLifecycleEvent
//import androidx.viewbinding.ViewBinding
//import java.lang.reflect.ParameterizedType
//import kotlin.properties.ReadOnlyProperty
//import kotlin.reflect.KProperty
//
//inline fun <reified VB : ViewBinding> Activity.inflate() = lazy {
//    inflateBinding<VB>(layoutInflater).apply { setContentView(root) }
//}
//
//inline fun <reified VB : ViewBinding> Dialog.inflate() = lazy {
//    inflateBinding<VB>(layoutInflater).apply { setContentView(root) }
//}

//inline fun <reified VB : ViewBinding> ComponentActivity.binding() = lazy {
//    inflateBinding<VB>(layoutInflater).also {
//        setContentView(it.root)
//        if (this is ViewDataBinding) lifecycleOwner = this@binding
//    }
//}
//
//inline fun <reified VB : ViewBinding> Fragment.binding() = FragmentBindingDelegate<VB> { requireView().bind() }
//
//inline fun <reified VB : ViewBinding> Fragment.binding(method: Method) =
//        FragmentBindingDelegate<VB> { if (method == Method.BIND) requireView().bind() else inflateBinding(layoutInflater) }
//
//inline fun <reified VB : ViewBinding> Dialog.binding() = lazy {
//    inflateBinding<VB>(layoutInflater).also { setContentView(it.root) }
//}
//
//inline fun <reified VB : ViewBinding> ViewGroup.binding(attachToParent: Boolean = true) = lazy {
//    inflateBinding<VB>(LayoutInflater.from(context), if (attachToParent) this else null, attachToParent)
//}
//
////inline fun <reified VB : ViewBinding> TabLayout.Tab.setCustomView(onBindView: VB.() -> Unit) {
////    customView = inflateBinding<VB>(LayoutInflater.from(parent!!.context)).apply(onBindView).root
////}
////
////inline fun <reified VB : ViewBinding> TabLayout.Tab.bindCustomView(onBindView: VB.() -> Unit) =
////        customView?.bind<VB>()?.run(onBindView)
////
////inline fun <reified VB : ViewBinding> TabLayout.Tab.bindCustomView(bind: (View) -> VB, onBindView: VB.() -> Unit) =
////        customView?.let { bind(it).run(onBindView) }
//
//inline fun <reified VB : ViewBinding> inflateBinding(layoutInflater: LayoutInflater) =
//        VB::class.java.getMethod("inflate", LayoutInflater::class.java).invoke(null, layoutInflater) as VB
//
//inline fun <reified VB : ViewBinding> inflateBinding(parent: ViewGroup) =
//        inflateBinding<VB>(LayoutInflater.from(parent.context), parent, false)
//
//inline fun <reified VB : ViewBinding> inflateBinding(
//        layoutInflater: LayoutInflater, parent: ViewGroup?, attachToParent: Boolean
//) =
//        VB::class.java.getMethod("inflate", LayoutInflater::class.java, ViewGroup::class.java, Boolean::class.java)
//                .invoke(null, layoutInflater, parent, attachToParent) as VB
//
//inline fun <reified VB : ViewBinding> View.bind() =
//        VB::class.java.getMethod("bind", View::class.java).invoke(null, this) as VB
//
//
//inline class Method   constructor(val value: Int) {
//    companion object {
//        val BIND = Method(0)
//        val INFLATE = Method(1)
//    }
//}
//
//
//@JvmName("inflateWithGeneric")
//fun <VB : ViewBinding> Any.inflateBindingWithGeneric(layoutInflater: LayoutInflater): VB =
//        withGenericBindingClass(this) { clazz ->
//            clazz.getMethod("inflate", LayoutInflater::class.java).invoke(null, layoutInflater) as VB
//        }
//
//@JvmName("inflateWithGeneric")
//fun <VB : ViewBinding> Any.inflateBindingWithGeneric(layoutInflater: LayoutInflater, parent: ViewGroup?, attachToParent: Boolean): VB =
//        withGenericBindingClass(this) { clazz ->
//            clazz.getMethod("inflate", LayoutInflater::class.java, ViewGroup::class.java, Boolean::class.java)
//                    .invoke(null, layoutInflater, parent, attachToParent) as VB
//        }
//
//@JvmName("inflateWithGeneric")
//fun <VB : ViewBinding> Any.inflateBindingWithGeneric(parent: ViewGroup): VB =
//        inflateBindingWithGeneric(LayoutInflater.from(parent.context), parent, false)
//
//fun <VB : ViewBinding> Any.bindViewWithGeneric(view: View): VB =
//        withGenericBindingClass(this) { clazz ->
//            clazz.getMethod("bind", LayoutInflater::class.java).invoke(null, view) as VB
//        }
//
//private fun <VB : ViewBinding> withGenericBindingClass(any: Any, block: (Class<VB>) -> VB): VB {
//    any.allParameterizedType.forEach { parameterizedType ->
//        parameterizedType.actualTypeArguments.forEach {
//            try {
//                return block.invoke(it as Class<VB>)
//            } catch (e: Exception) {
//            }
//        }
//    }
//    throw IllegalArgumentException("There is no generic of ViewBinding.")
//}
//
//private val Any.allParameterizedType: List<ParameterizedType>
//    get() {
//        val genericParameterizedType = mutableListOf<ParameterizedType>()
//        var genericSuperclass = javaClass.genericSuperclass
//        var superclass = javaClass.superclass
//        while (superclass != null) {
//            if (genericSuperclass is ParameterizedType) {
//                genericParameterizedType.add(genericSuperclass)
//            }
//            genericSuperclass = superclass.genericSuperclass
//            superclass = superclass.superclass
//        }
//        return genericParameterizedType
//    }