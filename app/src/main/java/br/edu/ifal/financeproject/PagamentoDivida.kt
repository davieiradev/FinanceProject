package br.edu.ifal.financeproject

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "tabela_pagamentos",
    foreignKeys = [
        ForeignKey(
            entity = Divida::class,
            parentColumns = ["id"],
            childColumns = ["dividaId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["dividaId"])]
)
data class PagamentoDivida(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val dividaId: Int,
    val valor: Double,
    val dataPagamento: String? = null
)
