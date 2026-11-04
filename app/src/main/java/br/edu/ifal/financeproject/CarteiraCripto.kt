package br.edu.ifal.financeproject

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tabela_carteira_cripto")
data class CarteiraCripto(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val simbolo: String,
    val nome: String,
    val quantidade: Double,
    val precoMedioCompra: Double,
    val cotacaoReferencia: Double
)