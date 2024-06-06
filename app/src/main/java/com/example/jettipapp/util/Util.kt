package com.example.jettipapp.util

fun calculateTotalTip(totalBillString: String, tipPercentage: Int): Double {
    if(totalBillString.isEmpty()) {
        return 0.0
    }

    val totalBill = cleanValueString(totalBillString).toDouble()

    return if(totalBill > 1) {
        (totalBill * tipPercentage) / 100
    } else {
        0.0
    }
}

fun calculateTotalPerPerson(totalBillString: String, splitBy: Int, tipPercentage: Int): Double {
    if(totalBillString.isEmpty()) {
        return 0.0
    }

    val bill = calculateTotalTip(totalBillString, tipPercentage) + cleanValueString(totalBillString).toDouble()

    return bill / splitBy
}

fun cleanValueString(value: String): String {
    return when {
        value.indexOf(",") == value.length - 1 -> value.replace(",", "")
        value.indexOf(".") == value.length - 1 -> value.replace(".", "")
        value.contains(",") -> value.replace(",", ".")
        else -> value
    }
}