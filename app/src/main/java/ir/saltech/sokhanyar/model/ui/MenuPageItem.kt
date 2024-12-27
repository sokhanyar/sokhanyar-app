package ir.saltech.sokhanyar.model.ui

data class MenuPageItem(
    val iconResId: Int,
    val title: String,
    val disabledReason: String? = null,
    val comingSoon: Boolean = false,
    val onClick: () -> Unit
)
