package br.edu.ifal.financeproject.dados.modelo

import androidx.room.*

enum class FormaPagamento { A_VISTA, A_PRAZO }

@Entity(tableName = "gastos")
data class Gasto(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val titulo: String,
    val totalCentavos: Long,
    val data: String,
    val categoria: String,
    val forma: FormaPagamento,
    @ColumnInfo(defaultValue = "0") val jurosCentavos: Long = 0
) {
    val valor: Double get() = totalCentavos / 100.0
}

@Entity(
    tableName = "parcelas",
    primaryKeys = ["gastoId", "numero"],
    foreignKeys = [ForeignKey(entity = Gasto::class, parentColumns = ["id"],
        childColumns = ["gastoId"], onDelete = ForeignKey.CASCADE)]
)
data class Parcela(
    val gastoId: Long,
    val numero: Int,
    val centavos: Long,
    val vencimento: String,
    val dataPagamento: String? = null,
    val lembreteEnviado: Boolean = false
)

data class GastoComParcelas(
    @Embedded val gasto: Gasto,
    @Relation(parentColumn = "id", entityColumn = "gastoId") val parcelas: List<Parcela>
) {
    val pago: Boolean get() = gasto.forma == FormaPagamento.A_VISTA ||
        (parcelas.isNotEmpty() && parcelas.all { it.dataPagamento != null })

    val pagoCentavos: Long get() = if (gasto.forma == FormaPagamento.A_VISTA) gasto.totalCentavos
        else parcelas.filter { it.dataPagamento != null }.sumOf { it.centavos }
}
