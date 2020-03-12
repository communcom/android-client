package io.golos.cyber_android.ui.screens.wallet_point.view.prime_panel

import androidx.databinding.BindingAdapter
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import io.golos.cyber_android.ui.screens.wallet_point.dto.CarouselStartData
import io.golos.cyber_android.ui.shared.extensions.parentActivity

@BindingAdapter("wallet_point_prime_panel_title")
fun setWalletPointPrimePanelTitleDateBinding(view: WalletPointPrimePanelView, valueToBind: LiveData<String>?) {
    valueToBind?.let { liveValue ->
        view.parentActivity?.let { activity ->
            liveValue.observe(activity, Observer { view.setTitle(it) })
        }
    }
}

@BindingAdapter("wallet_point_prime_panel_value")
fun setWalletPointPrimePanelValueDateBinding(view: WalletPointPrimePanelView, valueToBind: LiveData<Double>?) {
    valueToBind?.let { liveValue ->
        view.parentActivity?.let { activity ->
            liveValue.observe(activity, Observer { view.setValue(it) })
        }
    }
}

@BindingAdapter("wallet_point_prime_panel_value_communs")
fun setWalletPointPrimePanelValueInCommunsDateBinding(view: WalletPointPrimePanelView, valueToBind: LiveData<Double>?) {
    valueToBind?.let { liveValue ->
        view.parentActivity?.let { activity ->
            liveValue.observe(activity, Observer { view.setValueCommun(it) })
        }
    }
}

@BindingAdapter("wallet_point_prime_panel_available_hold_bar")
fun setWalletPointPrimePanelAvailableHoldBarDateBinding(view: WalletPointPrimePanelView, valueToBind: LiveData<Double>?) {
    valueToBind?.let { liveValue ->
        view.parentActivity?.let { activity ->
            liveValue.observe(activity, Observer { view.setAvailableHoldBarValue(it) })
        }
    }
}

@BindingAdapter("wallet_point_prime_panel_available")
fun setWalletPointPrimePanelAvailableBinding(view: WalletPointPrimePanelView, valueToBind: LiveData<Double>?) {
    valueToBind?.let { liveValue ->
        view.parentActivity?.let { activity ->
            liveValue.observe(activity, Observer { view.setAvailableValue(it) })
        }
    }
}

@BindingAdapter("wallet_point_prime_panel_hold")
fun setWalletPointPrimePaneHoldBinding(view: WalletPointPrimePanelView, valueToBind: LiveData<Double>?) {
    valueToBind?.let { liveValue ->
        view.parentActivity?.let { activity ->
            liveValue.observe(activity, Observer { view.setHoldValue(it) })
        }
    }
}

@BindingAdapter("wallet_point_prime_panel_carousel_start")
fun setWalletPointPrimePanelCarouselStartBinding(view: WalletPointPrimePanelView, valueToBind: LiveData<CarouselStartData>?) {
    valueToBind?.let { liveValue ->
        view.parentActivity?.let { activity ->
            liveValue.observe(activity, Observer { view.setCarouselStartData(it) })
        }
    }
}