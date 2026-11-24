package br.edu.ifal.financeproject

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "progresso_licoes")
data class ProgressoLicao(
    @PrimaryKey val licaoId: Int,
    val concluida: Boolean = true
)
