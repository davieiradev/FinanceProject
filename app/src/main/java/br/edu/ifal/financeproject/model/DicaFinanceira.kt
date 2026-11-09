package br.edu.ifal.financeproject.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tabela_dicas_financeiras")
data class DicaFinanceira(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val titulo: String,
    val categoria: String,
    val descricao: String
)
