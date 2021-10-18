package com.samwdev.battlecity.core

import androidx.lifecycle.ViewModel

class Store {

}

interface Reducer<S, A : Action> {

}

sealed class Action

object StartGame : Action()