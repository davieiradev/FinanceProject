package br.edu.ifal.financeproject

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tabela_dividas")
data class Divida(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val titulo : String,
    val valor: Double,
    val dataVencimento: String,
    val paga: Boolean = false
)