package com.samwdev.battlecity.core

import androidx.lifecycle.ViewModel

class Store {

}

interface Reducer<S, A : Action> {

}

sealed class Action(open val type: String)

data class StartGame(override val type: String) : Action(type)