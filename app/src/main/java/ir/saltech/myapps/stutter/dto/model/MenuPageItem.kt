package ir.saltech.myapps.stutter.dto.model

data class MenuPageItem(val iconResId: Int, val title: String, val enabled: Boolean = true, val disabledReason: String? = null, val comingSoon: Boolean = false, val onClick: () -> Unit)
