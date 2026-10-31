package com.example.model

enum class UnitCategory(val displayName: String, val iconName: String) {
    LENGTH("Length", "Straighten"),
    WEIGHT("Weight", "FitnessCenter"),
    TEMPERATURE("Temperature", "Thermostat"),
    SPEED("Speed", "Speed"),
    DATA("Data Storage", "Memory"),
    AREA("Area", "SquareFoot")
}

data class UnitItem(
    val name: String,
    val symbol: String,
    // Base conversion factor relative to category's standard base unit
    val toBaseFactor: Double = 1.0
)

object UnitConverterData {
    val unitsByCategory: Map<UnitCategory, List<UnitItem>> = mapOf(
        UnitCategory.LENGTH to listOf(
            UnitItem("Meter", "m", 1.0),
            UnitItem("Kilometer", "km", 1000.0),
            UnitItem("Centimeter", "cm", 0.01),
            UnitItem("Millimeter", "mm", 0.001),
            UnitItem("Mile", "mi", 1609.344),
            UnitItem("Yard", "yd", 0.9144),
            UnitItem("Foot", "ft", 0.3048),
            UnitItem("Inch", "in", 0.0254)
        ),
        UnitCategory.WEIGHT to listOf(
            UnitItem("Kilogram", "kg", 1.0),
            UnitItem("Gram", "g", 0.001),
            UnitItem("Milligram", "mg", 0.000001),
            UnitItem("Metric Ton", "t", 1000.0),
            UnitItem("Pound", "lb", 0.45359237),
            UnitItem("Ounce", "oz", 0.028349523)
        ),
        UnitCategory.TEMPERATURE to listOf(
            UnitItem("Celsius", "°C", 1.0),
            UnitItem("Fahrenheit", "°F", 1.0),
            UnitItem("Kelvin", "K", 1.0)
        ),
        UnitCategory.SPEED to listOf(
            UnitItem("Kilometers/hour", "km/h", 1.0),
            UnitItem("Miles/hour", "mph", 1.60934),
            UnitItem("Meters/second", "m/s", 3.6),
            UnitItem("Knot", "kn", 1.852)
        ),
        UnitCategory.DATA to listOf(
            UnitItem("Byte", "B", 1.0),
            UnitItem("Kilobyte", "KB", 1024.0),
            UnitItem("Megabyte", "MB", 1024.0 * 1024.0),
            UnitItem("Gigabyte", "GB", 1024.0 * 1024.0 * 1024.0),
            UnitItem("Terabyte", "TB", 1024.0 * 1024.0 * 1024.0 * 1024.0)
        ),
        UnitCategory.AREA to listOf(
            UnitItem("Square Meter", "m²", 1.0),
            UnitItem("Square Kilometer", "km²", 1_000_000.0),
            UnitItem("Square Foot", "ft²", 0.092903),
            UnitItem("Acre", "ac", 4046.86),
            UnitItem("Hectare", "ha", 10_000.0)
        )
    )

    fun convert(value: Double, fromSymbol: String, toSymbol: String, category: UnitCategory): Double {
        if (fromSymbol == toSymbol) return value

        if (category == UnitCategory.TEMPERATURE) {
            // Temperature special conversion
            val celsius = when (fromSymbol) {
                "°C" -> value
                "°F" -> (value - 32.0) * 5.0 / 9.0
                "K" -> value - 273.15
                else -> value
            }
            return when (toSymbol) {
                "°C" -> celsius
                "°F" -> (celsius * 9.0 / 5.0) + 32.0
                "K" -> celsius + 273.15
                else -> celsius
            }
        }

        val units = unitsByCategory[category] ?: return value
        val fromUnit = units.find { it.symbol == fromSymbol } ?: return value
        val toUnit = units.find { it.symbol == toSymbol } ?: return value

        val baseValue = value * fromUnit.toBaseFactor
        return baseValue / toUnit.toBaseFactor
    }
}
