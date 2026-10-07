package br.edu.ifal.financeproject
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tabela_despesas")
data class Despesa (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val titulo: String,
    val valor: Double,
    val data: String,
    val categoria: String = "Outros"
)