package ir.kodato.coincap.util

import java.text.DecimalFormat

fun formatPrice(price: Double): String {
    val decimalFormat = DecimalFormat("#,##0.#####")
    val formattedPrice = decimalFormat.format(price)

    if (price < 0.001) {
        return "$$formattedPrice"
    }

    val parts = formattedPrice.split(".")
    val decimalPart = parts[1].take(3).trimEnd('0')
    val formattedDecimalPart = if (decimalPart.isEmpty()) "" else ".$decimalPart"
    return "$${parts[0]}$formattedDecimalPart"
}