package br.edu.ifal.financeproject.dados.modelo

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "dicas_financeiras")
data class DicaFinanceira(
    @PrimaryKey val id: Int,
    val titulo: String,
    val categoria: String,
    val descricao: String
)
