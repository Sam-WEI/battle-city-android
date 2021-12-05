package com.samwdev.battlecity.core

class TankController(
    private val tankState: TankState,
    private val bulletState: BulletState,
    private val handheldControllerState: HandheldControllerState,
) : TickListener {
    private val tankId: TankId get() = tankState.playerTankId

    override fun onTick(tick: Tick) {
        val tank = tankState.getTankOrNull(tankId) ?: return
        if (tank.isSpawning) {
            return
        }
        val newDir = handheldControllerState.direction
        if (newDir != null) {
            tankState.moveTank(tankId, newDir)
        } else {
            tankState.stopTank(tankId)
        }

        if (handheldControllerState.firePressed) {
            bulletState.fire(tank)
        }
    }
}