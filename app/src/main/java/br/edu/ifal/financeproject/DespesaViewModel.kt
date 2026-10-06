package br.edu.ifal.financeproject

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DespesaViewModel (application : Application) : AndroidViewModel(application){

    private val dao = AppDataBase.getDatabase(application).despesaDao()

    fun adicionarDespesa(titulo : String, valor: Double, data: String){
        viewModelScope.launch(Dispatchers.IO){
            val novaDespesa = Despesa(titulo = titulo, valor = valor, data = data)
            dao.inserir(novaDespesa)
        }
    }
}